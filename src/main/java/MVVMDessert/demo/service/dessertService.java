package MVVMDessert.demo.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.model.dessert;

@Service
public class dessertService {
	private static dessertDAO dessertDao;

	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		dessertDao = context.getBean(dessertDAO.class);
	}

	public String modify(dessert dessert) throws SQLException {
		return dessertDao.modifyDessert(dessert);
	}

	public dessert getDessert(String id) throws SQLException {
		return dessertDao.getOneDessert(id);
	}

	public List<dessert> findBestCombinationByTabuSearch(int budget) {
		List<dessert> allDesserts = new ArrayList<>();
		try {
			allDesserts = dessertDao.getAllDessertObjectList();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化起始解（例如亂選一堆甜點）
		List<dessert> currentSolution = getRandomInitialSolution(allDesserts, budget);
		List<dessert> bestSolution = new ArrayList<>(currentSolution);

		Set<String> tabuList = new LinkedHashSet<>();
		int maxIterations = 1000;

		for (int i = 0; i < maxIterations; i++) {
			List<List<dessert>> neighbors = generateNeighbors(currentSolution, allDesserts, budget);
			List<dessert> bestNeighbor = null;

			for (List<dessert> neighbor : neighbors) {
				if (!tabuList.contains(generateKey(neighbor))) {
					if (getTotalAmount(neighbor) > getTotalAmount(bestSolution)) {
						bestNeighbor = neighbor;
						break;
					}
				}
			}

			if (bestNeighbor != null) {
				currentSolution = bestNeighbor;
				if (getTotalAmount(bestNeighbor) > getTotalAmount(bestSolution)) {
					bestSolution = bestNeighbor;
				}
				tabuList.add(generateKey(currentSolution));
				if (tabuList.size() > 10) {
					tabuList.remove(tabuList.iterator().next()); // 保持Tabu List長度
				}
			}
		}

		return bestSolution;
	}

	private List<dessert> getRandomInitialSolution(List<dessert> allDesserts, int budget) {
		List<dessert> solution = new ArrayList<>();
		Collections.shuffle(allDesserts);
		int totalCost = 0;
		for (dessert d : allDesserts) {
			if (totalCost + d.getDessert_price() <= budget) {
				solution.add(d);
				totalCost += d.getDessert_price();
			} else {
				break;
			}
		}
		System.out.println("solution is " + solution.size());
		return solution;
	}

	private List<List<dessert>> generateNeighbors(List<dessert> current, List<dessert> all, int budget) {
		List<List<dessert>> neighbors = new ArrayList<>();
		for (dessert d : all) {
			if (!current.contains(d)) {
				List<dessert> neighbor = new ArrayList<>(current);
				neighbor.add(d);
				if (getTotalPrice(neighbor) <= budget) {
					neighbors.add(neighbor);
				}
			}
		}
		return neighbors;
	}

	private int getTotalAmount(List<dessert> list) {
		return list.stream().mapToInt(d -> d.getDessert_amount()).sum();
	}

	private int getTotalPrice(List<dessert> list) {
		return list.stream().mapToInt(d -> d.getDessert_price()).sum();
	}

	private String generateKey(List<dessert> list) {
		return list.stream().map(d -> d.getDessert_id().toString()).sorted().collect(Collectors.joining(","));
	}

}
