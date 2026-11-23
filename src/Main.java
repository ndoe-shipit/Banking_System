import controller.AccountController;
import controller.CustomerController;
import controller.TransactionController;
import model.*;

import java.util.List;
import java.util.Scanner;


public class Main  {
    private static final CustomerController customerController = new CustomerController();
    private static final AccountController accountController = new AccountController();
    private static final TransactionController transactionController = new TransactionController();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Banking System ===");
            System.out.println("1. Add customer");
            System.out.println("2. List customers");
            System.out.println("3. Delete customer");
            System.out.println("4. Open account");
            System.out.println("5. List accounts");
            System.out.println("6. Deposit");
            System.out.println("7. Withdraw");
            System.out.println("8. Pay monthly interest");
            System.out.println("9. Transaction history");
            System.out.println("0. Exit");
            String choice = ask("Choose selection");
            try {
                switch (choice) {
                    case "1" -> addCustomer();
                    case "2" -> listCustomers();
                    case "3" -> deleteCustomer();
                    case "4" -> openAccount();
                    case "5" -> listAccounts();
                    case "6" -> deposit();
                    case "7" -> withdraw();
                    case "8" -> payInterest();
                    case "9" -> showHistory();
                    case "0" -> {
                        System.out.println("Goodbye");
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static String ask(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    private static double askDouble(String label) {
        return Double.parseDouble(ask(label));
    }


    private static void addCustomer() throws Exception {
        String type = ask("Type (1 = Individual, 2 = Organisation)");
        String id = ask("Customer ID");
        String address = ask("Address");
        if (type.equals("1")) {
            String first = ask("First name");
            String surname = ask("Surname");
            customerController.addIndividualCustomer(new IndividualCustomer(id, address, first, surname));
        } else if (type.equals("2")) {
            String name = ask("Registered name");
            String regNo = ask("Registration number");
            customerController.addOrganisationCustomer(new OrganisationCustomer(id, address, name, regNo));
        } else {
            System.out.println("Invalid type");
            return;
        }
        System.out.println("Customer added");
    }

    private static void listCustomers() throws Exception {
        List<Customer> customers = customerController.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("There are no customers in the system");
            return;
        }
        for (Customer c : customers) System.out.println(c);
    }

    private static void deleteCustomer() throws Exception {
        customerController.deleteCustomer(ask("Customer ID to delete"));
        System.out.println("Customer deleted");
    }

    // ---------- accounts ----------
    private static void openAccount() throws Exception {
        Customer customer = customerController.getCustomerByID(ask("Customer ID"));
        if (customer == null) {
            System.out.println("Customer not found. Add the customer first.");
            return;
        }
        String type = ask("Account type (1 = Savings, 2 = Investment, 3 = Cheque)");
        String number = ask("Account number");
        String branch = ask("Branch");
        double balance = askDouble("Opening deposit");

        switch (type) {
            case "1" -> accountController.addSavingsAccount(new SavingsAccount(number, balance, branch, customer));
            case "2" -> accountController.addInvestmentAccount(new InvestmentAccount(number, balance, branch, customer));
            case "3" -> {
                String employer = ask("Employer name");
                String employerAddress = ask("Employer address");
                // your constructor order: ..., employerAddress, employerName
                accountController.addChequeAccount(
                        new ChequeAccount(number, balance, branch, customer, employerAddress, employer));
            }
            default -> {
                System.out.println("Invalid type");
                return;
            }
        }
        System.out.println("Account opened");
    }

    private static void listAccounts() throws Exception {
        List<Account> accounts = accountController.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("There are no accounts in the system");
            return;
        }
        for (Account a : accounts) {
            System.out.printf("%-10s %-11s customer=%-8s branch=%-10s balance=BWP %.2f%n",
                    a.getAccountNumber(), accountController.getAccountType(a),
                    a.getCustomer().getCustomerID(), a.getBranch(), a.getBalance());
        }
    }

    private static void deposit() throws Exception {
        String number = ask("Account number");
        accountController.deposit(number, askDouble("Amount"));
        System.out.println("Deposit successful");
    }

    private static void withdraw() throws Exception {
        String number = ask("Account number");
        accountController.withdraw(number, askDouble("Amount"));
        System.out.println("Withdrawal successful");
    }

    private static void payInterest() throws Exception {
        System.out.println("Interest paid to " + accountController.payInterest() + " account(s)");
    }

    private static void showHistory() throws Exception {
        List<Transaction> list = transactionController.getTransactionsByAccount(ask("Account number"));
        if (list.isEmpty()) {
            System.out.println("No transactions");
            return;
        }
        for (Transaction t : list) {
            System.out.printf("%s  %-10s BWP %.2f  (%s)%n",
                    t.getTimestamp(), t.getType(), t.getAmount(), t.getTransactionID());
        }
    }
}