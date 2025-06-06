package MVVMDessert.demo.controller.order;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndView;

import com.twilio.Twilio;

import MVVMDessert.demo.DataSourceConfig;
import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.dao.dessertOrderDAO;
import MVVMDessert.demo.dao.dessertOrderItemDAO;
import MVVMDessert.demo.model.dessert;
import MVVMDessert.demo.model.dessertOrder;
import MVVMDessert.demo.model.dessertOrderItem;
import MVVMDessert.demo.model.orderItemPrint;
import MVVMDessert.demo.service.EmailProducer;
import MVVMDessert.demo.service.paymentService;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
public class orderController {

	@Autowired
	private JedisPool jPool;

	@Autowired
	private Validator validator;

	@Autowired
	private paymentService paymentService; // 注入 PaymentService
	
	@Autowired
	private EmailProducer emailProducer;

	private String handleError1(@Valid dessertOrder dessertOrder) {
		// 錯誤處理邏輯
		return "輸入驗證失敗";
	}

	static dessertOrderDAO dessertOrderDAO;
	static dessertOrderItemDAO dessertOrderItemDAO;
	static dessertDAO dessertDAO;

	static {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
		dessertOrderDAO = context.getBean(dessertOrderDAO.class);
		dessertOrderItemDAO = context.getBean(dessertOrderItemDAO.class);
		dessertDAO = context.getBean(dessertDAO.class);
	}

	@RequestMapping("/backtoView")
	public ModelAndView index(Model model) {
		return new ModelAndView("/index");

	}

	@RequestMapping("/dessertView")
	public ModelAndView index_dessert(Model model) {
		return new ModelAndView("/backView/dessert");
	}

	@PostMapping("/promoteCode")
	public String getPromote(@RequestBody String jsonBody) {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		System.out.println(jsonRequests);
		Jedis jedis = jPool.getResource();
		jedis.select(1);
		System.out.println(jedis.get(jsonRequests.getString("promoteCode")));
		if (jedis.exists(jsonRequests.getString("promoteCode"))) {
			String promoteCode = jedis.get(jsonRequests.getString("promoteCode"));
			jedis.close();
			return promoteCode;

		}
		jedis.close();
		return "1";

	}

