package MVVMDessert.demo.dao;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class promoteCodeDAO {
	@Autowired
	private JedisPool jPool;

	public JSONArray getAllPromoteCode() {
		Jedis jedis = jPool.getResource();
		jedis.select(1);
		JSONArray result = new JSONArray();
		Set<String> set_key = jedis.keys("*");
		for (String key : set_key) {
			JSONObject jobject = new JSONObject();
			jobject.put("promoteCode", key);
			jobject.put("promoteValue", jedis.get(key));
			jobject.put("last_time", FormateTime(jedis.ttl(key)));
			result.put(jobject);
		}
		jedis.close();
		return result;
	}

	private String FormateTime(long time) {
		long oneday = 24 * 60 * 60;
		long onehour = 60 * 60;
		long onemin = 60;
		int days, hours, mins, seconds;
		StringBuilder sbSql = new StringBuilder();

		days = (int) (time / oneday);
		hours = (int) ((time % oneday) / onehour);
		mins = (int) (((time % oneday) % onehour) / onemin);
		seconds = (int) (((time % oneday) % onehour) % onemin);

		if (days != 0) {
			sbSql.append(days);
			sbSql.append("天");
		}

		if (hours != 0) {
			sbSql.append(hours);
			sbSql.append("小時");
		}

		if (mins != 0) {
			sbSql.append(mins);
			sbSql.append("分鐘");
		}

		if (seconds != 0) {
			sbSql.append(seconds);
			sbSql.append("秒");
		}

		return sbSql.toString();

	}
}
