package MVVMDessert.demo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import MVVMDessert.demo.model.dessert;

public class dessertDAO {

	// get connection pool use
	@Autowired
	private DataSource ds;

	// get SQL script
	private static final String GET_ALL_STMT = "SELECT * FROM mitactest.dessert ORDER BY dessert_id ASC ";
	private static final String GET_ONE_STMT = "select * from dessert WHERE DESSERT_ID=?";
	private static final String GET_AMOUNT_STMT = "select DESSERT_AMOUNT from dessert WHERE DESSERT_ID=?";
	private static final String GET_BY_STRING = "select * from dessert WHERE DESSERT_NAME LIKE CONCAT( '%',?,'%')";
	private static final String GET_BY_NUMBER = "select * from dessert WHERE DESSERT_AMOUNT < ?;";
	private static final String INSERT = "INSERT INTO dessert"
			+ "(DESSERT_DATE,DESSERT_TYPE_ID,DESSERT_NAME,DESSERT_PRICE,DESSERT_AMOUNT,DESSERT_INSTRUCTION,TKT_TOTAL_SCORE,TKT_TOTAL_PEOPLE,DESSERT_IMG)"
			+ "VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String DELETE = "DELETE FROM dessert where DESSERT_ID=?";
	private static final String UPDATE = "Update dessert SET DESSERT_DATE=?,DESSERT_TYPE_ID=?,DESSERT_NAME=?,DESSERT_PRICE=?,DESSERT_AMOUNT=?,DESSERT_INSTRUCTION=?,TKT_TOTAL_SCORE=?,TKT_TOTAL_PEOPLE=?,DESSERT_IMG=? WHERE DESSERT_ID=?";

	// function

