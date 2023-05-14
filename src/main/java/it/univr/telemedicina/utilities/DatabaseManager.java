package it.univr.telemedicina.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:/home/davide/Scrivania/Uni/Secondo_anno/Ingegneria_Software/TeleMedicina/ospedale.db";
    /**
     * Restituisce una connessione al database.
     *
     * @return la connessione al database
     * @throws SQLException se si verifica un errore durante la connessione
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC non trovato.");
        } catch (SQLException e) {
            throw new SQLException("Errore di connessione al database.", e);
        }
    }

    public static Connection getConnection2() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/davide/Scrivania/Uni/Secondo_anno/Ingegneria_Software/TeleMedicina/codiceCatastale.db");
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC non trovato.");
        } catch (SQLException e) {
            throw new SQLException("Errore di connessione al database.", e);
        }
    }

}
