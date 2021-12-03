package edu.sjsu;

import java.sql.*;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/HRS";
    static final String USER = "root";
    static final String PASS = "password";
    static final String OPTIONS = "1. Show the price of a room type.\n" +
            "2. Show current room tenant.\n" +
            "3. Show current booking for guest\n" +
            "4. Show guest's check-in time\n" +
            "5. Show guest's invoice\n" +
            "6. Caclulate total cost for booking\n" +
            "7. Update amount owed for guest.\n" +
            "8. Create new reservation\n" +
            "9. Cancel guest's reservation\n" +
            "10. Extend guest's reservation\n" +
            "11. Show guest type of rooms\n" +
            "12. Search for reservation by guest name\n" +
            "13. Show how many rooms guest has checked out\n" +
            "14. Change hotel room for guest booking\n" +
            "15. Show which guest are leaving by date\n";

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
            System.out.print("Please type the number of the selection to proceed, if you want to exit type 'exit'\n");
            System.out.print(OPTIONS);

            while (loop) {
                System.out.print("Selection: ");
                userInput = scan.next();
                if (userInput.equalsIgnoreCase("exit"))
                    loop = false;
                else if (userInput.equalsIgnoreCase("help"))
                    System.out.println(OPTIONS);
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
                        case "4":
                            qResult = fd.showGuestCID();
                            System.out.println(qResult);
                            break;
                        case "5":
                            qResult = fd.showGuestInvoice();
                            System.out.println(qResult);
                            break;
                        case "6":
                            qResult = fd.calculateBookingCost();
                            System.out.println(qResult);
                            break;
                        case "7":
                            qResult = fd.updateAmountOwed();
                            System.out.println(qResult);
                            break;
                        case "8":
                            qResult = fd.createNewReservationForExistingUser();
                            System.out.println(qResult);
                            break;
                        case "9":
                            qResult = fd.cancelGuestReservation();
                            System.out.println(qResult);
                            break;
                        case "10":
                            qResult = fd.extendGuestReservation();
                            System.out.println(qResult);
                            break;
                        case "11":
                            qResult = fd.showRoomType();
                            System.out.println(qResult);
                            break;
                        case "12":
                            qResult = fd.showReservationByGuestName();
                            System.out.println(qResult);
                            break;
                        case "13":
                            qResult = fd.showNumberOfCheckedRooms();
                            System.out.println(qResult);
                            break;
                        case "14":
                            qResult = fd.changeBookedRoom();
                            System.out.println(qResult);
                            break;
                        case "15":
                            qResult = fd.showCOD();
                            System.out.println(qResult);
                            break;

                    }
                }
                System.out.println("Enter another selection to continue, type 'help' for options, type 'exit' to exit");
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