	@PostMapping("/orderQuery")
	public String getAllOrder() {
		try {
			return dessertOrderDAO.getAllOrder().toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	@PostMapping("/orderQueryCustomerName")
	public String getAllOrderByCustomerName(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		String string = jsonRequests.getString("inputString");
		if (!string.matches("^[(\u4e00-\u9fa5)(a-zA-Z)]{1,}$")) {
			return "-1";
		}
		return dessertOrderDAO.getAllOrderByCustomerName(string).toString();

	}

	@PostMapping("/orderdelete")
	public String deleteOrder(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		Integer id = jsonRequests.getJSONObject("order").getInt("dessert_order_id");
		dessertOrderItemDAO.deleteAllOrderItem(id);
		return dessertOrderDAO.deleteOrder(id);
	}

	@PostMapping("/getOrderDetailed")
	public String getAllOrderItem(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		Integer id = jsonRequests.getJSONObject("order").getInt("dessert_order_id");

		List<dessertOrderItem> orderItemList = dessertOrderItemDAO.getAllDessertItem(id);
		JSONArray result = new JSONArray();

		for (int i = 0; i < orderItemList.size(); i++) {
			JSONObject userData = new JSONObject();
			dessertOrderItem one_item = orderItemList.get(i);
			String id_dessert = one_item.getDessert_id().toString();
			userData.put("dessert_name", one_item.getDessert(id_dessert).getDessert_name());
			userData.put("dessert_amout", one_item.getAmount());
			userData.put("dessert_price", one_item.getDessert_price());
			userData.put("subtotal", one_item.getAmount() * one_item.getDessert_price());
			userData.put("dessert_deadline", one_item.getDessert_deadline().format(formatter));
			userData.put("dessert_id", one_item.getDessert_id());

			result.put(userData);

		}
		return result.toString();

	}

	@PostMapping("/ordermodify")
	public String modifyOrder(@RequestBody String jsonBody) throws SQLException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		JSONObject order = jsonRequests.getJSONObject("order");
		Integer id = order.getInt("dessert_order_id");
		String customer_name = order.getString("customer_name");
		String customer_phone = order.getString("customer_phone");
		String customer_email = order.getString("customer_email");
		String customer_address = order.getString("customer_address");

		dessertOrder dessertOrder = new dessertOrder(id, customer_name, customer_phone, customer_email,
				customer_address);
		Set<ConstraintViolation<@Valid dessertOrder>> errormsgfromBackend = validator.validate(dessertOrder);

		if (!errormsgfromBackend.isEmpty()) {
			return handleError1(dessertOrder);
		}

		return dessertOrderDAO.modify(dessertOrder);

	}

	@RequestMapping("/linebottest") // query in line console
	public void lineTest(@RequestBody String line_message)
			throws NumberFormatException, SQLException, InterruptedException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(line_message).nextValue();
		System.out.println("line get " + line_message);
		System.out.println("line_message size are " + jsonRequests.length());
		JSONObject event = jsonRequests.getJSONArray("events").getJSONObject(0);
		String replyToken = jsonRequests.getJSONArray("events").getJSONObject(0).getString("replyToken");

		String text = jsonRequests.getJSONArray("events").getJSONObject(0).getJSONObject("message").getString("text");

		if (text.matches("-?\\d+")) {
			System.out.println("line_message size are " + event);
			System.out.println("line_message size are " + event.length());
			System.out.println(replyToken);
			System.out.println(text);
			List<dessertOrderItem> list_order = dessertOrderItemDAO.getAllDessertItem(Integer.valueOf(text));
			System.out.println("list size = " + list_order.size());
			if (list_order.size() == 0) {
				String messString = "查無此訂單";
				sendLineResponseMessages(replyToken, messString);
				return;

			}
			String showsString = dessertOrderDAO.getOneDessertOrder(text).toString();
			System.out.println(showsString);
			String show_item = "";
			for (int i = 0; i < list_order.size(); i++) {
				Integer id = list_order.get(i).getDessert_id();
				show_item += "第" + (i + 1) + "個品項為" + list_order.get(i).getDessert(id.toString()).getDessert_name()
						+ "," + list_order.get(i).toString() + "\\n";
			}
			show_item = showsString + "\\n" + show_item;
			System.out.println(show_item);
			sendLineResponseMessages(replyToken, show_item);
		} else if (text.equals("忘記訂單")) {
			String messageString = "請洽客服專員，電話 0800123123";
			sendLineResponseMessages(replyToken, messageString);
		} else if (text.equals("查詢商品")) {

			List<dessert> list_dessert = dessertDAO.getAllDessertObjectList();
			String show_all_dessert = "";
			for (int i = 0; i < list_dessert.size(); i++) {
				show_all_dessert += "第" + (i + 1) + "個品項為" + list_dessert.get(i).toString() + "\\n";
			}

			sendLineResponseMessages(replyToken, show_all_dessert);
		} else if (text.equals("優惠代碼領取")) {
			String promotecode = getPromoteCode();

			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.FLOOR);
			Integer show_discount = (int) (Math.random() * 21 + 60);
			String discount = df.format((double) show_discount / 100);

//			Save to REDIS AND SET TTL
			Jedis jedis = jPool.getResource();
			jedis.select(1);
			jedis.set(promotecode, discount);
			jedis.expire(promotecode, 3 * 24 * 60 * 60);
			System.out.println(jedis.keys("*").toString());

			sendLineResponseMessages(replyToken,
					"請注意優惠代碼將在3天後失效" + "\\n" + "您的優惠代碼為" + promotecode + "\\n" + "折扣數為" + show_discount + "折");

			jedis.close();

		} else {
			String messString = "";
			sendLineResponseMessages(replyToken, messString);

		}

	}

	@PostMapping("/download")
	public void download(@RequestBody String jsonBody, HttpServletRequest request, HttpServletResponse response)
			throws SQLException, IOException, JRException {

		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		Integer id = jsonRequests.getJSONObject("order").getInt("dessert_order_id");
		List<dessertOrderItem> orderItemList = dessertOrderItemDAO.getAllDessertItem(id);
		List<orderItemPrint> printList = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < orderItemList.size(); i++) {
			dessertOrderItem one_item = orderItemList.get(i);
			orderItemPrint orderItemPrint = new orderItemPrint();
			orderItemPrint.setDessert_amount(one_item.getAmount());
			orderItemPrint.setDessert_deadline(one_item.getDessert_deadline().format(formatter));
			orderItemPrint.setDessert_name(one_item.getDessert(one_item.getDessert_id().toString()).getDessert_name());
			orderItemPrint.setDessert_price(one_item.getDessert_price());
			orderItemPrint.setSubtotal(one_item.getDessert_price() * one_item.getAmount());
			printList.add(orderItemPrint);
		}

