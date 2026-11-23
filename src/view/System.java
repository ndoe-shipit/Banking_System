package view;

import controller.AccountController;
import controller.BankTellerController;
import controller.CustomerController;
import controller.TransactionController;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

public class System extends Application {
    // 1. Controllers
    private final CustomerController customerController = new CustomerController();
    private final AccountController accountController = new AccountController();
    private final TransactionController transactionController = new TransactionController();
    private final BankTellerController bankTellerController = new BankTellerController();

    // 2. Main Layout Container & State
    private final BorderPane root = new BorderPane();
    private String selectedAccountNumber = null;
    private BankTeller loggedInTeller = null;

    // 3. Helper Method for Pop-up Messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public void start(Stage stage) {
        Scene loginScene = new Scene(buildLoginView(stage), 460, 380);
        if (getClass().getResource("dashboard.css") != null) {
            loginScene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        }

        stage.setTitle("Banking System - Teller Login");
        stage.setScene(loginScene);
        stage.show();
    }

    // ---------- LOGIN VIEW ----------
    private VBox buildLoginView(Stage stage) {
        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(30));
        wrapper.getStyleClass().add("login-wrapper");

        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(360);
        card.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Banking System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #241438;");

        Label subtitle = new Label("Sign in with your Teller ID and Password");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        TextField tellerIdField = new TextField();
        tellerIdField.setPromptText("Enter Teller ID (e.g., T001)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("primary-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        passwordField.setOnAction(e -> loginBtn.fire());

        loginBtn.setOnAction(e -> {
            try {
                loggedInTeller = bankTellerController.login(
                        tellerIdField.getText(),
                        passwordField.getText()
                );
                showMainDashboard(stage);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", ex.getMessage());
            }
        });

        card.getChildren().addAll(
                title, subtitle,
                new Label("Teller ID:"), tellerIdField,
                new Label("Password:"), passwordField,
                loginBtn
        );

        wrapper.getChildren().add(card);
        return wrapper;
    }

    private void showMainDashboard(Stage stage) {
        root.setTop(buildTopBar());
        root.setLeft(buildSidebar());

        Label welcome = new Label("Search a Customer ID or Account Number on the left, or choose an action.");
        welcome.setStyle("-fx-font-size: 15px; -fx-text-fill: #6b7280;");
        root.setCenter(new StackPane(welcome));

        Scene mainScene = new Scene(root, 1150, 750);
        if (getClass().getResource("dashboard.css") != null) {
            mainScene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        }

        stage.setTitle("Banking System");
        stage.setScene(mainScene);
        stage.centerOnScreen();
    }

    // ---------- TOP BAR ----------
    private HBox buildTopBar() {
        HBox topBar = new HBox(15);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label portalTitle = new Label("Teller Portal");
        portalTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String tellerText = (loggedInTeller != null)
                ? "Logged in as: Teller " + loggedInTeller.getTellerID()
                : "Logged in as: Bank Teller";

        Label tellerBadge = new Label(tellerText);
        tellerBadge.getStyleClass().add("teller-badge");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("danger-btn");
        logoutBtn.setOnAction(e -> {
            loggedInTeller = null;
            selectedAccountNumber = null;
            Stage currentStage = (Stage) topBar.getScene().getWindow();
            start(currentStage);
        });

        topBar.getChildren().addAll(portalTitle, spacer, tellerBadge, logoutBtn);
        return topBar;
    }

    // ---------- LEFT SIDEBAR ----------
    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");

        Label header = new Label("Banking System");
        header.getStyleClass().add("sidebar-title");

        TextField searchField = new TextField();
        searchField.setPromptText("Search ID or Account #...");
        searchField.getStyleClass().add("sidebar-search");
        searchField.setOnAction(e -> handleSearch(searchField.getText().trim()));

        Label custSection = new Label("CUSTOMERS");
        custSection.getStyleClass().add("sidebar-section-label");

        Button btnAddCust = createSidebarButton("Add Customer");
        Button btnUpdateCust = createSidebarButton("Update Customer");
        Button btnDeleteCust = createSidebarButton("Delete Customer");

        Label accSection = new Label("ACCOUNTS");
        accSection.getStyleClass().add("sidebar-section-label");

        Button btnAddAcc = createSidebarButton("Add Account");
        Button btnUpdateAcc = createSidebarButton("Update Account");
        Button btnDeleteAcc = createSidebarButton("Delete Account");

        btnAddCust.setOnAction(e -> root.setCenter(buildCustomerForm("ADD")));
        btnUpdateCust.setOnAction(e -> root.setCenter(buildCustomerForm("UPDATE")));
        btnDeleteCust.setOnAction(e -> root.setCenter(buildDeleteCustomerForm()));
        btnAddAcc.setOnAction(e -> root.setCenter(buildAddAccountForm()));
        btnUpdateAcc.setOnAction(e -> root.setCenter(buildUpdateAccountForm()));
        btnDeleteAcc.setOnAction(e -> root.setCenter(buildDeleteAccountForm()));

        sidebar.getChildren().addAll(
                header, searchField,
                custSection, btnAddCust, btnUpdateCust, btnDeleteCust,
                accSection, btnAddAcc, btnUpdateAcc, btnDeleteAcc
        );
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private void handleSearch(String query) {
        if (query.isEmpty()) return;

        try {
            Customer customer = customerController.getCustomerByID(query);
            if (customer != null) {
                selectedAccountNumber = null;
                root.setCenter(buildCustomerOverview(customer));
                return;
            }

            Account account = accountController.getAccountByNumber(query);
            if (account != null) {
                selectedAccountNumber = account.getAccountNumber();
                root.setCenter(buildCustomerOverview(account.getCustomer()));
                return;
            }

            showAlert(Alert.AlertType.WARNING, "Not Found", "No customer or account found for: " + query);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", e.getMessage());
        }
    }

    // ---------- MAIN CUSTOMER OVERVIEW ----------
    private VBox buildCustomerOverview(Customer customer) {
        VBox overview = new VBox(20);
        overview.setPadding(new Insets(25, 30, 30, 30));

        HBox headerRow = new HBox(15);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        String fullName = (customer instanceof IndividualCustomer ind)
                ? ind.getFirstName() + " " + ind.getSurname()
                : ((OrganisationCustomer) customer).getRegisterdName();

        Label nameLabel = new Label(fullName);
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subLabel = new Label("ID: " + customer.getCustomerID() + "  •  " + customer.getAddress());
        subLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        VBox nameBox = new VBox(4, nameLabel, subLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDeposit = new Button("+ Deposit");
        btnDeposit.getStyleClass().add("primary-btn");
        btnDeposit.setOnAction(e -> handleTransactionAction(customer, true));

        Button btnWithdraw = new Button("- Withdraw");
        btnWithdraw.getStyleClass().add("danger-btn");
        btnWithdraw.setOnAction(e -> handleTransactionAction(customer, false));

        headerRow.getChildren().addAll(nameBox, spacer, btnDeposit, btnWithdraw);

        Label cardsHeading = new Label("Accounts (Click a card to view its transactions)");
        cardsHeading.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        HBox cardsRow = new HBox(16);
        cardsRow.setPadding(new Insets(4, 0, 8, 0));
        ScrollPane cardScroll = new ScrollPane(cardsRow);
        cardScroll.setFitToHeight(true);
        cardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        Label txHeading = new Label("Transaction History");
        txHeading.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        TableView txTable = new TableView<>();
        txTable.setPrefHeight(280);
        TableColumn<Transaction, String> colDate = new TableColumn<>("Date & Time");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTimestamp().toString()));
        colDate.setPrefWidth(210);
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colType.setPrefWidth(140);
        TableColumn<Transaction, String> colAmount = new TableColumn<>("Amount (BWP)");
        colAmount.setCellValueFactory(c -> new SimpleStringProperty(String.format("BWP %.2f", c.getValue().getAmount())));
        colAmount.setPrefWidth(160);
        TableColumn<Transaction, String> colId = new TableColumn<>("Transaction ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransactionID()));
        colId.setPrefWidth(300);
        txTable.getColumns().addAll(colDate, colType, colAmount, colId);
        try {
            var accounts = accountController.getAccountsByCustomer(customer.getCustomerID());
            if (accounts.isEmpty()) {
                cardsRow.getChildren().add(new Label("This customer has no accounts yet."));
            } else {
                if (selectedAccountNumber == null) {
                    selectedAccountNumber = accounts.get(0).getAccountNumber();
                }
                for (Account acc : accounts) {
                    VBox card = createBankCard(acc);
                    card.setOnMouseClicked(e -> {
                        selectedAccountNumber = acc.getAccountNumber();
                        loadTransactionsIntoTable(acc.getAccountNumber(), txTable);
                    });
                    cardsRow.getChildren().add(card);
                }
                loadTransactionsIntoTable(selectedAccountNumber, txTable);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", e.getMessage());
        }
        overview.getChildren().addAll(headerRow, cardsHeading, cardScroll, txHeading, txTable);
        return overview;
    }
    private VBox createBankCard(Account acc) {
        VBox card = new VBox(12);
        card.setPrefSize(270, 155);
        card.getStyleClass().add("bank-card");
        String type = accountController.getAccountType(acc);
        if (type.equals("Savings")) card.getStyleClass().add("card-savings");
        else if (type.equals("Investment")) card.getStyleClass().add("card-investment");
        else card.getStyleClass().add("card-cheque");
        Label typeBadge = new Label(type.toUpperCase());
        typeBadge.getStyleClass().add("card-type");
        Label balanceLabel = new Label(String.format("BWP %,.2f", acc.getBalance()));
        balanceLabel.getStyleClass().add("card-balance");
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label footerLabel = new Label("Acc #: " + acc.getAccountNumber() + "  •  " + acc.getBranch());
        footerLabel.getStyleClass().add("card-meta");
        card.getChildren().addAll(typeBadge, balanceLabel, spacer, footerLabel);
        return card;
    }
    private void loadTransactionsIntoTable(String accountNumber, TableView table) {
        try {
            var transactions = transactionController.getTransactionsByAccount(accountNumber);
            table.setItems(FXCollections.observableArrayList(transactions));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Transaction Load Error", e.getMessage());
        }
    }
    private void handleTransactionAction(Customer customer, boolean isDeposit) {
        if (selectedAccountNumber == null) {
            showAlert(Alert.AlertType.WARNING, "No Account", "This customer has no account to transact on.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(isDeposit ? "Deposit Funds" : "Withdraw Funds");
        dialog.setHeaderText((isDeposit ? "Deposit to " : "Withdraw from ") + "Account: " + selectedAccountNumber);
        dialog.setContentText("Enter Amount (BWP):");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr.trim());
                if (isDeposit) {
                    accountController.deposit(selectedAccountNumber, amount);
                } else {
                    accountController.withdraw(selectedAccountNumber, amount);
                }
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        (isDeposit ? "Deposit" : "Withdrawal") + " completed!");
                root.setCenter(buildCustomerOverview(customer));
            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric amount.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Transaction Failed", ex.getMessage());
            }
        });
    }
    // ---------- CUSTOMER FORMS ----------
    private VBox buildCustomerForm(String mode) {
        boolean isUpdate = mode.equals("UPDATE");
        VBox container = new VBox(16);
        container.setPadding(new Insets(30));
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(520);
        Label title = new Label(isUpdate ? "Update Customer" : "Add New Customer");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rbIndividual = new RadioButton("Individual");
        RadioButton rbOrganisation = new RadioButton("Organisation");
        rbIndividual.setToggleGroup(typeGroup);
        rbOrganisation.setToggleGroup(typeGroup);
        rbIndividual.setSelected(true);
        HBox radioRow = new HBox(20, rbIndividual, rbOrganisation);
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        TextField idField = new TextField();
        TextField addressField = new TextField();
        Label lblField1 = new Label("First Name:");
        TextField field1 = new TextField();
        Label lblField2 = new Label("Surname:");
        TextField field2 = new TextField();
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (rbIndividual.isSelected()) {
                lblField1.setText("First Name:");
                lblField2.setText("Surname:");
            } else {
                lblField1.setText("Registered Name:");
                lblField2.setText("Registration Number:");
            }
        });
        grid.addRow(0, new Label("Customer ID:"), idField);
        grid.addRow(1, new Label("Address:"), addressField);
        grid.addRow(2, lblField1, field1);
        grid.addRow(3, lblField2, field2);
        Button submitBtn = new Button(isUpdate ? "Update Customer" : "Save Customer");
        submitBtn.getStyleClass().add("primary-btn");
        submitBtn.setOnAction(e -> {
            try {
                String id = idField.getText().trim();
                String addr = addressField.getText().trim();
                String val1 = field1.getText().trim();
                String val2 = field2.getText().trim();
                if (rbIndividual.isSelected()) {
                    IndividualCustomer ind = new IndividualCustomer(id, addr, val1, val2);
                    if (isUpdate) customerController.updateIndividualCustomer(ind);
                    else customerController.addIndividualCustomer(ind);
                } else {
                    OrganisationCustomer org = new OrganisationCustomer(id, addr, val1, val2);
                    if (isUpdate) customerController.updateOrganisationCustomer(org);
                    else customerController.addOrganisationCustomer(org);
                }
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        isUpdate ? "Customer updated successfully!" : "Customer added successfully!");
                idField.clear();
                addressField.clear();
                field1.clear();
                field2.clear();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        card.getChildren().addAll(title, radioRow, grid, submitBtn);
        container.getChildren().add(card);
        return container;
    }
    private VBox buildDeleteCustomerForm() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(30));
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(450);
        Label title = new Label("Delete Customer");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField idField = new TextField();
        idField.setPromptText("Enter Customer ID to delete");
        Button deleteBtn = new Button("Delete Customer");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setOnAction(e -> {
            try {
                customerController.deleteCustomer(idField.getText().trim());
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Customer deleted successfully.");
                idField.clear();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        card.getChildren().addAll(title, new Label("Customer ID:"), idField, deleteBtn);
        container.getChildren().add(card);
        return container;
    }
    // ---------- ACCOUNT FORMS ----------
    private VBox buildAddAccountForm() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(30));
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(520);
        Label title = new Label("Open New Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rbSavings = new RadioButton("Savings");
        RadioButton rbInvestment = new RadioButton("Investment");
        RadioButton rbCheque = new RadioButton("Cheque");
        rbSavings.setToggleGroup(typeGroup);
        rbInvestment.setToggleGroup(typeGroup);
        rbCheque.setToggleGroup(typeGroup);
        rbSavings.setSelected(true);
        HBox radioRow = new HBox(20, rbSavings, rbInvestment, rbCheque);
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        TextField custIdField = new TextField();
        TextField accNumField = new TextField();
        TextField branchField = new TextField();
        TextField depositField = new TextField();
        TextField empNameField = new TextField();
        TextField empAddrField = new TextField();
        empNameField.setDisable(true);
        empAddrField.setDisable(true);
        typeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCheque = rbCheque.isSelected();
            empNameField.setDisable(!isCheque);
            empAddrField.setDisable(!isCheque);
        });
        grid.addRow(0, new Label("Customer ID:"), custIdField);
        grid.addRow(1, new Label("Account Number:"), accNumField);
        grid.addRow(2, new Label("Branch:"), branchField);
        grid.addRow(3, new Label("Opening Deposit (BWP):"), depositField);
        grid.addRow(4, new Label("Employer Name (Cheque):"), empNameField);
        grid.addRow(5, new Label("Employer Address (Cheque):"), empAddrField);
        Button submitBtn = new Button("Open Account");
        submitBtn.getStyleClass().add("primary-btn");
        submitBtn.setOnAction(e -> {
            try {
                Customer customer = customerController.getCustomerByID(custIdField.getText().trim());
                if (customer == null) {
                    throw new IllegalArgumentException("Customer not found. Add the customer first.");
                }
                String accNum = accNumField.getText().trim();
                String branch = branchField.getText().trim();
                double balance = Double.parseDouble(depositField.getText().trim());
                if (rbSavings.isSelected()) {
                    accountController.addSavingsAccount(new SavingsAccount(accNum, balance, branch, customer));
                } else if (rbInvestment.isSelected()) {
                    accountController.addInvestmentAccount(new InvestmentAccount(accNum, balance, branch, customer));
                } else {
                    accountController.addChequeAccount(new ChequeAccount(
                            accNum, balance, branch, customer,
                            empAddrField.getText().trim(), empNameField.getText().trim()));
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account opened successfully!");
                accNumField.clear();
                depositField.clear();
                empNameField.clear();
                empAddrField.clear();
            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Opening deposit must be a valid number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        card.getChildren().addAll(title, radioRow, grid, submitBtn);
        container.getChildren().add(card);
        return container;
    }
    private VBox buildUpdateAccountForm() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(30));
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(520);
        Label title = new Label("Update Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        TextField accNumField = new TextField();
        TextField branchField = new TextField();
        TextField balanceField = new TextField();
        grid.addRow(0, new Label("Account Number:"), accNumField);
        grid.addRow(1, new Label("New Branch:"), branchField);
        grid.addRow(2, new Label("New Balance (BWP):"), balanceField);
        Button updateBtn = new Button("Update Account");
        updateBtn.getStyleClass().add("primary-btn");
        updateBtn.setOnAction(e -> {
            try {
                Account acc = accountController.getAccountByNumber(accNumField.getText().trim());
                if (acc == null) throw new IllegalArgumentException("Account not found.");
                if (!branchField.getText().isBlank()) acc.setBranch(branchField.getText().trim());
                if (!balanceField.getText().isBlank()) acc.setBalance(Double.parseDouble(balanceField.getText().trim()));
                accountController.updateAccount(acc);
                showAlert(Alert.AlertType.INFORMATION, "Updated", "Account updated successfully!");
                accNumField.clear();
                branchField.clear();
                balanceField.clear();
            } catch (NumberFormatException nfe) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Balance must be a valid number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        card.getChildren().addAll(title, grid, updateBtn);
        container.getChildren().add(card);
        return container;
    }
    private VBox buildDeleteAccountForm() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(30));
        VBox card = new VBox(16);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(450);
        Label title = new Label("Delete Account");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField accNumField = new TextField();
        accNumField.setPromptText("Enter Account Number to delete");
        Button deleteBtn = new Button("Delete Account");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setOnAction(e -> {
            try {
                accountController.deleteAccount(accNumField.getText().trim());
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Account deleted successfully.");
                accNumField.clear();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        card.getChildren().addAll(title, new Label("Account Number:"), accNumField, deleteBtn);
        container.getChildren().add(card);
        return container;
    }
    public static void main(String[] args) {
        launch(args);
    }
}