package com.joswlv.github.oracle;

import com.joswlv.github.oracle.driver.JdbcSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;

/**
 * Created by SeungWanJo on 08/10/2019
 */
public class OracleTest {

  public static void main(String[] args) {
    JdbcSource jdbcSource = new JdbcSource();

    DataSource dataSource = jdbcSource.getDataSource();

    try (Connection conn = dataSource.getConnection()) {
      Statement stmt = conn.createStatement();

      if (tableExist(conn, "playtest")) {
        System.out.println("playtest table is exist!!");
        ResultSet rs = stmt.executeQuery("select * from playtest");
        while (rs.next()) {
          System.out.println(rs.getString(1));
          System.out.println(rs.getString(2));
        }

      } else {
        System.out.println("playtest table is not exist!!");
        stmt.executeQuery("create table playtest("
            + "     id number(10) not null,"
            + "     name varchar2(20)"
            + ")");

        StringBuffer sql = new StringBuffer("INSERT INTO playtest (")
            .append("id")
            .append(", name")
            .append(") VALUES(?, ?)");

        OraclePreparedStatement ps = conn.prepareCall(sql.toString())
            .unwrap(OracleCallableStatement.class);

        int cnt = 0;

        for (int i = 1; i < 1000001; i++) {
          ps.setInt(1, i);
          ps.setString(2, "name-" + i);
          ps.addBatch();
          cnt++;
          if ((cnt % 10000) == 0) {
            cnt = 0;
            ps.executeBatch();
            ps.clearBatch();
            conn.commit();
          }
        }

        ps.executeBatch();
        ps.clearBatch();
        conn.commit();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public static boolean tableExist(Connection conn, String tableName) throws SQLException {
    boolean tExists = false;
    DatabaseMetaData metaData = conn.getMetaData();
    try (ResultSet rs = metaData.getTables(null, null, tableName.toUpperCase(), null)) {
      while (rs.next()) {
        String tName = rs.getString("TABLE_NAME").toLowerCase();
        if (tName != null && tName.equals(tableName.toLowerCase())) {
          tExists = true;
          break;
        }
      }
    }
    return tExists;
  }
}