////	產生報表
//
////	.jrxml is a human readable XML file that contains the report template i.e. report structure and its formatting rules.
////	.jasper is the compiled report template i.e. compiled .jrxml file. You use this file as the template argument in the JasperReports API.
////	.jrprint is a serialized JasperPrint object i.e. an actual report instance i.e. a template that has been filled with data. This file can be deserialized back into a JasperPrint object.
////	.jrpxml is a human readable XML represenatation of a JasperPrint object i.e. an XML version of a template that has been filled with data. This file can be unmarshalled back into a JasperPrint object.
//
		ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
		ByteArrayOutputStream htmOutStream = new ByteArrayOutputStream();
		ByteArrayOutputStream xlsOutStream = new ByteArrayOutputStream();
		ByteArrayOutputStream csvOutStream = new ByteArrayOutputStream();

		String reportTemplate = "static/report/report.jrxml";
		java.io.InputStream ins = orderController.class.getClassLoader().getResourceAsStream(reportTemplate);
		System.out.println("InputStream" + ins.toString());
		Map<String, Object> parameters = new HashMap<>();
		String fileExt = FilenameUtils.getExtension(reportTemplate);
		System.out.print(fileExt);
		JasperReport jasperReport;
		JasperPrint jasperPrint;
		System.out.println(fileExt.equalsIgnoreCase("jasper"));

//	read 報表細部設定, but i am not sure whether it should judged?
		if (fileExt.equalsIgnoreCase("jasper")) {
			jasperReport = (JasperReport) JRLoader.loadObject(new File(reportTemplate));
		} else {
			jasperReport = JasperCompileManager.compileReport(JRXmlLoader.load(ins));
		}

//	 read 要寫入的檔案來源
		if (!printList.isEmpty()) {
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JRBeanCollectionDataSource(printList));
		} else {
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
		}

		byte[] bytes_pdf = new byte[] {};
		byte[] bytes_html = new byte[] {};
		byte[] bytes_excel = new byte[] {};
		byte[] bytes_csv = new byte[] {};
//
//	// pdf
////	bytes_pdf = createPDF(jasperPrint, pdfOutStream);
////	HttpServletResponse response_pdf = response;
////	response_pdf.reset();
////	response_pdf.setHeader("Content-Disposition", "attachment; filename=order.pdf");
////	response_pdf.setContentType("application/pdf");
////	response_pdf.setContentLength(bytes_pdf.length);
////	ServletOutputStream ouputStream_pdf = response_pdf.getOutputStream();
////	ouputStream_pdf.write(bytes_pdf, 0, bytes_pdf.length);
////	ouputStream_pdf.flush();
////	ouputStream_pdf.close();
//
//	// html
////	bytes_html = createHTML(jasperPrint, htmOutStream);
////	HttpServletResponse response_html = response;
////	response_html.reset();
////	response_html.setHeader("Content-Disposition", "attachment; filename=report.html");
////	response_html.setContentType("text/html");
////	response_html.setContentLength(bytes_html.length);
////	ServletOutputStream ouputStream_html = response_html.getOutputStream();
////	ouputStream_html.write(bytes_html, 0, bytes_html.length);
////	ouputStream_html.flush();
////	ouputStream_html.close();
//
//	 excel
		bytes_excel = createEXCEL(jasperPrint, xlsOutStream);
		HttpServletResponse response_excel = response;
		response_excel.reset();
		// 在瀏覽器中測試生效，postman中檔名為response,無法修改
		response_excel.setHeader("Content-disposition", "attachment;filename=report.xlsx");
		// 此設定，可保證web端可以取到檔名
		response_excel.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
		// 閘道器服務會校驗是否有"Download"標識
		response_excel.setHeader("Response-Type", "Download");
		ServletOutputStream ouputStream_excel = response_excel.getOutputStream();
		ouputStream_excel.write(bytes_excel, 0, bytes_excel.length);
		ouputStream_excel.flush();
		ouputStream_excel.close();
