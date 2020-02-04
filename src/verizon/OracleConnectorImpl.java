package verizon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnectorImpl implements IConnector {

    public Connection createConnection(String jdbcURL, String user, String password) throws SQLException {
        Connection oracleConnection = DriverManager.getConnection(jdbcURL, user, password);
        return oracleConnection;
    }

}
