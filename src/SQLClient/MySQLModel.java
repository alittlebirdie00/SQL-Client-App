package SQLClient;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.swing.table.AbstractTableModel;
import java.sql.*;

public class MySQLModel extends AbstractTableModel {
    public MysqlDataSource dataSource;
    public Connection connection;
    public Statement statement;
    public ResultSet resultSet;
    public ResultSetMetaData resultSetMetaData;

    public int numberOfRows;

    public boolean connectedToDatabase = false;
    public String[] driverOptions;
    public String [] databaseURLOptions;

    MySQLModel() {
        driverOptions = new String[1];
        driverOptions[0] = "com.mysql.jdbc.MySQLConnection";

        databaseURLOptions = new String[1];
        databaseURLOptions[0] = "jdbc:mysql://localhost:3306/project3";
    }

    boolean connectToDatabase(String username, String password, String url) {
        try {
            // Load the JDBC driver
            Class.forName(driverOptions[0]);
            System.out.println("Driver loaded");

            dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setURL(url);

            connection = dataSource.getConnection();

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Keep track of connection to database
            connectedToDatabase = true;
            return connectedToDatabase;
        } catch (Exception e) {
            System.out.println(e);
            return connectedToDatabase;
        }
    }

    void executeSQLQuery(String query) {
        //if (!connectedToDatabase) throw new IllegalStateException("Not Connected to Database");
        try {
            Statement s = connection.createStatement();
            resultSet = s.executeQuery(query);
            resultSetMetaData = resultSet.getMetaData();

            resultSet.last();
            numberOfRows = resultSet.getRow();
            System.out.println(resultSetMetaData.getColumnName(1));

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement
    // or an SQL statement that returns nothing, such as an SQL DDL statement.
    void executeSQLUpdate(String update) {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database.");

        try {
            int res = statement.executeUpdate(update);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public Object getValueAt(int row, int column) throws IllegalStateException {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database.");
        try {
            resultSet.next();
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sqlexception) {
            sqlexception.printStackTrace();
        }
        return "";
    }

    public Class getColumnClass(int column) throws IllegalStateException {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database.");
        try {
            String className = resultSetMetaData.getColumnClassName(column + 1);
            return Class.forName(className);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // If problems occur above, assume type Object
        return Object.class;
    }

    public int getColumnCount() throws IllegalStateException {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database.");
        try {
            return resultSetMetaData.getColumnCount();
        } catch (SQLException sqlexception) {
            sqlexception.printStackTrace();
        }

        // If problems above occur, return 0
        return 0;
    }

    public String getColumnName(int column) throws IllegalStateException {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database.");
        try {
            String columnName = resultSetMetaData.getColumnName(column + 1);
        } catch (SQLException sqlexception) {
            sqlexception.printStackTrace();
        }
        // If problems occur above, default to ""
        return "";
    }

    public int getRowCount() throws IllegalStateException {
        if (!connectedToDatabase) throw new IllegalStateException("Not Connected to a Database");
        return numberOfRows;
    }

    void disconnectFromDatabase() {
        if (!connectedToDatabase) return;
        try {
            statement.close();
            connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            connectedToDatabase = false;
        }
    }
}