//
//	// csv
//	bytes_csv = createCSV(jasperPrint, csvOutStream);
//	HttpServletResponse response_csv = response;
//	response_csv.reset();
//	response_csv.setCharacterEncoding("UTF-8");// 設定檔案頭編碼方式和檔名
//	response_csv.setContentType("application/octet-stream");
//	response_csv.setHeader("Content-Disposition", "attachment;filename=report.csv");
//	ServletOutputStream ouputStream_csv = response_csv.getOutputStream();
//	ouputStream_csv.write(bytes_csv, 0, bytes_csv.length);
//	ouputStream_csv.flush();
//	ouputStream_csv.close();
//
//	ins.close();
//	return printList.toString();

	}

	@PostMapping("/checkout")
	public String checkout(@RequestBody String jsonBody, HttpServletRequest request, HttpServletResponse response)
			throws SQLException, JRException, IOException {
		JSONObject jsonRequests = (JSONObject) new JSONTokener(jsonBody).nextValue();
		JSONArray cart = jsonRequests.getJSONArray("all_shoppinglist");
		System.out.println(cart.toString());
		Integer total = jsonRequests.optInt("total", 0);
		JSONObject order_info = jsonRequests.getJSONObject("order");

		// 0531 add validate
		dessertOrder dessertOrder = new dessertOrder(order_info.optString("customer_name", ""),
				order_info.optString("customer_phone", ""), order_info.optString("customer_email", ""),
				order_info.optString("customer_address", ""));

		Set<ConstraintViolation<@Valid dessertOrder>> errormsgfromBackend = validator.validate(dessertOrder);

		if (!errormsgfromBackend.isEmpty()) {
			return handleError1(dessertOrder);
		}

		Integer order_id = dessertOrderDAO.insert(total, order_info);
		JSONObject one_order_DB = dessertOrderDAO.getOneDessertOrder(order_id);
		Timestamp time = (Timestamp) one_order_DB.get("order_date");
		List<orderItemPrint> printList = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		// save each shopping item in DB
		JSONObject one_item = null;

		for (int i = 0; i < cart.length(); i++) {
//			save to DB;
			one_item = cart.getJSONObject(i);
			Integer dessert_id = one_item.getInt("dessert_id");
			Integer amount = one_item.getInt("dessert_amount");
			Integer dessert_price = one_item.getInt("dessert_price");
			String dessert_name = one_item.getString("dessert_name");
			Integer subtotal = one_item.getInt("subtotal");

			Integer time_dessert_preserve = dessertDAO.getOneDessert(dessert_id).getInt("dessert_preserve_date") * 24
					* 60 * 60 + 8 * 60 * 60;// 轉成UTC+8(8 * 60 * 60)
			LocalDateTime dessert_preverve_time = Timestamp.from(time.toInstant().plusSeconds(time_dessert_preserve))
					.toLocalDateTime();
			dessertOrderItemDAO.insert(dessert_id, order_id, amount, dessert_price, dessert_preverve_time);

//			create object for printList
			dessertOrderItem dessertOrderItem = new dessertOrderItem();
			dessertOrderItem.setDessert_id(dessert_id);
			dessertOrderItem.setDessert_order_id(order_id);
			dessertOrderItem.setAmount(amount);
			dessertOrderItem.setDessert_price(dessert_price);
			dessertOrderItem.setDessert_deadline(dessert_preverve_time);

//			create object for print use
			orderItemPrint orderItemPrint = new orderItemPrint();
			orderItemPrint.setDessert_amount(amount);
			orderItemPrint.setDessert_deadline(dessert_preverve_time.format(formatter));
			orderItemPrint.setDessert_name(dessert_name);
			orderItemPrint.setDessert_price(dessert_price);
			orderItemPrint.setSubtotal(subtotal);

			printList.add(orderItemPrint);

		}

//		寄信

//		show on mail for printList
		String show_item = "";
		for (int i = 0; i < printList.size(); i++) {
			show_item += printList.get(i).toString() + "\n";
		}

		String to = order_info.getString("customer_email");

		String subject = "訂單通知";

		String ch_name = order_info.getString("customer_name") + "您好:";
		String messageText = "此郵件是系統自動傳送，請勿直接回覆此郵件! \n\n\n" + ch_name + "\n\n\n" + " 您在甜點店 的訂單已完成訂購，以下是您的訂單明細 "
				+ "\n\n\n" + "※ 請注意!本店保留接受訂單與否的權利" + "\n\n\n" + "總共品項共有" + printList.size() + "項\n\n" + "【訂購明細】\n\n"
				+ show_item + "\n\n" + "共" + total + "元\n\n";

//		產生電子郵件
//		MailService mailService = new MailService();
		emailProducer.sendEmailRequest(to, subject, messageText);
//		mailService.sendMail(to, subject, messageText);

//		// 呼叫 LINE Pay API
//		try {
//			Map<String, String> linePayResponse = paymentService.requestPayment(String.valueOf(order_id), // 使用訂單 ID 作為
//																											// LINE Pay
//																											// 的 orderId
//					printList.get(0).getDessert_name()
//							+ (printList.size() > 1 ? " 等 " + printList.size() + " 項商品" : ""), // 商品名稱
//					total // 總金額
//			);
//			return "訂單成立"; // 回傳包含付款連結的 JSON
//		} catch (Exception e) {
//			// 處理 LINE Pay API 呼叫失敗的情況，可能需要記錄錯誤並給予使用者友善的回應
//			e.printStackTrace();
//			return "訂單失敗";
//		}

////		產生簡訊
//		String ch_phone = "+886" + order_info.getString("customer_phone");
//		System.out.println(ch_phone);
//
//		SMSservice smService = new SMSservice();
//		smService.sendSMS(ch_phone, messageText);

		return "訂單成立";

	}

