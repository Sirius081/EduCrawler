package main.java.general;

/**
 * Created by sirius-.- on 2015/9/21.
 */
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by binbin on 15/9/18.
 */
public class SimpleConnectionPool {

    private static SimpleConnectionPool simpleConnectionPool;
    private  ComboPooledDataSource dataSource;

    static {

        simpleConnectionPool = new SimpleConnectionPool();
    }

    public SimpleConnectionPool() {
        try {
            ResourceBundle rb= ResourceBundle.getBundle("db");
            dataSource = new ComboPooledDataSource();
            dataSource.setUser(rb.getString("username"));
            dataSource.setPassword(rb.getString("password"));
            dataSource.setJdbcUrl(rb.getString("url"));
            dataSource.setDriverClass(rb.getString("driver"));
            dataSource.setInitialPoolSize(Integer.valueOf(rb.getString("initialpool")));
            dataSource.setMinPoolSize(Integer.valueOf(rb.getString("minpool")));
            dataSource.setMaxPoolSize(Integer.valueOf(rb.getString("maxpool")));
            dataSource.setMaxStatements(Integer.valueOf(rb.getString("maxsize")));
            dataSource.setMaxIdleTime(Integer.valueOf(rb.getString("idleTimeout")));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public final static SimpleConnectionPool getInstance() {
        return simpleConnectionPool;
    }

    public final  Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("无法从数据源获取连接 ", e);
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        try {
            con = SimpleConnectionPool.getInstance().getConnection();
        } catch (Exception e) {
        } finally {
            if (con != null)
                con.close();
        }
    }
}