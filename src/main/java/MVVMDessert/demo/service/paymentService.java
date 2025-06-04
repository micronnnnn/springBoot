package MVVMDessert.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class paymentService {

	@Value("${linepay.channel.id}")
	private String channelId;

	@Value("${linepay.channel.secret}")
	private String channelSecret;

	@Value("${linepay.api.baseurl}")
	private String apiBaseUrl;

	@Value("${linepay.payment.return.url.confirm}")
	private String confirmUrl;

	@Value("${linepay.payment.return.url.cancel}")
	private String cancelUrl;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public Map<String, String> requestPayment(String orderId, String productName, int amount) throws Exception {
		String path = "/v3/payments/request";
		String uri = apiBaseUrl + path;

		Map<String, Object> bodyMap = buildRequestBody(orderId, productName, amount);
		String requestBodyJson = objectMapper.writeValueAsString(bodyMap); // 保證簽名與送出一致

		long nonce = System.currentTimeMillis();
		String nonceStr = Long.toString(nonce);
		String signatureText = channelSecret + "POST" + path + requestBodyJson + nonceStr;
		String signature = Base64.encodeBase64String(new HmacUtils(HmacAlgorithms.HMAC_SHA_256, channelSecret)
				.hmac(signatureText.getBytes(StandardCharsets.UTF_8)));

		// 印出資訊方便 debug
		System.out.println("Channel ID: " + channelId);
		System.out.println("Signature Text: " + signatureText);
		System.out.println("Generated Signature: " + signature);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON); // 不要加 charset
		headers.set("X-LINE-ChannelId", channelId);
		headers.set("X-LINE-Authorization-Nonce", String.valueOf(nonce));
		headers.set("X-LINE-Authorization", signature);
		System.out.println("headers is " + headers);

		HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

		Map<String, Object> response = restTemplate.postForObject(uri, entity, Map.class);
		System.out.println("LINE Pay API response: " + response);

		if (response != null && "0000".equals(response.get("returnCode"))) {
			Map<String, Object> info = (Map<String, Object>) ((java.util.List<?>) response.get("info")).get(0);
			Map<String, String> result = new HashMap<>();
			result.put("paymentUrl", (String) ((Map<String, Object>) info.get("paymentUrl")).get("web"));
			result.put("transactionId", info.get("transactionId").toString());
			return result;
		} else {
			throw new RuntimeException("LINE Pay payment request failed: " + response);
		}
	}

	private Map<String, Object> buildRequestBody(String orderId, String productName, int amount) {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("amount", amount);
		requestBody.put("currency", "TWD");
		requestBody.put("orderId", orderId);
		requestBody.put("productName", "test");
		requestBody.put("redirectUrls", Map.of("confirmUrl", confirmUrl, "cancelUrl", cancelUrl));
		return requestBody;
	}
}
