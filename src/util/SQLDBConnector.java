package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLDBConnector {

    private Connection connection;
    private String driver;
    private String url;
    private String user;
    private String password;

    public SQLDBConnector(String driver, String url, String user, String password) {
        this.connection = null;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public boolean connect() {
        if (this.connection != null) {
            String errMsg = "Failed to connect to SQL database at " + this.url +
                    ": current connection is still alive.";
            System.out.println(errMsg);
            return false;
        }
        try {
            Class.forName(this.driver);
            this.connection = DriverManager.getConnection(this.url + "?user=" + this.user + "&password=" + this.password);
            return true;
        } catch (Exception e) {
            String errMsg = "Failed to connect to SQL database at " + this.url +
                    ". Error message:\n" + e.getMessage();
            System.out.println(errMsg);
            return false;
        }
    }

    public void disconnect() {
        if (this.connection == null) {
            String errMsg = "Failed to disconnect from SQL database at " + this.url +
                    ": connection does not exist.";
            System.out.println(errMsg);
        }
        try {
            this.connection.close();
            this.connection = null;
        } catch (Exception e) {
            String errMsg = "Error occured while disconnecting from SQL database at " +
                    this.url + ". Error message:\n" + e.getMessage();
            System.out.println(errMsg);
        }
    }

    public ResultSet executeSQL(String SQLCommand) {
        if (this.connection == null) {
            String errMsg = "Failed to disconnect from SQL database at " + this.url +
                    ": connection does not exist.";
            System.out.println(errMsg);
            return null;
        }
        try {
            Statement stmt = this.connection.createStatement();
            stmt.setEscapeProcessing(true);
            stmt.executeQuery(SQLCommand);
            return stmt.getResultSet();
        } catch (Exception e) {
            String errMsg = "Failed to execute SQL query on database at " + this.url +
                    ". Error message:\n" + e.getMessage();
            System.out.println(errMsg);
            return null;
        }
    }

}
