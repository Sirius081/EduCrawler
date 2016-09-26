package main.java.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by wenliang on 2015/7/6.
 */
public class DataAccess {
    private final static String db = "";
    private static DataAccess instance = null;
    private static ResourceBundle rb = ResourceBundle.getBundle("db");
    private static Connection connection = null;

//  public static ComboPooledDataSource cpds = new ComboPooledDataSource();

    private DataAccess(){
//        initRDB();
        initRelationDB();
    }

    public synchronized static DataAccess getInstance(){
        if(instance==null){
            instance = new DataAccess();
        }
        return instance;
    }

    private synchronized static void initRelationDB(){
        try {
            Class.forName(rb.getString("DRIVER"));
            connection  = DriverManager.getConnection(rb.getString("URL"),rb.getString("user"),rb.getString("password"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    private synchronized static void initRDB(){
//        if(cpds == null){
//            try {
//                cpds = new ComboPooledDataSource();
//                cpds.setDriverClass(rb.getString("DRIVER"));
//                cpds.setJdbcUrl(rb.getString("URL"));
//                cpds.setMaxPoolSize(20);
//                cpds.setMinPoolSize(2);
//                cpds.setInitialPoolSize(2);
//                cpds.setCheckoutTimeout(1800);
//                cpds.setMaxStatements(0);
//                cpds.setAcquireIncrement(1);
//                cpds.setIdleConnectionTestPeriod(60);
//                cpds.setAcquireRetryAttempts(400);
//                cpds.setAcquireRetryDelay(3000);
//                cpds.setUser(rb.getString("user"));
//                cpds.setPassword(rb.getString("password"));
//            } catch (Exception e) {
//                throw new RuntimeException("Init rdb failed!", e);
//            }
//            System.out.println("Initialize RDB...");
//        }
//    }

    public static Connection getConnection(){
       return connection;
    }


}
