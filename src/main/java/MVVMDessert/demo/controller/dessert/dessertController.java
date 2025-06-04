package MVVMDessert.demo.controller.dessert;

import java.sql.SQLException;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.picRule;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.model.dessert;
import MVVMDessert.demo.service.dessertService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
public class dessertController {
    private static final Logger logger = LogManager.getLogger(dessertController.class);


//	@Autowired
//	private JedisPool jPool;

	@Autowired
	private ObjectMapper objectMapper;
//	@Autowired
//	private DessertRepository deseertFluxDao;

	@Autowired
	private Validator validator;
	@Autowired
	private static dessertDAO dessertDao;

	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		dessertDao = context.getBean(dessertDAO.class);
	}
	
    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("故意拋出錯誤來產生 500");
    }

//	@GetMapping("/webFluxAsString")
//	public Mono<String> getAllDessertsAsString() {
//		return deseertFluxDao.findAllDesserts() // Flux<Dessert>
//				.collectList() // Mono<List<Dessert>>
//				.map(list -> {
//					try {
//						// 把 List<Dessert> 轉成 JSON 字串
//						return objectMapper.writeValueAsString(list);
//					} catch (JsonProcessingException e) {
//						// 出錯時回傳空陣列或錯誤資訊
//						return "[]";
//					}
//				});
//	}

	@PostMapping("/dessertQuery")
	public String dessertQueryAll() {

//		Jedis jedis = jPool.getResource();
//		jedis.set("3", "test");
//		jedis.expire("3", 10);

		try {
			logger.info("載入成功");
			return dessertDao.getAllDessert().toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		jPool.destroy();
		return null;
	}

	@PostMapping("/dessertName")
	public String dessertQueryName(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		String string = jsonRequests.getString("inputString");
		if (!string.matches("^[(\u4e00-\u9fa5)(a-zA-Z)]{1,}$")) {
			return "-1";
		}
		return dessertDao.getDessertByName(string).toString();
	}

	@PostMapping("/dessertNumber")
	public String dessertQueryNumber(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		Integer number = jsonRequests.optInt("inputmaxValue", 0);
		if (number <= 0) {
			return "-1";
		}

		return dessertDao.getDessertByAmount(number).toString();
	}

	@RequestMapping("/orderView")
	public ModelAndView index_order(Model model) {
		return new ModelAndView("/backView/order");
	}

	@RequestMapping("/backView")
	public ModelAndView index(Model model) {
		return new ModelAndView("/backView/dessert");
	}

	@RequestMapping("/promoteCode")
	public ModelAndView index_promotecode(Model model) {
		return new ModelAndView("/backView/promotcode");
	}

	@PostMapping("/dessertAdd")
	public String dessertAdd(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		JSONObject dessertItem = jsonRequests.getJSONObject("dessertItem");
		Integer dessert_preserve_date = dessertItem.optInt("dessert_preserve_date", 0);
		Integer dessert_type_id = dessertItem.optInt("dessert_type_id", 0);
		String dessert_name = dessertItem.optString("dessert_name", null);
		Integer dessert_price = dessertItem.optInt("dessert_price", 0);
		Integer dessert_amount = dessertItem.optInt("dessert_amount", 0);
		String dessert_instruction = dessertItem.optString("dessert_instruction", null);
		Integer dessert_total_score = dessertItem.optInt("dessert_total_score", 0);
		Integer dessert_total_people = dessertItem.optInt("dessert_total_people", 0);
		String dessert_pic = dessertItem.optString("dessertpic", null);
		dessert dessert = new dessert(dessert_preserve_date, dessert_type_id, dessert_name, dessert_price,
				dessert_amount, dessert_instruction, dessert_total_score, dessert_total_people, dessert_pic);
		Set<ConstraintViolation<@Valid dessert>> errormsgfromBackend = validator.validate(dessert, Default.class,
				picRule.class);

		if (!errormsgfromBackend.isEmpty()) {
			return handleError(dessert);
		}

		return dessertDao.insertDessert(dessert);

	}

	@PostMapping("/dessertmodify")
	public String dessertModify(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		System.out.println(jsonBody);

		dessertService dessertService = new dessertService();

		JSONObject dessertItem = jsonRequests.getJSONObject("dessertItem");
		Integer dessert_preserve_date = dessertItem.optInt("dessert_preserve_date", 0);
		Integer dessert_type_id = dessertItem.optInt("dessert_type_id", 0);
		String dessert_name = dessertItem.optString("dessert_name", null);
		Integer dessert_price = dessertItem.optInt("dessert_price", 0);
		Integer dessert_amount = dessertItem.optInt("dessert_amount", 0);
		String dessert_instruction = dessertItem.optString("dessert_instruction", null);
		Integer id = dessertItem.getInt("dessert_id");
		dessert dessert_old = dessertDao.getOneDessert(id.toString());
//		System.out.println("dessert_old" + dessert_old);
		Integer dessert_total_score = dessert_old.getDessert_total_score();
//		System.out.println("dessert_total_score=" + dessert_total_score);
		Integer dessert_total_people = dessert_old.getDessert_total_people();
		String dessert_pic = dessertItem.optString("dessertpic", null);
//		System.out.println(dessert_pic);
		if (dessert_pic == null) {
			dessert_pic = "data:image/jpeg;base64," + dessert_old.getDessert_pic();
		}
		System.out.println(dessert_pic);
		dessert dessert_new = new dessert(dessert_preserve_date, dessert_type_id, dessert_name, dessert_price,
				dessert_amount, dessert_instruction, dessert_total_score, dessert_total_people, dessert_pic);
		Set<ConstraintViolation<@Valid dessert>> errormsgfromBackend = validator.validate(dessert_new, Default.class,
				picRule.class);

		System.out.println(errormsgfromBackend.isEmpty());
		if (!errormsgfromBackend.isEmpty()) {
//			System.out.println(handleError(dessert_new));
			return handleError(dessert_new);
		}
//		System.out.println("2222");
		dessert_new.setDessert_id(id);
//		String result = dessertDao.modifyDessert(dessert_new);
		return dessertDao.modifyDessert(dessert_new);

	}

	@PostMapping("/dessertdelete")
	public String dessertdelete(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		Integer id = jsonRequests.getJSONObject("dessertItem").getInt("dessert_id");
		return dessertDao.deleteDessert(id);

	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleError(dessert dessert) {
		Set<ConstraintViolation<@Valid dessert>> errormsgsfromBackend = validator.validate(dessert);
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<@Valid dessert> violation : errormsgsfromBackend) {
			strBuilder.append(violation.getMessage() + ",");
		}
		System.out.println(strBuilder.toString());
		return strBuilder.toString();

	}

}
