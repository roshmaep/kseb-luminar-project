package com.kseb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/addwrk")
public class AddWrkServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection con = null;
        DBConnection dbcon = new DBConnection();

        try {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();

            con = dbcon.getConnection();

            // Get parameters from the request
            String date = request.getParameter("date");
            String complaintNoStr = request.getParameter("complaintno");
            String lineman = request.getParameter("lineman");

            if (date == null || complaintNoStr == null || lineman == null) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("error", "Invalid parameters");
                out.print(errorResponse.toString());
                return;
            }


            // Parse complaintNo as an integer
            int complaintNo = Integer.parseInt(complaintNoStr);

            // Insert data into the database
            insertDataIntoDatabase(con, date, complaintNo, lineman);

            // Fetch Complaint No options
            JSONArray complaintOptions = getOptionsFromDatabase(con, "select complaint_id from complaintregister");

            // Fetch Line Man options
            JSONArray linemanOptions = getOptionsFromDatabase(con, "select lineman_name from lineman");
            
            // Debugging print statements
            System.out.println("Servlet called");
            System.out.println("Complaint Options: " + complaintOptions);
            System.out.println("Lineman Options: " + linemanOptions);

            // Create a JSONObject to hold both arrays
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("complaintOptions", complaintOptions);
            jsonResponse.put("linemanOptions", linemanOptions);

            // Send the JSON response back to the client-side
            out.print(jsonResponse.toString());

        } catch (SQLException sql) {
            sql.printStackTrace();
        } catch (NumberFormatException | JSONException e) {
            e.printStackTrace();
        } finally {
            // Close the connection in the finally block
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private JSONArray getOptionsFromDatabase(Connection con, String query) throws SQLException {
        JSONArray options = new JSONArray();
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                options.put(rs.getString(1));
            }
        }
        return options;
    }

    private void insertDataIntoDatabase(Connection con, String date, int complaintNo, String lineman) throws SQLException {
        // Adjust this query based on your table structure
        String insertQuery = "INSERT INTO work_allocation (date,lineman_name, complaint_id) VALUES (?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
            pst.setString(1, date);
            pst.setString(2, lineman);
            pst.setInt(3, complaintNo);
           

            pst.executeUpdate();
        }
    }
}
