package MVVMDessert.demo.model;

import java.time.LocalDateTime;

public class PromoteList {

	private Integer pomotelist_date_id;
	private LocalDateTime promotestart_time;
	private LocalDateTime promoteend_time;
	private String promote_instruction;

	public Integer getPomotelist_date_id() {
		return pomotelist_date_id;
	}

	public void setPomotelist_date_id(Integer pomotelist_date_id) {
		this.pomotelist_date_id = pomotelist_date_id;
	}

	public LocalDateTime getPromotestart_time() {
		return promotestart_time;
	}

	public void setPromotestart_time(LocalDateTime promotestart_time) {
		this.promotestart_time = promotestart_time;
	}

	public LocalDateTime getPromoteend_time() {
		return promoteend_time;
	}

	public void setPromoteend_time(LocalDateTime promoteend_time) {
		this.promoteend_time = promoteend_time;
	}

	public String getPromote_instruction() {
		return promote_instruction;
	}

	public void setPromote_instruction(String promote_instruction) {
		this.promote_instruction = promote_instruction;
	}

}
