package MVVMDessert.demo.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
			e.printStackTrace();
		}
		Map<dessert, Integer> currentSolution = getRandomInitialSolution(allDesserts, budget);
		Map<dessert, Integer> bestSolution = new HashMap<>(currentSolution);

		Set<String> tabuList = new LinkedHashSet<>();
		int maxIterations = 1000;

		for (int i = 0; i < maxIterations; i++) {
			List<Map<dessert, Integer>> neighbors = generateNeighbors(currentSolution, allDesserts, budget);
			Map<dessert, Integer> bestNeighbor = null;

			for (Map<dessert, Integer> neighbor : neighbors) {
				String key = generateKey(neighbor);
				if (!tabuList.contains(key) && getUniqueDessertCount(neighbor) > getUniqueDessertCount(bestSolution)) {
					bestNeighbor = neighbor;
					break;
				}
			}

			if (bestNeighbor != null) {
				currentSolution = bestNeighbor;
				if (getUniqueDessertCount(bestNeighbor) > getUniqueDessertCount(bestSolution)) {
					bestSolution = bestNeighbor;
				}
				tabuList.add(generateKey(currentSolution));
				if (tabuList.size() > 10) {
					tabuList.remove(tabuList.iterator().next());
				}
			}
		}

		return convertToDessertList(bestSolution);
	}

	private Map<dessert, Integer> getRandomInitialSolution(List<dessert> allDesserts, int budget) {
		Map<dessert, Integer> solution = new HashMap<>();
		Collections.shuffle(allDesserts);
		int total = 0;
		for (dessert d : allDesserts) {
			int price = d.getDessert_price();
			if (total + price <= budget) {
				solution.put(d, 1); // 每種先選一次
				total += price;
			}
		}
		return solution;
	}

	private List<Map<dessert, Integer>> generateNeighbors(Map<dessert, Integer> current, List<dessert> allDesserts,
			int budget) {
		List<Map<dessert, Integer>> neighbors = new ArrayList<>();

		for (dessert d : allDesserts) {
			Map<dessert, Integer> neighbor = new HashMap<>(current);
			int count = neighbor.getOrDefault(d, 0);
			neighbor.put(d, count + 1);
			if (getTotalPrice(neighbor) <= budget) {
				neighbors.add(neighbor);
			}
		}

		return neighbors;
	}

	private int getTotalPrice(Map<dessert, Integer> map) {
		return map.entrySet().stream().mapToInt(e -> e.getKey().getDessert_price() * e.getValue()).sum();
	}

	private int getUniqueDessertCount(Map<dessert, Integer> map) {
		return map.size();
	}

	private String generateKey(Map<dessert, Integer> map) {
		return map.entrySet().stream().map(e -> e.getKey().getDessert_id() + ":" + e.getValue()).sorted()
				.collect(Collectors.joining(","));
	}

	private List<dessert> convertToDessertList(Map<dessert, Integer> map) {
		List<dessert> result = new ArrayList<>();
		for (Map.Entry<dessert, Integer> entry : map.entrySet()) {
			dessert d = entry.getKey();
			d.setSelectedAmount(entry.getValue()); // 用來顯示選幾個
			result.add(d);
		}
		return result;
	}

	public List<dessert> selectDessertsByBudget(int budget) {
		List<dessert> allDesserts = new ArrayList<>();
		try {
			allDesserts = dessertDao.getAllDessertObjectList();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int[] dp = new int[budget + 1]; // dp[b]：代表預算為 b 時的最大總價值
		int[] track = new int[budget + 1]; // track[b]：代表預算 b 最後選的是哪個甜點
		Arrays.fill(track, -1);

		for (int b = 0; b <= budget; b++) { // ✅ b 可以等於 budget
			for (int i = 0; i < allDesserts.size(); i++) {
				dessert d = allDesserts.get(i);
				int price = d.getDessert_price();
				if (price <= b && dp[b - price] + price > dp[b]) {
					if (canSelectMore(d, b - price, track, allDesserts)) {
						dp[b] = dp[b - price] + price;
						track[b] = i;
					}
				}
			}
		}

		// 找到最大總價值對應的預算點（可等於 budget）
		int bestBudgetUsed = 0;
		for (int i = 0; i <= budget; i++) {
			if (dp[i] > dp[bestBudgetUsed])
				bestBudgetUsed = i;
		}

		// 回推選擇的甜點
		int b = bestBudgetUsed;
		while (b > 0 && track[b] != -1) {
			int idx = track[b];
			dessert d = allDesserts.get(idx);
			d.setSelectedAmount(d.getSelectedAmount() + 1);
			b -= d.getDessert_price();
		}

		// 回傳被選擇的甜點（selectedAmount > 0）
		return allDesserts.stream().filter(d -> d.getSelectedAmount() > 0).collect(Collectors.toList());
	}

	private boolean canSelectMore(dessert d, int prevBudget, int[] track, List<dessert> allDesserts) {
		int count = 0;
		int b = prevBudget;
		int targetIndex = allDesserts.indexOf(d);

		while (b > 0 && track[b] != -1) {
			if (track[b] == targetIndex)
				count++;
			b -= allDesserts.get(track[b]).getDessert_price();
		}

		return count < d.getDessert_amount();
	}

}
