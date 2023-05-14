package it.univr.telemedicina.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Database {
    private final Connection connection;

    public Database(String path) throws SQLException, ClassNotFoundException {
        // load the JDBC driver for SQLite
        Class.forName("org.sqlite.JDBC");

        // establish a connection to the database
        connection = DriverManager.getConnection("jdbc:sqlite:"+path);
    }

    public ArrayList<String> getQuery(String query, String[] columns) throws SQLException {
        Statement statement;
        ResultSet resultSet;

        // create a statement object
        statement = connection.createStatement();

        // execute a SELECT statement and retrieve results
        resultSet = statement.executeQuery(query);

        ArrayList<String> returnList = new ArrayList<>();
        // process the results
        while (resultSet.next()) {
            for(String s : columns)
                returnList.add(resultSet.getString(s));
        }

        resultSet.close();
        statement.close();

        return returnList;
    }



    public void closeAll() throws SQLException{
        // close the result set, statement, and connection
        connection.close();
    }
}