//	寄簡訊

	class SMSservice {

		public static final String ACCOUNT_SID = "ACf53697ccebf35cb66c081dda24e5fc24";
		public static final String AUTH_TOKEN = "2d70bf4cb255f0d0287204fec97009ae";

		public void sendSMS(String phone, String message) {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			com.twilio.rest.api.v2010.account.Message message2 = com.twilio.rest.api.v2010.account.Message
					.creator(new com.twilio.type.PhoneNumber(phone), new com.twilio.type.PhoneNumber("+12542774697"),
							message)
					.create();

		}

	}

////	寄信gmail
//
//	class MailService {
//
//		// 設定傳送郵件:至收信人的Email信箱,Email主旨,Email內容
//		public void sendMail(String to, String subject, String messageText) {
//
//			try {
//				// 設定使用SSL連線至 Gmail smtp Server
//				Properties props = new Properties();
//				props.put("mail.smtp.host", "smtp.gmail.com");
//				props.put("mail.smtp.socketFactory.port", "465");
//				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//				props.put("mail.smtp.auth", "true");
//				props.put("mail.smtp.port", "465");
//
//				// ●設定 gmail 的帳號 & 密碼 (將藉由你的Gmail來傳送Email)
//				// ●1) 登入你的Gmail的:
//				// ●2) 點選【管理你的 Google 帳戶】
//				// ●3) 點選左側的【安全性】
//
//				// ●4) 完成【兩步驟驗證】的所有要求如下:
//				// ●4-1) (請自行依照步驟要求操作之.....)
//
//				// ●5) 完成【應用程式密碼】的所有要求如下:
//				// ●5-1) 下拉式選單【選取應用程式】--> 選取【郵件】
//				// ●5-2) 下拉式選單【選取裝置】--> 選取【Windows 電腦】
//				// ●5-3) 最後按【產生】密碼
//				final String myGmail = "s9912045@gmail.com";
//				final String myGmail_password = "cmtl okwa hidk ptez";
//				Session session = Session.getInstance(props, new Authenticator() {
//					protected PasswordAuthentication getPasswordAuthentication() {
//						return new PasswordAuthentication(myGmail, myGmail_password);
//					}
//				});
//
//				Message message = new MimeMessage(session);
//				message.setFrom(new InternetAddress(myGmail));
//				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//
//				// 設定信中的主旨
//				message.setSubject(subject);
//				// 設定信中的內容
//				message.setText(messageText);
//
//				Transport.send(message);
//				System.out.println("傳送成功!");
//			} catch (MessagingException e) {
//				System.out.println("傳送失敗!");
//				e.printStackTrace();
//			}
//		}
//
//	}

