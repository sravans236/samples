package verizon;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnector {
    Connection createConnection(String jdbcURL, String user, String password) throws SQLException;
}
