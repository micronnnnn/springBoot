package MVVMDessert.demo.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;

public class dessertOrderItem implements Serializable {

	static dessertDAO dessertDAO;
	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		dessertDAO = context.getBean(dessertDAO.class);
	}

	private Integer dessert_id;
	private Integer dessert_order_id;
	private Integer amount;
	private Integer dessert_price;
	private LocalDateTime dessert_deadline;

	public Integer getDessert_id() {
		return dessert_id;
	}

	@Override
	public String toString() {
		return "甜點單價=" + dessert_price + ", 甜點數量=" + amount + ", 甜點賞味期限為=" + dessert_deadline;
	}

	public void setDessert_id(Integer dessert_id) {
		this.dessert_id = dessert_id;
	}

	public Integer getDessert_order_id() {
		return dessert_order_id;
	}

	public void setDessert_order_id(Integer dessert_order_id) {
		this.dessert_order_id = dessert_order_id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getDessert_price() {
		return dessert_price;
	}

	public void setDessert_price(Integer dessert_price) {
		this.dessert_price = dessert_price;
	}

	public LocalDateTime getDessert_deadline() {
		return dessert_deadline;
	}

	public void setDessert_deadline(LocalDateTime dessert_preverve_time) {
		this.dessert_deadline = dessert_preverve_time;
	}

	public MVVMDessert.demo.model.dessert getDessert(String id) throws SQLException {
		MVVMDessert.demo.model.dessert oneDessert = new MVVMDessert.demo.model.dessert();
		oneDessert = dessertDAO.getOneDessert(id);
		return oneDessert;
	}

}
