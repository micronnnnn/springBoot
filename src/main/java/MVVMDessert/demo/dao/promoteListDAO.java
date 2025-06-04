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

import MVVMDessert.demo.model.PromoteList;

public class promoteListDAO {

	// get connection pool use
	@Autowired
	private DataSource ds;

	// get SQL script
	private static final String GET_ALL_STMT = "select * from PROMTELIST_DATE order by PROMOTELIST_DATE_ID";

	public List<PromoteList> getAll() throws SQLException {
		List result = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				PromoteList promotelist = new PromoteList();
				promotelist.setPomotelist_date_id(rs.getInt(1));
				promotelist.setPromotestart_time((LocalDateTime) rs.getObject(2));
				promotelist.setPromoteend_time((LocalDateTime) rs.getObject(3));
				promotelist.setPromote_instruction(rs.getString(4));
				result.add(promotelist);

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