	public JSONArray getAllDessert() throws SQLException {

		JSONArray result = new JSONArray();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject userData = new JSONObject();
				userData.put("dessert_id", rs.getInt(1));
				userData.put("dessert_preserve_date", rs.getInt(2));
				userData.put("dessert_type_id", rs.getInt(3));
				userData.put("dessert_name", rs.getString(4));
				userData.put("dessert_price", rs.getInt(5));
				userData.put("dessert_amount", rs.getInt(6));
				userData.put("dessert_instruction", rs.getString(7));
				userData.put("dessert_total_score", rs.getInt(8));
				userData.put("dessert_total_people", rs.getInt(9));
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

	public List getAllDessertObjectList() throws SQLException {

		List result = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				dessert userData = new dessert();

//				userData.put("dessert_id", rs.getInt(1));
//				userData.put("dessert_preserve_date", rs.getInt(2));
//				userData.put("dessert_type_id", rs.getInt(3));
//				userData.put("dessert_name", rs.getString(4));
//				userData.put("dessert_price", rs.getInt(5));
//				userData.put("dessert_amount", rs.getInt(6));
//				userData.put("dessert_instruction", rs.getString(7));
//				userData.put("dessert_total_score", rs.getInt(8));
//				userData.put("dessert_total_people", rs.getInt(9));
				userData.setDessert_id(rs.getInt(1));
				userData.setDessert_preserve_date(rs.getInt(2));
				userData.setDessert_type_id(rs.getInt(3));
				userData.setDessert_name(rs.getString(4));
				userData.setDessert_price(rs.getInt(5));
				userData.setDessert_amount(rs.getInt(6));
				userData.setDessert_instruction(rs.getString(7));
				userData.setDessert_type_id(rs.getInt(8));
				userData.setDessert_total_people(rs.getInt(9));

				result.add(userData);

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

	public JSONObject getOneDessert(Integer id) throws SQLException {
		JSONObject result = new JSONObject();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ONE_STMT);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				result.put("dessert_id", rs.getInt(1));
				result.put("dessert_preserve_date", rs.getInt(2));
				result.put("dessert_type_id", rs.getInt(3));
				result.put("dessert_name", rs.getString(4));
				result.put("dessert_price", rs.getInt(5));
				result.put("dessert_amount", rs.getInt(6));
				result.put("dessert_instruction", rs.getString(7));
				result.put("dessert_total_score", rs.getInt(8));
				result.put("dessert_total_people", rs.getInt(9));

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

	public dessert getOneDessert(String id) throws SQLException {
		dessert result = new dessert();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ONE_STMT);
			pstmt.setInt(1, Integer.valueOf(id));
			rs = pstmt.executeQuery();
			if (rs.next()) {
//				result.put("dessert_id", rs.getInt(1));
//				result.put("dessert_preserve_date", rs.getInt(2));
//				result.put("dessert_type_id", rs.getInt(3));
//				result.put("dessert_name", rs.getString(4));
//				result.put("dessert_price", rs.getInt(5));
//				result.put("dessert_amount", rs.getInt(6));
//				result.put("dessert_instruction", rs.getString(7));
//				result.put("dessert_total_score", rs.getInt(8));
//				result.put("dessert_total_people", rs.getInt(9));
				result.setDessert_id(rs.getInt(1));
				result.setDessert_preserve_date(rs.getInt(2));
				result.setDessert_type_id(rs.getInt(3));
				result.setDessert_name(rs.getString(4));
				result.setDessert_price(rs.getInt(5));
				result.setDessert_amount(rs.getInt(6));
				result.setDessert_instruction(rs.getString(7));
				result.setDessert_total_score(rs.getInt(8));
				result.setDessert_total_people(rs.getInt(9));
				result.setDessert_pic(DatatypeConverter.printBase64Binary(rs.getBytes(10)));
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

	public JSONArray getDessertByName(String string) throws SQLException {

		JSONArray result = new JSONArray();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_BY_STRING);
			pstmt.setString(1, string);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject userData = new JSONObject();
				userData.put("dessert_id", rs.getInt(1));
				userData.put("dessert_preserve_date", rs.getInt(2));
				userData.put("dessert_type_id", rs.getInt(3));
				userData.put("dessert_name", rs.getString(4));
				userData.put("dessert_price", rs.getInt(5));
				userData.put("dessert_amount", rs.getInt(6));
				userData.put("dessert_instruction", rs.getString(7));
				userData.put("dessert_total_score", rs.getInt(8));
				userData.put("dessert_total_people", rs.getInt(9));
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

	public JSONArray getDessertByAmount(Integer id) throws SQLException {

		JSONArray result = new JSONArray();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_BY_NUMBER);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				JSONObject userData = new JSONObject();
				userData.put("dessert_id", rs.getInt(1));
				userData.put("dessert_preserve_date", rs.getInt(2));
				userData.put("dessert_type_id", rs.getInt(3));
				userData.put("dessert_name", rs.getString(4));
				userData.put("dessert_price", rs.getInt(5));
				userData.put("dessert_amount", rs.getInt(6));
				userData.put("dessert_instruction", rs.getString(7));
				userData.put("dessert_total_score", rs.getInt(8));
				userData.put("dessert_total_people", rs.getInt(9));
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

	// get amount from DB

	public Integer getTotalAmount(Integer id) throws SQLException {
//		JSONObject total = new JSONObject();
		Integer total = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_AMOUNT_STMT);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				total = rs.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return total;
	}

	public String insertDessert(dessert dessert) throws SQLException {
		String result = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		String inputfile = dessert.getDessert_pic();
		String specailhandeString = inputfile.substring(0, inputfile.indexOf(","));//
		inputfile = inputfile.substring(specailhandeString.length() + 1, inputfile.length());

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(INSERT);
			pstmt.setInt(1, dessert.getDessert_preserve_date());
			pstmt.setInt(2, dessert.getDessert_type_id());
			pstmt.setString(3, dessert.getDessert_name());
			pstmt.setInt(4, dessert.getDessert_price());
			pstmt.setInt(5, dessert.getDessert_amount());
			pstmt.setString(6, dessert.getDessert_instruction());
			pstmt.setInt(7, dessert.getDessert_total_score());
			pstmt.setInt(8, dessert.getDessert_total_people());
			pstmt.setBytes(9, DatatypeConverter.parseBase64Binary(inputfile));
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return "新增成功";

	}

	public String deleteDessert(Integer id) throws SQLException {
		String result = null;
		Connection con = null;
		PreparedStatement pstmt = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(DELETE);
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

	public String modifyDessert(dessert dessert) throws SQLException {
		String result = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		String inputfile = dessert.getDessert_pic();
		String specailhandeString = inputfile.substring(0, inputfile.indexOf(","));//
		inputfile = inputfile.substring(specailhandeString.length() + 1, inputfile.length());
		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(UPDATE);
			pstmt.setInt(1, dessert.getDessert_preserve_date());
			pstmt.setInt(2, dessert.getDessert_type_id());
			pstmt.setString(3, dessert.getDessert_name());
			pstmt.setInt(4, dessert.getDessert_price());
			pstmt.setInt(5, dessert.getDessert_amount());
			pstmt.setString(6, dessert.getDessert_instruction());
			pstmt.setInt(7, dessert.getDessert_total_score());
			pstmt.setInt(8, dessert.getDessert_total_people());
			pstmt.setBytes(9, DatatypeConverter.parseBase64Binary(inputfile));
			pstmt.setInt(10, dessert.getDessert_id());
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

}
