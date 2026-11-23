package dao;

import model.Customer;
import model.IndividualCustomer;
import model.OrganisationCustomer;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    private static final String Selectbase = "SELECT c.customerID, c.address, " +
            "i.customerID AS individualKey, i.firstName, i.surname, " +
            "o.customerID AS organisationKey, o.registeredName, o.registrationNumber " +
            "FROM customer c " +
            "LEFT JOIN individualcustomer i ON i.customerID = c.customerID " +
            "LEFT JOIN organisationcustomer o ON o.customerID = c.customerID";

    //add customer
    public void addCustomer(Customer customer) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String basesql = "INSERT INTO customer(customerID, address) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(basesql)) {
                    stmt.setString(1, customer.getCustomerID());
                    stmt.setString(2, customer.getAddress());
                    stmt.executeUpdate();
                }
                if (customer instanceof OrganisationCustomer organisation) {
                    String sql = "INSERT INTO organisationcustomer (customerID, registeredName, registrationNumber) VALUES (?,?,?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, organisation.getCustomerID());
                        stmt.setString(2, organisation.getRegisterdName());
                        stmt.setString(3, organisation.getRegistrationNumber());
                        stmt.executeUpdate();//if you are altering data(INSERT,UPDATE,DELETE) it returns an integer showing how many rows changed
                    }
                } else if (customer instanceof IndividualCustomer individual) {
                    String sql = "INSERT INTO individualcustomer (customerID, firstName, surname) VALUES (?,?,?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, individual.getCustomerID());
                        stmt.setString(2, individual.getFirstName());
                        stmt.setString(3, individual.getSurname());
                        stmt.executeUpdate();
                    }

                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    //select by if
    public Customer selectCustomerByID(String customerID) throws SQLException {
        String sql = Selectbase + " WHERE c.customerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }//executeQuery if looking for data (SELECT) it returns a resultset
        return null;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        String customerID = rs.getString("customerID");
        String address = rs.getString("address");

        if (rs.getString("individualKey") != null) {
            return new IndividualCustomer(customerID, address, rs.getString("firstName"), rs.getString("surname"));

        } else if (rs.getString("organisationKey") != null) {
            return new OrganisationCustomer(customerID, address, rs.getString("registeredName"), rs.getString("registrationNumber"));
        } else {
            throw new SQLException("Customer " + customerID + " not found");
        }

    }

    //select all customers
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Selectbase);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = mapRow(rs);
                customers.add(customer);
            }
        }
        return customers;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String baseUpdate = "UPDATE customer SET address = ? WHERE customerID = ?";
            try {
                try (PreparedStatement stmt = conn.prepareStatement(baseUpdate)) {
                    stmt.setString(1, customer.getAddress());
                    stmt.setString(2, customer.getCustomerID());
                    stmt.executeUpdate();
                }
                if (customer instanceof IndividualCustomer individual) {
                    String sql = "UPDATE individualcustomer SET firstName = ?, surname = ? WHERE customerID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, individual.getFirstName());
                        stmt.setString(2, individual.getSurname());
                        stmt.setString(3, individual.getCustomerID());
                        stmt.executeUpdate();
                    }
                } else if (customer instanceof OrganisationCustomer organisation) {
                    String sql = "UPDATE organisationcustomer SET registeredName = ?, registrationNumber =? WHERE customerID = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, organisation.getRegisterdName());
                        stmt.setString(2, organisation.getRegistrationNumber());
                        stmt.setString(3, organisation.getCustomerID());
                        stmt.executeUpdate();
                    }
                }

               conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        }
    }

    public void deleteCustomer(String customerID) throws SQLException {
        String sql ="DELETE FROM customer WHERE customerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerID);
           stmt.executeUpdate();
        }
    }
}


