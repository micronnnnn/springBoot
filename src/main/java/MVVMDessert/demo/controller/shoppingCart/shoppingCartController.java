package MVVMDessert.demo.controller.shoppingCart;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndView;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.dao.promoteListDAO;
import MVVMDessert.demo.model.PromoteList;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController // don't forget to add this in each controller
public class shoppingCartController {

	@Autowired
	private JedisPool jPool;

	static dessertDAO dessertDao;
	static promoteListDAO promoteListDAO;

	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		dessertDao = context.getBean(dessertDAO.class);
		promoteListDAO = context.getBean(promoteListDAO.class);
	}

	@PostMapping("/addShoppingCart")
	public String addShoppingCart(@RequestBody String jsonString) throws JSONException, SQLException {
		System.out.println("backend received is" + jsonString);

		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonString).nextValue();

//		System.out.println(jsonRequests.get("all_shoppinglist")); // 用這個去取

//		Get redis data for checking avaliablity of time
//		HashMap<String, String> onepromte_list;
//		LocalDateTime now = LocalDateTime.now();
//		List<PromoteList> promoteList = promoteListDAO.getAll();
//		Integer promotelist_date_id = null;
//		Integer promote_amount=null;
//		for (int i = 0; i < promoteList.size(); i++) {
//			PromoteList onePromote = promoteList.get(i);
//			LocalDateTime promoteStartTime = onePromote.getPromotestart_time();
//			LocalDateTime promoteEndTime = onePromote.getPromoteend_time();
//			if (promoteStartTime.isBefore(now) && promoteEndTime.isAfter(now)) {
//				promotelist_date_id=promoteList.get(i).getPomotelist_date_id();
//			}
//		}

//		current item
		JSONObject shoppinglist = jsonRequests.getJSONObject("shoppinglist");
		Integer item = jsonRequests.getJSONObject("shoppinglist").getInt("item");
		Integer amount = jsonRequests.getJSONObject("shoppinglist").optInt("amount", 0);
		if (amount.equals(0)) {
			return "-1";
		}

