package edu.sjsu;

import java.sql.*;
import java.util.Scanner;

public class FunctionDriver {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    Scanner scan = new Scanner(System.in);

    public FunctionDriver (Connection conn) {
        this.conn = conn;
    }

    public String showRoomPrice() throws SQLException {
        boolean loop = true;
        String queryResult = null;
        stmt = conn.createStatement();
        while (loop) {
            System.out.println("Enter room type to check price of (1,2,3,4): ");
            String userInput = scan.next();
            if (!userInput.equals("1") && !userInput.equals("2") && !userInput.equals("3") && !userInput.equals("4"))
                System.out.println("Please enter a valid input");
            else {
                String query = "Select price from RoomType where rtype = (" + userInput + ");";
                rs = stmt.executeQuery(query);
                loop = false;
                while (rs.next()) {
                    int price = rs.getInt("price");
                    queryResult = "Price of room type " + userInput + " is $" + price + " per night.";
                }
            }
        }

        return queryResult;
    }

    public String showCurrentTenant() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        System.out.println("Enter the room number to check if occupied: ");
        String userInput = scan.next();
        String query = "Select uID, name from guest where uID = (select uID from booking where rnum = (" + userInput + ") and current_timestamp >= Booking.cid and current_timestamp <= Booking.cod);";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int uID = rs.getInt("uID");
                String name = rs.getString("name");
                queryResult = "Room  " + userInput + " is occupied by User ID: " + uID + ", Name: " + name + "]\n";
            }
            return queryResult;
        }

        return "Room is not occupied.";
    }

    public String showCurrentBooking() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        System.out.println("Enter the user's ID to see bookings: ");
        String userInput = scan.next();
        String query = "Select * from booking where uID = (" + userInput + ");";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int bID = rs.getInt("bID");
                int uID = rs.getInt("uID");
                int rnum = rs.getInt("rnum");
                String cid = rs.getString("cid");
                String cod = rs.getString("cod");
                queryResult = "Booking ID: " + bID + "\nUser ID: " + uID + "\nRoom Number: " + rnum + "\nCheck-in-date: " + cid + "\nCheck-out-date " + cod + "\n";
            }
            return queryResult;
        }

        return "No bookings for current user.";
    }
}
