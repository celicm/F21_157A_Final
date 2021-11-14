package edu.sjsu;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/HRS";
    static final String USER = "root";
    static final String PASS = "password";

    public static void main (String[] args){
        Scanner scan = new Scanner(System.in);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String userInput = null;
        boolean loop = true;

        try {
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("---------------------------------------");
            System.out.println("Welcome to the Hotel Reservation System\n");
            FunctionDriver fd = new FunctionDriver(conn);
            String qResult = "";

            while (loop) {
                System.out.print("Please type the number of the , if you want to exit type 'exit'\n");
                System.out.print("1. Show the price of a room type.\n" +
                        "2. Show current room tenant.\n" +
                        "3. Show current booking for guest\n");
                userInput = scan.next();
                if (userInput.equals("exit"))
                    loop = false;
                else{
                    switch (userInput) {
                        case "1":
                            qResult = fd.showRoomPrice();
                            System.out.println(qResult);
                        break;
                        case "2":
                            qResult = fd.showCurrentTenant();
                            System.out.println(qResult);
                        break;
                        case "3":
                            qResult = fd.showCurrentBooking();
                            System.out.println(qResult);
                        break;
                    }
                }
            }

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
