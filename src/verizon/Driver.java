package verizon;

import com.sun.deploy.util.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class Driver {

    private Connection oracleConnection;
    private Statement statement;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;
    private CallableStatement callableStatement;

    public Driver(){
        this.oracleConnection = oracleConnection;
        this.statement = statement;
        this.resultSet = resultSet;
        this.preparedStatement = preparedStatement;
        this.callableStatement = callableStatement;
    }

    private static Connection getConnection() throws SQLException {
        IConnector oracleConnector = new OracleConnectorImpl();
        Connection oracleConnection = oracleConnector.
                createConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
        return oracleConnection;
    }

    public void executeStatement(String sql, String fileName) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            oracleConnection = Driver.getConnection();

            System.out.println("SQL Connection to database established!");

            // this allows to stream the records on heap issues
            statement = oracleConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            //statement.setFetchSize(Integer.MIN_VALUE);
            if (sql.length() > 0) {
                String sqlUpperCase = sql.toUpperCase();
                if (sqlUpperCase.contains("SELECT")) {
                    resultSet = statement.executeQuery(sql);
                    writeResultSetToWriter(resultSet, fileName);
                    statement.close();
                } else if (sqlUpperCase.contains("INSERT") || sql.contains("UPDATE")) {
                    preparedStatement = oracleConnection.prepareStatement(sql);
                    int effectedRows = preparedStatement.executeUpdate();
                    preparedStatement.clearParameters();
                    preparedStatement.close();
                    System.out.println("Insert/Update executed succesfully. Rows effected :" + effectedRows);
                } else if (sqlUpperCase.contains("DELETE")) {
                    preparedStatement = oracleConnection.prepareStatement(sql);
                    boolean isDeleted = preparedStatement.execute();
                    preparedStatement.clearParameters();
                    preparedStatement.close();
                    System.out.println("Delete status.. :" + isDeleted);
                }
                else if(sqlUpperCase.contains("EXEC") || sqlUpperCase.contains("CALL")){
                    //String sql = "{ call pr_RebellionRider }";
                    //String sql = "{ call TEST_INSERT_SP('12','kalvala')}";
                    if(sql.contains("(")) {
                        System.out.println("IN argument stored procedure was called...");
                        String spName =  sql.split("call ")[1].split("\\(")[0];
                        String[] variableParameters = readValuesByIndex(sql);
                        StringBuilder reformattedInputWithQ = new StringBuilder("{ call ").append(spName).append("(");
                        for (int i = 0; i < variableParameters.length; i++) {
                            if (variableParameters.length - 1 == i) {
                                reformattedInputWithQ.append("?");
                            } else {
                                reformattedInputWithQ.append("?").append(",");
                            }
                        }
                        reformattedInputWithQ.append(")}");
                        callableStatement = oracleConnection.prepareCall(reformattedInputWithQ.toString());
                        String[] parameterBox = readValuesByIndex(sql);
                        for (int i = 0; i < parameterBox.length; i++) {
                            callableStatement.setString(i + 1, parameterBox[i].replace("'", ""));
                        }
                    }
                    else{
                        System.out.println("No argument stored procedure was called...");
                        callableStatement = oracleConnection.prepareCall(sql);
                    }
                    callableStatement.execute();
                    System.out.println("Stored procedure executed succesfully...");
                }
                else {
                    System.out.println("only supported operations are SELECT/INSERT/UPDATE/STORED PROCEDURE EXECUTE.");
                }
            } else {
                System.out.println("Please enter valid query...");
            }
        }
        catch (Exception ex){
            System.out.println("SQL Error" + ex.getMessage());
        }
        finally {
            oracleConnection.close();
        }
    }

    private static String[] readValuesByIndex(String inputSP) {
        String requiredString = inputSP.substring(inputSP.indexOf("(") + 1, inputSP.indexOf(")"));
        String[] parameters = requiredString.split(",");
        return parameters;
    }

    private static void writeResultSetToWriter(ResultSet resultSet, String fileName) throws SQLException, IOException {
            FileWriter fileWriter = new FileWriter(fileName, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            //printWriter.print(true);
            ResultSetMetaData metadata = resultSet.getMetaData();
            int numColumns = metadata.getColumnCount();
            int numRows = 0;
            while (resultSet.next())
            {
                ++numRows;
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 1; i <= numColumns; ++i)
                {
                    String column_name = metadata.getColumnName(i);
                    stringBuffer.append(resultSet.getObject(column_name));
                    //if to write as JSON use below
                    //obj.put(column_name, resultSet.getObject(column_name));
                    //writer.println(obj.toJSONString());
                }
                printWriter.println(stringBuffer.toString());
                //writer.write(stringBuffer.toString());
//                if (numRows % 1000 == 0)
                    printWriter.flush();
            }
            printWriter.close();
        }
}
