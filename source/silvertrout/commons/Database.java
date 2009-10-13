/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package silvertrout.commons;
import java.sql.*;
/**
 *
 * @author reggna
 */
public class Database {

    private static Database instance = null;
    private static Connection conn;

    protected Database(){
        try{
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:silvertrout.db");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static Database getInstance(){
        if(instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection(){
        return conn;
    }

}
