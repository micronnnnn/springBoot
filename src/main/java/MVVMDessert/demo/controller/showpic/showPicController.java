package MVVMDessert.demo.controller.showpic;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class showPicController {
	@Autowired
	private DataSource ds;
	private static final String GET_DESSERT_IMG = "select DESSERT_IMG from dessert where DESSERT_ID=?";

	@GetMapping("/dessertPic")
	public void getDessertPic(@RequestParam("id") String id, HttpServletRequest req, HttpServletResponse res)
			throws IOException, SQLException {

		res.setContentType("image/gif");
		ServletOutputStream out = res.getOutputStream();
		PreparedStatement pstmt = null;

		try (Connection conn = ds.getConnection()) {
			pstmt = conn.prepareStatement(GET_DESSERT_IMG);// 告訴連線，準備使用這個語句
			pstmt.setInt(1, Integer.valueOf(id));
			pstmt.executeQuery();// 執行更新應該用executeUpdate
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				BufferedInputStream in = new BufferedInputStream(rs.getBinaryStream(1));
				byte[] buf = new byte[4 * 1024]; // 4K buffer
				int len;

				while ((len = in.read(buf)) != -1) {
					out.write(buf, 0, len);
				}
				in.close();

			}
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}
}
