package MVVMDessert.demo.model;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Range;

import MVVMDessert.demo.picRule;

public class dessert implements java.io.Serializable {

	// validate(not yet)

	private Integer dessert_id;

	@Range(min = 1, message = "甜點有效日期:最小值為一天")
	private Integer dessert_preserve_date;
	@Range(min = 1, max = 2, message = "甜點種類:請務必選擇")
	private Integer dessert_type_id;
	@NotBlank(message = "甜點名稱:請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z)]{1,}$", message = "甜點名稱:只能是中、英文字母")
	private String dessert_name;
	@Range(min = 1, message = "甜點價格:最小值為一元")
	private Integer dessert_price;
	@Range(min = 1, message = "甜點數量:最小值為一個")
	private Integer dessert_amount;
	@NotBlank(message = "甜點說明:請勿空白")
	private String dessert_instruction;
	private Integer dessert_total_score;
	private Integer dessert_total_people;
	@NotNull(message = "甜點圖片:請勿空白")
	@picRule(message = "甜點圖片:請確認格式")
	private String dessert_pic;

	public dessert() {

	}

	public dessert(Integer dessert_preserve_date, Integer dessert_type_id, String dessert_name, Integer dessert_price,
			Integer dessert_amount, String dessert_instruction, Integer dessert_total_score,
			Integer dessert_total_people, String dessert_pic) {
		super();
		this.dessert_preserve_date = dessert_preserve_date;
		this.dessert_type_id = dessert_type_id;
		this.dessert_name = dessert_name;
		this.dessert_price = dessert_price;
		this.dessert_amount = dessert_amount;
		this.dessert_instruction = dessert_instruction;
		this.dessert_total_score = dessert_total_score;
		this.dessert_total_people = dessert_total_people;
		this.dessert_pic = dessert_pic;
	}

	public String getDessert_pic() {
		return dessert_pic;
	}

	public void setDessert_pic(String dessert_pic) {
		this.dessert_pic = dessert_pic;
	}

	@Override
	public String toString() {
		return "甜點名稱=" + dessert_name + ", 總數量=" + dessert_amount;
//		return "name=" + dessert_name + ", amount=" + dessert_amount;
	}

	public Integer getDessert_id() {
		return dessert_id;
	}

	public void setDessert_id(Integer dessert_id) {
		this.dessert_id = dessert_id;
	}

	public Integer getDessert_preserve_date() {
		return dessert_preserve_date;
	}

	public void setDessert_preserve_date(Integer dessert_preserve_date) {
		this.dessert_preserve_date = dessert_preserve_date;
	}

	public Integer getDessert_type_id() {
		return dessert_type_id;
	}

	public void setDessert_type_id(Integer dessert_type_id) {
		this.dessert_type_id = dessert_type_id;
	}

	public String getDessert_name() {
		return dessert_name;
	}

	public void setDessert_name(String dessert_name) {
		this.dessert_name = dessert_name;
	}

	public Integer getDessert_price() {
		return dessert_price;
	}

	public void setDessert_price(Integer dessert_price) {
		this.dessert_price = dessert_price;
	}

	public Integer getDessert_amount() {
		return dessert_amount;
	}

	public void setDessert_amount(Integer dessert_amount) {
		this.dessert_amount = dessert_amount;
	}

	public String getDessert_instruction() {
		return dessert_instruction;
	}

	public void setDessert_instruction(String dessert_instruction) {
		this.dessert_instruction = dessert_instruction;
	}

	public Integer getDessert_total_score() {
		return dessert_total_score;
	}

	public void setDessert_total_score(Integer dessert_total_score) {
		this.dessert_total_score = dessert_total_score;
	}

	public Integer getDessert_total_people() {
		return dessert_total_people;
	}

	public void setDessert_total_people(Integer dessert_total_people) {
		this.dessert_total_people = dessert_total_people;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dessert_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		dessert other = (dessert) obj;
		return Objects.equals(dessert_id, other.dessert_id);
	}

}
