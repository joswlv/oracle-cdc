package com.joswlv.github.oracle.driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Created by SeungWanJo on 08/10/2019
 */
@Data
public class JdbcSource {

  @Setter(AccessLevel.NONE)
  private DataSource dataSource;

  private void initDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
    config.setUsername("system");
    config.setPassword("oracle");
    config.addDataSourceProperty("maximumPoolSize", 50);
    config.setMaxLifetime(20000);
    config.setDriverClassName("oracle.jdbc.driver.OracleDriver");
    config.setConnectionTestQuery("select 1 from dual");
    dataSource = new HikariDataSource(config);
  }

  public DataSource getDataSource() {
    if (dataSource == null) {
      initDataSource();
    }
    return this.dataSource;
  }

}
