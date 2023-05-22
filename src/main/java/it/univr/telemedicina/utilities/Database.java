package it.univr.telemedicina.utilities;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

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
        statement.close();
    }

        /*UPDATE Customers
    SET ContactName = 'Alfred Schmidt', City= 'Frankfurt'
    WHERE CustomerID = 1;
    */


    public void updateQuery(String nameTable, Map<String, Object> setValues, Map<String, Object> whereValues) throws SQLException{
        // add nametable + SET
        StringBuilder query = new StringBuilder("UPDATE ").append(nameTable).append(" SET " );
        Statement statement;

        for(String key : setValues.keySet()) {
            query.append(key).append(" = ");
            if(setValues.get(key) instanceof String || setValues.get(key) instanceof LocalDate)
                query.append("\"").append(setValues.get(key)).append("\"");
             else
                query.append(setValues.get(key).toString());
             query.append(",");
        }
        query.deleteCharAt(query.length()-1).append(" WHERE ");

        for(String key : whereValues.keySet()) {
            query.append(key).append(" = ");
            if(whereValues.get(key) instanceof String || whereValues.get(key) instanceof LocalDate)
                query.append("\"").append(whereValues.get(key)).append("\"");
            else
                query.append(whereValues.get(key).toString());
            query.append(" AND ");
        }

        query.delete(query.length()-5, query.length()-1);
        query.append(";");

        System.out.println(query.toString());

        // create a statement object
        statement = connection.createStatement();

        // execute a SELECT statement and retrieve results
        statement.executeUpdate(query.toString());

        statement.close();
    }

    public void deleteQuery (String nameTable, Map<String,Object> conditionValue) throws SQLException {
        StringBuilder query = new StringBuilder("DELETE FROM ").append(nameTable).append(" WHERE ");
        Statement statement;
        int flag = 0;
        for(String s : conditionValue.keySet()){
            //Condition for insert AND (Skip the first time)
            if(flag != 0){
                query.append(" AND ");
            }
            else
                flag++;
            query.append(s + " = '" + conditionValue.get(s) + "'");
        }
        System.out.println(query.toString());
        // create a statement object
        statement = connection.createStatement();

        // execute a SELECT statement and retrieve results
        statement.executeUpdate(query.toString());

        statement.close();
    }

    public void closeAll() throws SQLException{
        // close the result set, statement, and connection
        connection.close();
    }
}