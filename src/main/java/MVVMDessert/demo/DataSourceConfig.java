package MVVMDessert.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import MVVMDessert.demo.dao.dessertDAO;
import MVVMDessert.demo.dao.dessertOrderDAO;
import MVVMDessert.demo.dao.dessertOrderItemDAO;
import MVVMDessert.demo.dao.promoteCodeDAO;
import MVVMDessert.demo.dao.promoteListDAO;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@PropertySource("classpath:application.properties")
@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}") // <-- 注入applicatrion.properties的spring.datasource.url的值
	private String url;
	@Value("${spring.datasource.username}") // <-- 注入applicatrion.properties的spring.datasource.username的值
	private String username;
	@Value("${spring.datasource.password}") // <-- 注入applicatrion.properties的spring.datasource.password的值
	private String password;

////	redis use
	@Value("${spring.redis.jedis.pool.max-active}")
	private Integer redis_Max;
	@Value("${spring.redis.jedis.pool.max-idle}")
	private Integer redis_IdleMax;
	@Value("${spring.redis.jedis.pool.max-wait}")
	private Integer redis_WaitMax;
	@Value("${spring.redis.host}")
	private String redis_Url;
	@Value("${spring.redis.port}")
	private Integer redis_Port;

	@Primary
//	@Bean(name = "Test_HikariDataSource")
	@Bean
//	@ConfigurationProperties(prefix = "spring.datasource.jndi-name")
	public DataSource dataSource() { // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#data
		return DataSourceBuilder // 官方建議的 HikariDataSource 連線池
				.create().url(url).username(username).password(password).build();

	}

	@Bean
	public JedisPool getJedisPool() {

		JedisPool pool = null;
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(redis_Max);
		config.setMaxIdle(redis_IdleMax);
		config.setMaxWaitMillis(redis_WaitMax);
		pool = new JedisPool(config, redis_Url, redis_Port);
		return pool;
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public dessertDAO dessertDAO() {
		return new dessertDAO();
	}

	@Bean
	public dessertOrderDAO dessertOrderDAO() {
		return new dessertOrderDAO();
	}

	@Bean
	public dessertOrderItemDAO dessertOrderItemDAO() {
		return new dessertOrderItemDAO();
	}

	@Bean
	public promoteCodeDAO promoteCodeDAO() {
		return new promoteCodeDAO();
	}

	@Bean
	public promoteListDAO promoteListDAO() {
		return new promoteListDAO();
	}

}