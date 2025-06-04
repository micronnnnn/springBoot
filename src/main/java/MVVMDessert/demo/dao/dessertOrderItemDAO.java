package MVVMDessert.demo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import MVVMDessert.demo.model.dessertOrderItem;

public class dessertOrderItemDAO {
//	get connection pool use
	@Autowired
	private DataSource ds;

//	SQL script
	private static final String INSERT_INTO_DESSERT_ORDERITEM = "insert into dessert_item(DESSERT_ID,DESSERT_ORDER_ID,AMOUNT,DESSERT_PRICE,DESSERT_DEADLINE) values(?,?,?,?,?);";
	private static final String GET_ALL_ITEM_FROM_ORDER = "select * from dessert_item WHERE DESSERT_ORDER_ID=? ";
	private static final String DELETE_ALL_ORDERITEM = "delete from dessert_item WHERE DESSERT_ORDER_ID=? ";

//	function
	public void insert(Integer dessert_id, Integer dessert_order_id, Integer amount, Integer dessert_price,
			LocalDateTime dessert_deadline) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(INSERT_INTO_DESSERT_ORDERITEM);// 告訴連線，準備使用這個語句

			pstmt.setInt(1, dessert_id);
			pstmt.setInt(2, dessert_order_id);
			pstmt.setInt(3, amount);
			pstmt.setInt(4, dessert_price);
			pstmt.setObject(5, dessert_deadline);
			pstmt.executeUpdate();// 執行更新應該用executeUpdate

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void deleteAllOrderItem(Integer id) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(DELETE_ALL_ORDERITEM);
			pstmt.setInt(1, id);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}

	public List<dessertOrderItem> getAllDessertItem(Integer id) throws SQLException {
		List<dessertOrderItem> list = new ArrayList<dessertOrderItem>();
		dessertOrderItem dessertOrderItem = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ALL_ITEM_FROM_ORDER);// 告訴連線，準備使用這個語句
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				dessertOrderItem = new dessertOrderItem();
				dessertOrderItem.setDessert_id(rs.getInt(1));
				dessertOrderItem.setDessert_order_id(rs.getInt(2));
				dessertOrderItem.setAmount(rs.getInt(3));
				dessertOrderItem.setDessert_price(rs.getInt(4));
				dessertOrderItem.setDessert_deadline((LocalDateTime) rs.getObject(5));
				list.add(dessertOrderItem);

			}
//			pstmt.setInt(1, dessert_id);
//			pstmt.setInt(2, dessert_order_id);
//			pstmt.setInt(3, amount);
//			pstmt.setInt(4, dessert_price);
//			pstmt.setObject(5, dessert_deadline);
//			pstmt.executeUpdate();// 執行更新應該用executeUpdate

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			return list;
		}

	}

}