//	報表細部設定
	/**
	 * 產出PDF報表
	 * 
	 * @param jasperPrint
	 * @param pdfOutStream
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */

	private byte[] createPDF(JasperPrint jasperPrint, ByteArrayOutputStream pdfOutStream)
			throws IOException, JRException {
		byte[] result = new byte[] {};
		JRPdfExporter exporter = new JRPdfExporter();
		List<JasperPrint> list = new ArrayList<>();
		list.add(jasperPrint);
		exporter.setExporterInput(SimpleExporterInput.getInstance(list));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutStream));
		SimplePdfExporterConfiguration exporterConfiguration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(exporterConfiguration);
		exporter.exportReport();
		result = pdfOutStream.toByteArray();
		pdfOutStream.flush();
		return result;
	}

	/**
	 * 產出HTML報表
	 * 
	 * @param jasperPrint
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private byte[] createHTML(JasperPrint jasperPrint, ByteArrayOutputStream htmOutStream)
			throws IOException, JRException {
		byte[] result = new byte[] {};
		Exporter exporter = new HtmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(htmOutStream);
		output.setImageHandler(new WebHtmlResourceHandler("/reports/image?image={0}"));
		exporter.setExporterOutput(output);
		exporter.exportReport();
		htmOutStream.flush();
		result = htmOutStream.toByteArray();
		return result;
	}

	/**
	 * 產出EXCEL報表
	 * 
	 * @param jasperPrint
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	private byte[] createEXCEL(JasperPrint jasperPrint, ByteArrayOutputStream xlsOutStream)
			throws IOException, JRException {
		byte[] result = new byte[] {};
		JRXlsxExporter exporter = new JRXlsxExporter();
		List<JasperPrint> list = new ArrayList<>();
		list.add(jasperPrint);
		exporter.setExporterInput(SimpleExporterInput.getInstance(list));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsOutStream));
		SimpleXlsxReportConfiguration exporterConfiguration = new SimpleXlsxReportConfiguration();
		exporterConfiguration.setRemoveEmptySpaceBetweenRows(Boolean.TRUE);
		exporterConfiguration.setIgnoreCellBorder(Boolean.FALSE);
		exporterConfiguration.setWhitePageBackground(Boolean.FALSE);
		exporterConfiguration.setRemoveEmptySpaceBetweenColumns(Boolean.TRUE);
		exporter.setConfiguration(exporterConfiguration);
		exporter.exportReport();
		xlsOutStream.flush();
		result = xlsOutStream.toByteArray();
		return result;
	}

	/**
	 * 產出CSV報表
	 * 
	 * @param jasperPrint
	 * @return
	 * @throws IOException
	 */
	private byte[] createCSV(JasperPrint jasperPrint, ByteArrayOutputStream csvOutStream)
			throws IOException, JRException {
		byte[] result = new byte[] {};
		JRCsvExporter exporter = new JRCsvExporter();
		List<JasperPrint> list = new ArrayList<>();
		list.add(jasperPrint);
		exporter.setExporterInput(SimpleExporterInput.getInstance(list));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(csvOutStream, "BIG5"));
		SimpleCsvExporterConfiguration exporterConfiguration = new SimpleCsvExporterConfiguration();
		exporterConfiguration.setFieldDelimiter(",");
		exporterConfiguration.setRecordDelimiter("\r\n");
		exporter.setConfiguration(exporterConfiguration);
		exporter.exportReport();
		csvOutStream.flush();
		result = csvOutStream.toByteArray();
		return result;
	}

