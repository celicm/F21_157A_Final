package edu.sjsu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FunctionDriver {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public FunctionDriver (Connection conn, Statement stmt, ResultSet rs) {
        this.conn = conn;
        this.stmt = stmt;
        this.rs = rs;
    }


}
