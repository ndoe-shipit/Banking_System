package model;

public abstract class Customer {
    private String customerID;
    private String address;

public Customer(){

}
    public Customer(String customerID, String address) {
        this.customerID = customerID;
        this.address = address;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAddress() {
        return address;
    }
}