//	Line message
	private void sendLineResponseMessages(String replyToken, String message) {

		String accessToken = "Bb/ZHI26xjIzqAauYqFwiTN+YgTPkhMlty/JOy1EvP6LYJ8wJTNtKjccO/BLyMNbA01Z3hzSzpFi5AoWnad/f+WJaJRDDZY1WpZDj/egbncNhpm6t+CitNAA5KDmCvZ/dSH0JI4TFo6GLot7pUAjZwdB04t89/1O/w1cDnyilFU=";
		try {
			if (message.trim().length() != 0) {
				message = "{\"replyToken\":\"" + replyToken + "\",\"messages\":[{\"type\":\"text\",\"text\":\""
						+ message + "\"}]}"; // 回傳的json格式訊息
			} else {
				message = "{\"replyToken\":\"" + replyToken
						+ "\",\"messages\":[{\"type\":\"template\",\"altText\":\"thisisabuttonstemplate\",\"template\":{\"type\":\"buttons\",\"imageBackgroundColor\":\"#A72D2D\",\"title\":\"請輸入訂單標號\",\"text\":\"查詢訂單詳情\",\"actions\":"
						+ "[{\"type\":\"message\",\"label\":\"忘記訂單編號\",\"text\":\"忘記訂單\"},"
						+ "{\"type\":\"message\",\"label\":\"查詢所有商品數量\",\"text\":\"查詢商品\"},"
						+ "{\"type\":\"message\",\"label\":\"點我領取優惠代碼\",\"text\":\"優惠代碼領取\"}]}}]}";
//						+ "{\"type\":\"message\",\"label\":\"點我領取訂單明細清單\",\"text\":\"請輸入訂單編號\"}]}}]}";
//						+ "{\"type\":\"uri\",\"label\":\"點我領取訂單明細清單\",\"uri\":\"http://localhost:8080/dessert/\"}]}}]}";
			}
			URL myurl = new URL("https://api.line.me/v2/bot/message/reply"); // 回傳的網址
			java.net.HttpURLConnection con = (java.net.HttpURLConnection) myurl.openConnection(); // 使用HttpsURLConnection建立https連線
			System.out.println("con" + con.toString());
			con.setRequestMethod("POST");// 設定post方法
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8"); // 設定Content-Type為json
			con.setRequestProperty("Authorization", "Bearer " + accessToken); // 設定Authorization
			con.setDoOutput(true);
			con.setDoInput(true);
			java.io.DataOutputStream output = new java.io.DataOutputStream(con.getOutputStream()); // 開啟HttpsURLConnection的連線
			output.write(message.getBytes(Charset.forName("utf-8"))); // 回傳訊息編碼為utf-8
			System.out.println(message);
			output.close();
			System.out.println("Resp Code:" + con.getResponseCode() + "; Resp Message:" + con.getResponseMessage()); // 顯示回傳的結果，若code為200代表回傳成功
		} catch (MalformedURLException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String getPromoteCode() {
		int total = 8;
		String promotecode = "";
		for (int i = 1; i <= total; i++) {

			int k = (int) (Math.random() * 75 + 48);
			if (k <= 64 && k >= 58) {
				i--;
				continue;
			} else if (k <= 96 && k >= 91) {
				i--;
				continue;
			} else {
				promotecode += (char) (k);
			}
		}
		return promotecode;

	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleError(dessertOrder dessertOrder) {
		Set<ConstraintViolation<@Valid dessertOrder>> errormsgsfromBackend = validator.validate(dessertOrder);
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<@Valid dessertOrder> violation : errormsgsfromBackend) {
			strBuilder.append(violation.getMessage() + ",");
		}
		System.out.println(strBuilder.toString());
		return strBuilder.toString();

	}

}