//		System.out.println("test is " + jsonRequests.getJSONArray("all_shoppinglist").length()==0);
		// no item create cart
		if (jsonRequests.get("all_shoppinglist").equals(null)
				|| jsonRequests.getJSONArray("all_shoppinglist").length() == 0) {
			JSONArray cart = new JSONArray();
			JSONObject shoppingtoVue = new JSONObject();
			shoppingtoVue.put("dessert_name", dessertDao.getOneDessert(item).getString("dessert_name"));
			shoppingtoVue.put("dessert_price", dessertDao.getOneDessert(item).getInt("dessert_price"));
			shoppingtoVue.put("dessert_id", dessertDao.getOneDessert(item).getInt("dessert_id"));
			shoppingtoVue.put("dessert_amount", amount);
			cart.put(shoppingtoVue);
			return setDataForVue(cart);
		} else {
//			there is a cart
			JSONArray cart = jsonRequests.getJSONArray("all_shoppinglist");
			Integer number = null;
			for (int i = 0; i < cart.length(); i++) {
				JSONObject old_item_cart = cart.getJSONObject(i);
				JSONObject new_item_cart = new JSONObject();
				Integer item_intotal = old_item_cart.getInt("dessert_id");
				Integer item_amount = old_item_cart.getInt("dessert_amount");

//				//redis
//				List<String> data = jedis.hmget(promotelist_date_id.toString(), item_intotal.toString());
//				String[] dataArray=data.get(0).split(",");
//				promote_amount=Integer.valueOf(dataArray[0]);

				if (item_intotal.equals(item) && i <= cart.length() - 1) {
					number = i;
					cart.remove(number); // remove???
					new_item_cart.put("dessert_id", item);
					new_item_cart.put("dessert_amount", amount + item_amount);
					cart.put(new_item_cart);
					continue;
				} else if (!(item_intotal.equals(item)) && i == cart.length() - 1) {
					JSONObject shoppingtoVue = new JSONObject();
					shoppingtoVue.put("dessert_name", dessertDao.getOneDessert(item).getString("dessert_name"));
					shoppingtoVue.put("dessert_price", dessertDao.getOneDessert(item).getInt("dessert_price"));
					shoppingtoVue.put("dessert_id", dessertDao.getOneDessert(item).getInt("dessert_id"));
					shoppingtoVue.put("dessert_amount", amount);
					cart.put(shoppingtoVue);
					break;
				}

			}

			return setDataForVue(cart);

		}

	}

	@PostMapping("/removeShoppingCart")
	public String removeShoppingCart(@RequestBody String jsonBody) throws JSONException, SQLException {
		System.out.println("backend received is" + jsonBody);
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();

//		remove item
		JSONObject removeItem = jsonRequests.getJSONObject("removeItem");
		Integer id_remove = removeItem.getInt("dessert_id");
//		remove it from total
		JSONArray cart = jsonRequests.getJSONArray("all_shoppinglist");
		Integer number = null;
		for (int i = 0; i < cart.length(); i++) {
			JSONObject old_item_cart = cart.getJSONObject(i);
			Integer item_intotal = old_item_cart.getInt("dessert_id");
			if (item_intotal.equals(id_remove)) {
				number = i;
				cart.remove(number); // remove???
				break;
			}
		}
		return setDataForVue(cart);
	}

	@PostMapping("/modifyShoppingCart")
	public String modifyShoppingCart(@RequestBody String jsonBody) throws JSONException, SQLException {
		System.out.println("backend received is" + jsonBody);
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		JSONObject modifiedItem = jsonRequests.getJSONObject("modifyiedshoppinglist");
		Integer id_modify = modifiedItem.getInt("dessert_id");
		Integer amount_modify = modifiedItem.getInt("dessert_amount");
//		remove it from total
		JSONArray cart = jsonRequests.getJSONArray("all_shoppinglist");
		Integer number = null;
		for (int i = 0; i < cart.length(); i++) {
			JSONObject old_item_cart = cart.getJSONObject(i);
			Integer item_intotal = old_item_cart.getInt("dessert_id");
			if (item_intotal.equals(id_modify)) {
				number = i;
				old_item_cart.put("dessert_amount", amount_modify); // remove???
				break;
			}
		}

		return setDataForVue(cart);
	}

	@PostMapping("/checkTotalAmount")
	public String checkTotalAmount(@RequestBody String jsonBody) throws SQLException {
//		System.out.println("checkTotalAmount backend received is" + jsonBody);
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		JSONObject monitoring_item = jsonRequests.getJSONObject("monitoring_item");
		Integer id_monitoring = monitoring_item.getInt("dessert_id");
		Integer total_amount = dessertDao.getTotalAmount(id_monitoring);
		System.out.println(total_amount);
		return total_amount.toString();
	}

	@RequestMapping("/order")
	public ModelAndView index(Model model) {
		return new ModelAndView("/frontView/order"); // 這個要放實際上html放的地方
	}

	// 寫一個function去呼叫redis中存的東西折扣數

	private Integer checkforRedisDiscount(Integer item, Integer amount, Integer price) throws SQLException {

		Integer qantity;
		Jedis jedis = jPool.getResource();
		jedis.select(2);
//		System.out.println("item" + item);
//		System.out.println("amount" + amount);
//		List<String> data = jedis.hmget(promotelist_date_id.toString(), item_intotal.toString());
//		String[] dataArray=data.get(0).split(",");
//		promote_amount=Integer.valueOf(dataArray[0]);

//		System.out.println(jsonRequests.get("all_shoppinglist")); // 用這個去取

//		Get redis data for checking avaliablity of time
		HashMap<String, String> onepromte_list;
		LocalDateTime now = LocalDateTime.now();
		List<PromoteList> promoteList = promoteListDAO.getAll();
//		Integer quaifiedId;
		Integer promotelist_date_id = null;
		Integer promote_amount = null;

		// get qaulified id
		for (int i = 0; i < promoteList.size(); i++) {
			PromoteList onePromote = promoteList.get(i);
			LocalDateTime promoteStartTime = onePromote.getPromotestart_time();
			LocalDateTime promoteEndTime = onePromote.getPromoteend_time();
			if (promoteStartTime.isBefore(now) && promoteEndTime.isAfter(now)) {
				promotelist_date_id = promoteList.get(i).getPomotelist_date_id();
//				quaifiedId.add(promotelist_date_id);
			}
		}

		List<String> data = jedis.hmget(promotelist_date_id.toString(), item.toString());
		System.out.println(data);
		String[] dataArray = data.get(0).split(",");
		Integer q_amount = Integer.valueOf(dataArray[0]);
		Integer q_discount_amount = Integer.valueOf(dataArray[1]);
		qantity = amount / q_amount;
		System.out.println(amount % q_amount);
		jedis.close();
		if (amount % q_amount == 0) {
			return price * (q_amount - q_discount_amount);
		} else {
			return price * (q_amount - q_discount_amount) * qantity + (amount % q_amount) * price;
		}

	}

	private String setDataForVue(JSONArray cart) throws JSONException, SQLException {

		JSONArray result = new JSONArray();
		for (int i = 0; i < cart.length(); i++) {
			JSONObject itemForShow = new JSONObject();
			JSONObject old_item_cart = cart.getJSONObject(i);
//			System.out.println("vue" + old_item_cart);
			Integer item_intotal = old_item_cart.getInt("dessert_id");
			Integer item_amount = old_item_cart.getInt("dessert_amount");
			itemForShow.put("dessert_name", dessertDao.getOneDessert(item_intotal).getString("dessert_name"));
			itemForShow.put("dessert_price", dessertDao.getOneDessert(item_intotal).getInt("dessert_price"));
			itemForShow.put("dessert_id", dessertDao.getOneDessert(item_intotal).getInt("dessert_id"));
			itemForShow.put("dessert_amount", item_amount);
			itemForShow.put("subtotal",
					checkforRedisDiscount(dessertDao.getOneDessert(item_intotal).getInt("dessert_id"), item_amount,
							dessertDao.getOneDessert(item_intotal).getInt("dessert_price")));
			result.put(itemForShow);
//			checkforRedisDiscount(item_intotal);
		}
		return result.toString();

	}

}