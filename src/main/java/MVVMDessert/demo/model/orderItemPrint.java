package MVVMDessert.demo.model;

public class orderItemPrint {

//	just for print no DB or further use
	private Integer dessert_amount;
	private Integer dessert_price;
	private Integer subtotal;
	private String dessert_name;
	private String dessert_deadline;

	public Integer getDessert_amount() {
		return dessert_amount;
	}

	public void setDessert_amount(Integer dessert_amount) {
		this.dessert_amount = dessert_amount;
	}

	public Integer getDessert_price() {
		return dessert_price;
	}

	@Override
	public String toString() {
		return "甜點名稱=" + dessert_name + ", 單價=" + dessert_price + ", 小計=" + subtotal + ", 數量=" + dessert_amount
				+ ", 甜點保存期限=" + dessert_deadline;
	}

	public void setDessert_price(Integer dessert_price) {
		this.dessert_price = dessert_price;
	}

	public Integer getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Integer subtotal) {
		this.subtotal = subtotal;
	}

	public String getDessert_name() {
		return dessert_name;
	}

	public void setDessert_name(String dessert_name) {
		this.dessert_name = dessert_name;
	}

	public String getDessert_deadline() {
		return dessert_deadline;
	}

	public void setDessert_deadline(String dessert_deadline) {
		this.dessert_deadline = dessert_deadline;
	}

}
