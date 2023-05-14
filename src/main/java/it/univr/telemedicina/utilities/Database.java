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
    private String path1 = "codiceCatastale.db";
    private String path2 = "hospital.db";

    public Database(int nPath) throws SQLException, ClassNotFoundException {
        // load the JDBC driver for SQLite
        Class.forName("org.sqlite.JDBC");

        // establish a connection to the database
        connection = DriverManager.getConnection("jdbc:sqlite:" + ((nPath == 1)? path1 : path2) );
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

    public void insertQuery(String nameTable, String[] fieldName, String[] Values) throws SQLException{
        StringBuilder query = new StringBuilder("INSERT INTO");
        Statement statement;
        ResultSet resultSet;

        // add nametable + (
        query.append(nameTable).append(" (" );
        // add field
        for(String s : fieldName){
            query.append(s).append(",");
        }
        //remove for the last field ',' and add ') VALUES'
        query.deleteCharAt(query.length()-1).append(") VALUES(");

        for(String s : Values){
            query.append("'").append(s).append("'").append(",");
        }

        query.deleteCharAt(query.length()-1).append(");");

        System.out.println(query);

        // create a statement object

        //statement = connection.createStatement();

        //INSERT INTO name Table (Nomi Campi) Values(valori);

    }



    public void closeAll() throws SQLException{
        // close the result set, statement, and connection
        connection.close();
    }
}