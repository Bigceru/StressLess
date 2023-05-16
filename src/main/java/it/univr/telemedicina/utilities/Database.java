package it.univr.telemedicina.utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Database {
    private final Connection connection;
    private String path1 = "codiceCatastale.db";
    private String path2 = "hospital.db";

    public Database(int nPath) throws SQLException, ClassNotFoundException {
        // load the JDBC driver for SQLite
        Class.forName("org.sqlite.JDBC");

        // establish a connection to the database
        connection = DriverManager.getConnection("jdbc:sqlite:" + ((nPath == 1)? path1 : path2));
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
            for(String s : columns) {
                returnList.add(resultSet.getString(s));
            }
        }

        resultSet.close();
        statement.close();

        return returnList;
    }

    public void insertQuery(String nameTable, String[] fieldName, Object[] Values) throws SQLException{

        StringBuilder query = new StringBuilder("INSERT INTO ");
        PreparedStatement statement;

        // add nametable + (
        query.append(nameTable).append(" (" );

        // add field
        for(String s : fieldName){
            query.append(s).append(",");
        }

        //remove for the last field ',' and add ') VALUES'
        query.deleteCharAt(query.length()-1).append(") VALUES(");

        for(Object s : Values){
            query.append("?").append(",");
        }

        query.deleteCharAt(query.length()-1).append(");");

        // change "?" with my values
        statement = connection.prepareStatement(query.toString());
        for(int i = 0; i< Values.length;i++){
            statement.setObject(i+1, Values[i]);
        }

        // Add Line in Database
         statement.executeUpdate();
    }

    public void closeAll() throws SQLException{
        // close the result set, statement, and connection
        connection.close();
    }
}