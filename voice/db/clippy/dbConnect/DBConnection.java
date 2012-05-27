/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.clippy.dbConnect;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ray
 */
public class DBConnection {

    private static DBConnection dbc = new DBConnection();
    public String framework = "embedded";
    public String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    public String protocol = "jdbc:derby:";

    private DBConnection() {
        try {
            this.InitialDB();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DBConnection getInstance() {
        return dbc;
    }

    public Connection getConnect() {
        Connection conn = null;

        Properties props = new Properties();
        props.put("user", "clippy2");
        props.put("password", "clippy2");

        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(protocol + "ClippyDB;create=true", props);
        } catch (Exception e) {
        }
        return conn;
    }

    /**
     * Initial Database
     * @throws SQLException 
     */
    private void InitialDB() throws SQLException {
        Connection con = this.getConnect();

        DatabaseMetaData dma = con.getMetaData();
        String[] types = new String[1];
        types[0] = "TABLE";
        ResultSet rs = dma.getTables(null, null, "%", types);
        
        boolean exist = false;
        // check whether tables are exist
        while (rs.next()) {
            String tableName = rs.getString(3);
            // Add more tables if necessary
            if ("USERDB".equals(tableName.toUpperCase()) || "USERINFO".equals(tableName.toUpperCase())) {
                exist = true;
            }
        }
        // if not exist then create table
        if (!exist) {
            createTable();
        }
        
    }
    
    /**
     * create table by using SQL statements
     * userDB : "uid" unique id for each user;
     *           "username" user's name;
     *           "password" user's password (optional?);
     * userinfo : "uid" foreign key associate with userDB's "uid";
     *             "preference_type" user's preference type eg.browser, search engine;
     *             "preference" preference value;
     * @throws SQLException 
     */
    private void createTable() throws SQLException {
        try (Connection con = this.getConnect(); Statement s = con.createStatement()) {
            s.execute("create table userDB(uid varchar(50), username varchar(50), password varchar(50),unique(username))");
            s.execute("create table userInfo(uid varchar(50), preference_type varchar(50), preference varchar(100),unique(uid))");
        }
    }
    
}
