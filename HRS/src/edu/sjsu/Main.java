package edu.sjsu;

import java.sql.*;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/HRS";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main (String[] args){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean loop = true;

        try {
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("---------------------------------------");
            System.out.println("Welcome to the Hotel Reservation System\n");
            System.out.print("Please choose an option below\n");

            /*
            System.out.print("1. View if room ")

            while (loop) {


            }

             */

        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (stmt != null)
                    stmt.close();
            }
            catch (SQLException se2) {}
            try {
                if (conn != null)
                    conn.close();
            }
            catch (SQLException se3) {
                se3.printStackTrace();
            }
        }
        System.out.println("Closing connection...");
    }
}
