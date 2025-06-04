package MVVMDessert.demo.model;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;

public class dessertOrder {

	private Integer dessert_order_id;
	private Timestamp order_data;
	private Integer total;

	@NotBlank(message = "訂購者名稱:請勿空白")
	private String customer_name;
	@NotBlank(message = "訂購者名稱:請勿空白")
	private String customer_phone;
	@NotBlank(message = "訂購者名稱:請勿空白")
	private String customer_email;
	@NotBlank(message = "訂購者名稱:請勿空白")
	private String customer_address;

	public dessertOrder() {

	}

	public dessertOrder(String customer_name, String customer_phone, String customer_email, String customer_address) {
		super();
		this.customer_name = customer_name;
		this.customer_phone = customer_phone;
		this.customer_email = customer_email;
		this.customer_address = customer_address;
	}

	public dessertOrder(Integer dessert_order_id, String customer_name, String customer_phone, String customer_email,
			String customer_address) {
		super();
		this.dessert_order_id = dessert_order_id;
		this.customer_name = customer_name;
		this.customer_phone = customer_phone;
		this.customer_email = customer_email;
		this.customer_address = customer_address;
	}

	public dessertOrder(Integer dessert_order_id, Timestamp order_data, Integer total, String customer_name,
			String customer_phone, String customer_email, String customer_address) {
		super();
		this.dessert_order_id = dessert_order_id;
		this.order_data = order_data;
		this.total = total;
		this.customer_name = customer_name;
		this.customer_phone = customer_phone;
		this.customer_email = customer_email;
		this.customer_address = customer_address;
	}

	@Override
	public String toString() {
		return customer_name + "先生/小姐您好," + "本次訂單總價格為" + total + "元";
	}

	public Integer getDessert_order_id() {
		return dessert_order_id;
	}

	public void setDessert_order_id(Integer dessert_order_id) {
		this.dessert_order_id = dessert_order_id;
	}

	public Timestamp getOrder_data() {
		return order_data;
	}

	public void setOrder_data(Timestamp order_data) {
		this.order_data = order_data;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCustomer_phone() {
		return customer_phone;
	}

	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}

	public String getCustomer_email() {
		return customer_email;
	}

	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}

	public String getCustomer_address() {
		return customer_address;
	}

	public void setCustomer_address(String customer_address) {
		this.customer_address = customer_address;
	}

}
