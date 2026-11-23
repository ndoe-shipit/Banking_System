package model;

public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;

    public IndividualCustomer(String customerID, String address, String firstName, String surname) {
        super(customerID, address);
        this.firstName = firstName;
        this.surname = surname;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }
    @Override
    public String toString() {
        return "IndividualCustomer{" +
                "customerID='" + getCustomerID() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", address='" + getAddress() + '\'' +
                '}';
    }
}
