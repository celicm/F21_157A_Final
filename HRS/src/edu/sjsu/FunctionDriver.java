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

    //Shows the room price depending on user selection
    public String showRoomPrice() throws SQLException {
        boolean loop = true;
        String queryResult = null;
        stmt = conn.createStatement();
        while (loop) {
            System.out.print("Enter room type to check price of (1,2,3,4): ");
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

    //Shows current guest occupying specific room
    public String showCurrentTenant() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        System.out.print("Enter the room number to check if occupied: ");
        String userInput = scan.next();
        String query = "Select uID, name from guest where uID = (select uID from booking where rnum = (" + userInput + ") and current_date >= Booking.cid and current_date <= Booking.cod);";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int uID = rs.getInt("uID");
                String name = rs.getString("name");
                queryResult = "Room  " + userInput + " is occupied by User ID: " + uID + ", Name: " + name + "\n";
            }
            return queryResult;
        }

        return "Room is not occupied.";
    }

    //Shows current bookings under a specific guest
    public String showCurrentBooking() throws SQLException {
        String queryResult;
        int booking_counter = 0;
        stmt = conn.createStatement();
        System.out.print("Enter the user's ID to see bookings: ");
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
                System.out.println(queryResult);
                booking_counter++;
            }
            return booking_counter + " booking(s) displayed.";
        }

        return "No bookings for current user.";
    }

    //Shows check in date for specific booking ID
    public String showGuestCID() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        System.out.print("Please enter the booking ID to see check-in-date: ");
        String userInput = scan.next();
        String query = "Select cID from booking where bID = (" + userInput + ");";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                Timestamp cID = rs.getTimestamp("cID");
                queryResult = "Booking ID of " + userInput + " has check-in-date of " + cID;
            }
            return queryResult;
        }
        return "No check in date found for " + userInput;
    }

    //Show all invoices and details of an invoice under a speicific user ID
    public String showGuestInvoice() throws SQLException {
        String queryResult;
        int invoice_counter = 0;
        stmt = conn.createStatement();
        System.out.print("Please enter a user ID to see all invoices: ");
        String userInput = scan.next();
        String query = "Select * from invoices where bID in (select bID from booking where uID = (" + userInput + "));";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int pID = rs.getInt("pID");
                int bID = rs.getInt("bID");
                int total = rs.getInt("total");
                Timestamp updatedOn = rs.getTimestamp("updatedOn");
                queryResult = "Invoice ID: " + pID + "\nBooking ID: " + bID + "\nTotal: $" + total + "\nUpdated On: " + updatedOn + "\n";
                System.out.println(queryResult);
                invoice_counter++;
            }
            return invoice_counter + " invoice(s) displayed.";
        }

        return "No invoice found for current guest.";
    }

    //Sum of all invoices
    public String calculateBookingCost() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        System.out.print("Please enter the user ID to calculate cost: ");
        String userInput = scan.next();
        String query = "select sum(total) from invoices where bID in (select bID from booking where uID = (" + userInput +  "));";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int total = rs.getInt("sum(total)");
                queryResult = "Total is $" + total + "\n";
            }
            return queryResult;
        }
        return "No invoice found for current guest.";
    }

    //Add a surcharge to an existing reservation
    public String updateAmountOwed() throws SQLException {
        stmt = conn.createStatement();
        System.out.print("Enter booking ID which to update fee: ");
        String bookingInput = scan.next();
        System.out.print("Enter surcharge to add to original bill: ");
        String surchargeInput = scan.next();
        String query = "update invoices set total = total + (" + surchargeInput + ") where bID = (" + bookingInput + ");";
        stmt.executeUpdate(query);

        return "Booking updated with new charges.";
    }

    //Create a new reservation for an existing user
    public String createNewReservationForExistingUser() throws SQLException {
        stmt = conn.createStatement();
        System.out.println("To create a new reservation, fill information below");
        System.out.print("Existing user ID: ");
        String userInput = scan.next();

        System.out.print("Room number: ");
        String roomInput = scan.next();

        System.out.print("Check-in-date: ");
        String cidInput = scan.next();

        System.out.print("Check-out-date: ");
        String codInput = scan.next();

        String query = "insert into booking values ((bID),(" + userInput + "), (" + roomInput + "), ('" + cidInput + "'), ('" + codInput + "'));";
        try {
            stmt.executeUpdate(query);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            return "Room already occupied";
        }
        String validateQuery ="Select rnum from booking where uID = (" + userInput + ");";
        rs = stmt.executeQuery(validateQuery);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int wrappedInput = Integer.parseInt(roomInput);
                int rnum = rs.getInt("rnum");
                if (rnum == wrappedInput)
                    return "Reservation successfully created.";
            }
        }

        return "Unable to create new reservation.";
    }

    //Cancel a guests reservation
    public String cancelGuestReservation() throws SQLException {
        stmt = conn.createStatement();
        System.out.print("Enter the booking ID you wish to cancel: ");
        String userInput = scan.next();
        String query = "delete from booking where bID = (" + userInput + ");";
        stmt.executeUpdate(query);

        return "Successfully cancelled reservation.";
    }

    //Extend a guests reservation by a new date
    public String extendGuestReservation() throws SQLException {
        stmt = conn.createStatement();
        System.out.print("Enter the booking ID you wish to extend: ");
        String bookingID = scan.next();
        System.out.print("Enter the new end date: ");
        String extendedInput = scan.next();
        String query = "update booking set cod = ('" + extendedInput + "') where bID = (" + bookingID + ");";
        stmt.executeUpdate(query);

        return "Successfully extended reservation.";
    }

    //Show the different room types and their rates
    public String showRoomType() throws SQLException {
        String queryResult = null;
        stmt = conn.createStatement();
        String query = "select * from roomtype;";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                int rtype = rs.getInt("rtype");
                int beds = rs.getInt("beds");
                int price = rs.getInt("price");
                queryResult = "Room type " + rtype + " with " + beds + " is $" + price + " per night.";
                System.out.println(queryResult);
            }
        }

        return "Availability pending and surcharges ";
    }

    //Show number of bookings from a guest, including present, past, and future
    public String totalBookingByGuest() throws SQLException {
        int queryResult = 0;
        stmt = conn.createStatement();
        System.out.print("Enter user's ID to check reservation history: ");
        String userInput = scan.next();
        String query = "select count(*) from booking inner join guest on guest.uID = Booking.uID group by Guest.uID having Guest.uID = (" + userInput + ");";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                queryResult = rs.getInt("count(*)");
            }
            return "User ID " + userInput + " has " + queryResult + " bookings";
        }
        return "Unable to find reservation under guest name.";
    }

    //Show all current empty rooms
    public String currentEmptyRooms() throws SQLException {
        int queryResult;
        int roomCount = 0;
        stmt = conn.createStatement();
        String query = "Select * from Room left outer join Booking on Room.rnum = Booking.rnum where Booking.rnum is NULL or not (Booking.cid <= current_timestamp() and Booking.cod >= current_timestamp());";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                queryResult = rs.getInt("rnum");
                System.out.println("Room " + queryResult + " is open.");
                roomCount++;
            }
        }
        return roomCount + " open rooms.";
    }

    //List all user IDs across both bookings and guest tables
    public String listUserID() throws SQLException {
        int queryResult;
        int userIDcounter = 0;
        stmt = conn.createStatement();
        String query = "select uID from Guest union all select uID from booking;";
        rs = stmt.executeQuery(query);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                queryResult = rs.getInt("uID");
                System.out.println("User ID " + queryResult + " is in booking and invoice");
                userIDcounter++;
            }
        }

        return userIDcounter + " user IDs (including duplicates)";
    }

    //Add a new guest to the guest table
    public String createGuest() throws SQLException {
        boolean ageLimit = true;
        int ageInput = 0;
        stmt = conn.createStatement();
        System.out.print("Please enter the full name of new guest: ");
        String nameInput = scan.nextLine();
        while (ageLimit) {
            System.out.print("Please enter age of new guest: ");
            ageInput = scan.nextInt();
            if (ageInput >= 18)
                ageLimit = false;
        }
        String query = "insert into guest values ((uID), ('" + nameInput + "'), (" + ageInput + "));";
        stmt.executeUpdate(query);

        return "Successfully added new guest.";
    }

    //Archive invoices 2 weeks outside of current date and show archived invocies
    //Manually setting the timestamp to +1 month so we can demonstrate archive invoice
    public String archiveInvoices() throws SQLException {
        stmt = conn.createStatement();
        String archiveInvoices = "call ArchInvoice(current_timestamp+INTERVAL 1 MONTH);";
        stmt.executeUpdate(archiveInvoices);
        String showArchivedInvoices = "select * from ArchivedInvoices;";
        rs = stmt.executeQuery(showArchivedInvoices);
        if (rs.isBeforeFirst()) {
            System.out.println("-----------------Archived Invoices-----------------");
            while (rs.next()) {
                int pID = rs.getInt("pID");
                int bID = rs.getInt("bID");
                int total = rs.getInt("total");
                System.out.println("Payment ID " + pID + " with booking ID " + bID + " has total of $" + total);

            }
            return "Successfully archived invoices";
        }
        return "Error archiving invoices";
    }
}

