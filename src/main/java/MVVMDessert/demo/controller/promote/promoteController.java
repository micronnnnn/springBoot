package MVVMDessert.demo.controller.promote;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.dao.promoteCodeDAO;
import MVVMDessert.demo.dao.promoteListDAO;
import MVVMDessert.demo.model.PromoteList;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
public class promoteController {

	@Autowired
	private JedisPool jPool;

	static promoteCodeDAO promoteCodeDAO;
	static promoteListDAO promoteListDAO;
	static dessertDAO dessertDAO;

	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		promoteCodeDAO = context.getBean(promoteCodeDAO.class);
		promoteListDAO = context.getBean(promoteListDAO.class);
		dessertDAO = context.getBean(dessertDAO.class);
	}

	@PostMapping("/promoteCodeQuery")
	public String getPromoteCode() {
		return promoteCodeDAO.getAllPromoteCode().toString();
	}

	@PostMapping("/AllPromoteProject")
	public String getaAllProject() throws SQLException {
		JSONArray result = new JSONArray();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		List<PromoteList> promoteList = promoteListDAO.getAll();
		for (int i = 0; i < promoteList.size(); i++) {
			PromoteList onePromote = promoteList.get(i);
			JSONObject one_result = new JSONObject();
			one_result.put("promotelist_date_id", onePromote.getPomotelist_date_id());
			one_result.put("promotelist_start_date", onePromote.getPromotestart_time().format(formatter));
			one_result.put("promotelist_end_date", onePromote.getPromoteend_time().format(formatter));
			one_result.put("promotelist_instruction", onePromote.getPromote_instruction());
			result.put(one_result);

		}
		return result.toString();

	}

	@PostMapping("/findCurrentPromoteProject")
	public String getCurrentProject() throws SQLException {
		Jedis jedis = jPool.getResource();
		jedis.select(2);
		LocalDateTime now = LocalDateTime.now();
		PromoteList currentPromote = new PromoteList();
		JSONArray result = new JSONArray();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		List<PromoteList> promoteList = promoteListDAO.getAll();
		String string = "";
		for (int i = 0; i < promoteList.size(); i++) {
			PromoteList onePromote = promoteList.get(i);
			LocalDateTime promoteStartTime = onePromote.getPromotestart_time();
			LocalDateTime promoteEndTime = onePromote.getPromoteend_time();
			if (promoteStartTime.isBefore(now) && promoteEndTime.isAfter(now)) {
				currentPromote = onePromote;
				JSONObject one_result = new JSONObject();
				one_result.put("promotelist_date_id", onePromote.getPomotelist_date_id());
				one_result.put("promotelist_start_date", onePromote.getPromotestart_time().format(formatter));
				one_result.put("promotelist_end_date", onePromote.getPromoteend_time().format(formatter));
				one_result.put("promotelist_instruction", onePromote.getPromote_instruction());
				result.put(one_result);
			}

		}

		string = "促銷方案 " + currentPromote.getPromote_instruction() + "開始時間 " + "    "
				+ currentPromote.getPromotestart_time().format(formatter) + "結束時間 " + "    "
				+ currentPromote.getPromoteend_time().format(formatter);
		HashMap<String, String> total_promote_iteMap = (HashMap<String, String>) jedis
				.hgetAll(currentPromote.getPomotelist_date_id().toString());

		for (String key : total_promote_iteMap.keySet()) {
			string += dessertDAO.getOneDessert(key).getDessert_name();
			string += ":買"
					+ (Character.getNumericValue(total_promote_iteMap.get(key).charAt(0))
							- Character.getNumericValue(total_promote_iteMap.get(key).charAt(2)))
					+ "送" + total_promote_iteMap.get(key).charAt(2) + ",";

//			System.out.println(total_promote_iteMap.get(key).charAt(0));
//			System.out.println(Integer.valueOf(total_promote_iteMap.get(key).charAt(0)));
//			System.out.println(Character.getNumericValue(total_promote_iteMap.get(key).charAt(0)));

		}
		string += "要買要快，不買可惜!";

		jedis.close();
		return string;

	}

	@PostMapping("/addPromteDetailed")
	public void addPromteDetailed(@RequestBody String jsonBody) {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		System.out.println(jsonRequests);
		HashMap<String, String> onepromte_list;
		Jedis jedis = jPool.getResource();
		jedis.select(2);
		JSONObject onepromoteItem = jsonRequests.getJSONObject("onepromoteItem");
		Integer promote_id = onepromoteItem.getInt("promotelist_date_id");
		Integer promte_dessert_id = onepromoteItem.getInt("promoteDessert");
		Integer promte_amount = onepromoteItem.getInt("promoteAmount");
		String promote_Discount = onepromoteItem.getString("promoteDiscount");
		String redis_input = promte_amount + "," + promote_Discount;
		onepromte_list = (HashMap<String, String>) jedis.hgetAll(promote_id.toString());
		onepromte_list.put(promte_dessert_id.toString(), redis_input);
		jedis.hmset(promote_id.toString(), onepromte_list);
//		// test for array
//		jedis.select(3);
//		jedis.set("99", "1,2");

		jedis.close();

	}

}
