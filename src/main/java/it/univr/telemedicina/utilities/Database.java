package it.univr.telemedicina.utilities;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class Database {
    private final Connection connection;
    private String path1 = "codiceCatastale.db";
    private String path2 = "hospital.db";

    /***
     * Create a database instance and establish a connection.
     * @param nPath Path of database
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Database(int nPath) throws SQLException, ClassNotFoundException {
        // load the JDBC driver for SQLite
        Class.forName("org.sqlite.JDBC");

        // establish a connection to the database
        connection = DriverManager.getConnection("jdbc:sqlite:" + ((nPath == 1)? path1 : path2));
    }

    /***
     * Executes a SELECT query and save the results.
     * @param query SQL query to execute
     * @param columns An array of column names to save from the result set
     * @return  An ArrayList containing the values saved from the specified columns
     * @throws SQLException
     */
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

    /***
     * Executes a INSERT INTO query
     * @param nameTable name of table
     * @param fieldName array of all field name to use in the query
     * @param Values    array of all values to use in the query (relative with field)
     * @throws SQLException
     */
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

    /***
     * Executes a UPDATE query to modify records in a table.
     * @param nameTable name of table
     * @param setValues    a map containing the column names and new values to set
     * @param whereValues  a map containing the column names and values for the WHERE clause
     * @throws SQLException
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


        // create a statement object
        statement = connection.createStatement();

        // execute a SELECT statement and retrieve results
        statement.executeUpdate(query.toString());

        statement.close();
    }

    /**
     *
     * @param nameTable name of table
     * @param conditionValue a map containing the column names and values to delete
     * @throws SQLException
     */
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

        // create a statement object
        statement = connection.createStatement();

        // execute a SELECT statement and retrieve results
        statement.executeUpdate(query.toString());

        statement.close();
    }

    /**
     * Close connection
     * @throws SQLException
     */
    public void closeAll() throws SQLException{
        // close the result set, statement, and connection
        connection.close();
    }
}