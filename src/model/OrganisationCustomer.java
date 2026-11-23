package model;

public class OrganisationCustomer extends Customer {
    private String registeredName;
    private String registrationNumber;

    public OrganisationCustomer(String customerID, String address, String registerdName, String registrationNumber) {
        super(customerID, address);
        this.registeredName = registerdName;
        this.registrationNumber = registrationNumber;
    }

    public void setRegisterdName(String registerdName) {
        this.registeredName = registerdName;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegisterdName() {
        return registeredName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @Override
    public String toString() {
        return "OrganisationCustomer{" +
                "customerID='" + getCustomerID() + '\'' +
                ", registeredName='" + registeredName + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", address='" + getAddress() + '\'' +
                '}';
    }
}
