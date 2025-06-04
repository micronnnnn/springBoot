package MVVMDessert.demo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import MVVMDessert.demo.model.dessertOrder;

public class dessertOrderDAO {

//	get connection pool use
	@Autowired
	private DataSource ds;

//	SQL script
	private static final String INSERT_INTO_DESSERT_ORDER = "insert into dessert_order(TOTAL,CUSTOMER_NAME,CUSTOMER_PHONE,CUSTOMER_EMAIL,CUSTOMER_ADDRESS) values(?,?,?,?,?);";
	private static final String GET_ONE_STMT = "select * from dessert_order WHERE DESSERT_ORDER_ID=?";
	private static final String GET_ALL_ORDER = "select * from dessert_order";
	private static final String GET_BY_STRING = "select * from dessert_order WHERE CUSTOMER_NAME LIKE CONCAT( '%',?,'%')";
	private static final String DELETE_STRING = "DELETE FROM dessert_order WHERE DESSERT_ORDER_ID=? ";
	private static final String UPDATE_STRING = "Update dessert_order SET CUSTOMER_NAME=?,CUSTOMER_PHONE=?,CUSTOMER_EMAIL=?,CUSTOMER_ADDRESS=? WHERE DESSERT_ORDER_ID=?";
//	function

	public String modify(dessertOrder dessertOrder) throws SQLException {
		String result = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(UPDATE_STRING);
			pstmt.setString(1, dessertOrder.getCustomer_name());
			pstmt.setString(2, dessertOrder.getCustomer_phone());
			pstmt.setString(3, dessertOrder.getCustomer_email());
			pstmt.setString(4, dessertOrder.getCustomer_address());
			pstmt.setInt(5, dessertOrder.getDessert_order_id());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return "修改成功";

	}

	public JSONArray getAllOrder() throws SQLException {
		JSONArray result = new JSONArray();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ALL_ORDER);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject userData = new JSONObject();
				userData.put("dessert_order_id", rs.getInt(1));
				userData.put("order_date", Timestamp.from(rs.getTimestamp(2).toInstant().plusSeconds(28800))
						.toLocalDateTime().format(formatter));
				userData.put("total", rs.getInt(3));
				userData.put("customer_name", rs.getString(4));
				userData.put("customer_phone", rs.getString(5));
				userData.put("customer_email", rs.getString(6));
				userData.put("customer_address", rs.getString(7));
				result.put(userData);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	public String deleteOrder(Integer id) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(DELETE_STRING);
			pstmt.setInt(1, id);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

		return "刪除成功";

	}

	public JSONArray getAllOrderByCustomerName(String string) throws SQLException {
		JSONArray result = new JSONArray();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_BY_STRING);
			pstmt.setString(1, string);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject userData = new JSONObject();
				userData.put("dessert_order_id", rs.getInt(1));
				userData.put("order_date",
						Timestamp.from(rs.getTimestamp(2).toInstant()).toLocalDateTime().format(formatter));
				userData.put("total", rs.getInt(3));
				userData.put("customer_name", rs.getString(4));
				userData.put("customer_phone", rs.getString(5));
				userData.put("customer_email", rs.getString(6));
				userData.put("customer_address", rs.getString(7));
				result.put(userData);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	public Integer insert(Integer amount, JSONObject jsonBody) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		Integer order_id = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(INSERT_INTO_DESSERT_ORDER, java.sql.Statement.RETURN_GENERATED_KEYS);// 告訴連線，準備使用這個語句

			pstmt.setInt(1, amount);
			pstmt.setString(2, jsonBody.optString("customer_name", "nodata"));
			pstmt.setString(3, jsonBody.optString("customer_phone", "nodata"));
			pstmt.setString(4, jsonBody.optString("customer_email", "nodata"));
			pstmt.setString(5, jsonBody.optString("customer_address", "nodata"));
			pstmt.executeUpdate();// 執行更新應該用executeUpdate

			ResultSet rs = pstmt.getGeneratedKeys();
			rs.next();
			order_id = rs.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return order_id;

	}

	public JSONObject getOneDessertOrder(Integer id) throws SQLException {
		JSONObject result = new JSONObject();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ONE_STMT);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.put("dessert_order_id", rs.getInt(1));
				result.put("order_date", rs.getTimestamp(2));
				result.put("total", rs.getInt(3));
				result.put("customer_name", rs.getString(4));
				result.put("customer_phone", rs.getString(5));
				result.put("customer_email", rs.getString(6));
				result.put("customer_address", rs.getString(7));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	public dessertOrder getOneDessertOrder(String id) throws SQLException {
		dessertOrder result = new dessertOrder();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ONE_STMT);
			pstmt.setInt(1, Integer.valueOf(id));
			rs = pstmt.executeQuery();
			if (rs.next()) {
//				result.put("dessert_order_id", rs.getInt(1));
//				result.put("order_date", rs.getTimestamp(2));
//				result.put("total", rs.getInt(3));
//				result.put("customer_name", rs.getString(4));
//				result.put("customer_phone", rs.getString(5));
//				result.put("customer_email", rs.getString(6));
//				result.put("customer_address", rs.getString(7));
				result.setDessert_order_id(rs.getInt(1));
				result.setOrder_data(rs.getTimestamp(2));
				result.setTotal(rs.getInt(3));
				result.setCustomer_name(rs.getString(4));
				result.setCustomer_phone(rs.getString(5));
				result.setCustomer_email(rs.getString(6));
				result.setCustomer_address(rs.getString(7));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

}
