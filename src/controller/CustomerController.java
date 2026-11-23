package controller;

import dao.CustomerDao;
import model.Customer;
import model.IndividualCustomer;
import model.OrganisationCustomer;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private final CustomerDao customerDao = new CustomerDao();

    public void addIndividualCustomer(IndividualCustomer individual) throws SQLException {
        validateCommon(individual);
        require(individual.getFirstName(), "First name");
        require(individual.getSurname(), "Surname");
        rejectDuplicate(individual.getCustomerID());
        customerDao.addCustomer(individual);
    }

    public void addOrganisationCustomer(OrganisationCustomer organisation) throws SQLException {
        validateCommon(organisation);
        require(organisation.getRegisterdName(), "Registered name");
        require(organisation.getRegistrationNumber(), "Registration number");
        rejectDuplicate(organisation.getCustomerID());
        customerDao.addCustomer(organisation);
    }

    public Customer getCustomerByID(String customerID) throws SQLException {
        return customerDao.selectCustomerByID(customerID);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDao.getAllCustomers();
    }

    public void updateIndividualCustomer(IndividualCustomer individual) throws SQLException {
        validateCommon(individual);
        require(individual.getFirstName(), "First name");
        require(individual.getSurname(), "Surname");
        mustExist(individual.getCustomerID());
        customerDao.updateCustomer(individual);
    }

    public void updateOrganisationCustomer(OrganisationCustomer organisation) throws SQLException {
        validateCommon(organisation);
        require(organisation.getRegisterdName(), "Registered name");
        require(organisation.getRegistrationNumber(), "Registration number");
        mustExist(organisation.getCustomerID());
        customerDao.updateCustomer(organisation);
    }

    public void deleteCustomer(String customerID) throws SQLException {
        mustExist(customerID);
        customerDao.deleteCustomer(customerID);
    }

    // ---------- validation helpers ----------
    private void validateCommon(Customer c) {
        require(c.getCustomerID(), "Customer ID");
        require(c.getAddress(), "Address");
    }

    private void require(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(field + " is required");
    }

    private void rejectDuplicate(String id) throws SQLException {
        if (customerDao.selectCustomerByID(id) != null)
            throw new IllegalArgumentException("Customer ID already exists: " + id);
    }

    private void mustExist(String id) throws SQLException {
        if (customerDao.selectCustomerByID(id) == null)
            throw new IllegalArgumentException("Customer not found: " + id);
    }
}