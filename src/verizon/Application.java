package verizon;

import java.io.IOException;
import java.sql.SQLException;

public class Application {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {
       System.out.println("Driver program started");
       Driver driver = new Driver();
       if( args.length < 0){
           System.out.println("check the parameters, Usuage should be java -jar 'select * from test.emp' './myQuery'");
           System.exit(1);
       }
       else if(args.length == 2){
           driver.executeStatement(args[0], args[1]);
       }
       else if(args.length == 1){
            System.out.println("Running query with no output params set");
           driver.executeStatement(args[0], "");
        }
    }
}
