package MVVMDessert.demo.service;

import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.model.dessert;

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

}
