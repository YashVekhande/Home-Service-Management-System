package urban;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class AdminPage extends Application {

    private VBox contentArea;

    @Override
    public void start(Stage primaryStage) {
        // Create tabs
        Pane usersTab = createTabPane("USERS");
        usersTab.setOnMouseClicked(e -> displayUsersTab());

        Pane servicesTab = createTabPane("SERVICES");
        servicesTab.setOnMouseClicked(e -> displayServicesTab());

        Pane subServicesTab = createTabPane("SUB_SERVICES");
        subServicesTab.setOnMouseClicked(e -> displaySubServicesTab());

        Pane transactionsTab = createTabPane("TRANSACTIONS");
        transactionsTab.setOnMouseClicked(e -> displayTransactionsTab());
        
        Pane productsTab = createTabPane("PRODUCTS");
        productsTab.setOnMouseClicked(e -> displayProductsTab());
        
        Pane backToLogin = createTabPane("BACK TO LOGIN");
        backToLogin.setOnMouseClicked(e -> loginRedirect(primaryStage));
        
        VBox navigationBox = new VBox(usersTab, servicesTab, subServicesTab, transactionsTab, productsTab, backToLogin);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setId("navigationBox");
        navigationBox.setPrefWidth(300);

        VBox.setVgrow(usersTab, Priority.ALWAYS);
        VBox.setVgrow(servicesTab, Priority.ALWAYS);
        VBox.setVgrow(subServicesTab, Priority.ALWAYS);
        VBox.setVgrow(transactionsTab, Priority.ALWAYS);
        VBox.setVgrow(productsTab, Priority.ALWAYS);
        VBox.setVgrow(backToLogin, Priority.ALWAYS);

        contentArea = new VBox(15);
        contentArea.setId("contentArea");
        contentArea.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setId("scrollPane");

        BorderPane root = new BorderPane();
        root.setLeft(navigationBox);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Admin Dashboard - Home Service Management");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Pane createTabPane(String text) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("tab-pane");

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 22;");
        pane.getChildren().add(label);

        pane.setOnMouseEntered(e -> pane.setStyle("-fx-background-color: #1ABC9C; -fx-border-color: #16A085;"));
        pane.setOnMouseExited(e -> pane.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #1ABC9C;"));
        return pane;
    }
private void loginRedirect(Stage primaryStage){
    Login login = new Login();
    login.start(primaryStage);
}
private void displayUsersTab() {
    contentArea.getChildren().clear();

    TableView<Map<String, Object>> table = new TableView<>();
    VBox.setVgrow(table, Priority.ALWAYS); // Allow table to grow in VBox
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    List<String> columnNames = Arrays.asList("userid", "username", "password", "fname", "lname", "email", "phone");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
            col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (String colName : columnNames) {
                row.put(colName, rs.getObject(colName));
            }
            table.getItems().add(row);
        }

    } catch (SQLException e) {
        showAlert("Database Error", "Error loading users: " + e.getMessage());
        return;
    }

    // Input Form
    Label formLabel = new Label("Add / Update / Delete User");
    formLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);
    form.setPadding(new Insets(10));

    List<String> inputFields = Arrays.asList("username", "password", "fname", "lname", "email", "phone");
    Map<String, TextField> textFieldMap = new HashMap<>();

    int rowIndex = 0;
    for (String col : inputFields) {
        Label lbl = new Label(col + ":");
        TextField txt = new TextField();
        if (col.equals("password")) txt.setPromptText("8 chars, 1 special char");
        form.add(lbl, 0, rowIndex);
        form.add(txt, 1, rowIndex);
        textFieldMap.put(col, txt);
        rowIndex++;
    }

table.setPrefHeight(500); // Ensures ~5 rows are visible

table.setId("admin-table");


Button insertBtn = new Button("Insert");
insertBtn.setId("confirm-btn");

Button updateBtn = new Button("Update");
updateBtn.setId("confirm-btn");

Button deleteBtn = new Button("Delete Selected");
deleteBtn.setId("remove-btn");

    insertBtn.setOnAction(e -> {
        if (!validateInputs(textFieldMap)) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "INSERT INTO users (username, password, fname, lname, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, textFieldMap.get("username").getText().trim());
            ps.setString(2, textFieldMap.get("password").getText().trim());
            ps.setString(3, textFieldMap.get("fname").getText().trim());
            ps.setString(4, textFieldMap.get("lname").getText().trim());
            ps.setString(5, textFieldMap.get("email").getText().trim());
            ps.setString(6, textFieldMap.get("phone").getText().trim());
            ps.executeUpdate();
            showAlert("Success", "User added successfully!");
            displayUsersTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not insert user:\n" + ex.getMessage());
        }
    });

    updateBtn.setOnAction(e -> {
        if (!validateInputs(textFieldMap)) return;

        String username = textFieldMap.get("username").getText().trim();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            // Check if username exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                showAlert("Update Failed", "User does not exist.");
                return;
            }

            String updateQuery = "UPDATE users SET password=?, fname=?, lname=?, email=?, phone=? WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, textFieldMap.get("password").getText().trim());
            ps.setString(2, textFieldMap.get("fname").getText().trim());
            ps.setString(3, textFieldMap.get("lname").getText().trim());
            ps.setString(4, textFieldMap.get("email").getText().trim());
            ps.setString(5, textFieldMap.get("phone").getText().trim());
            ps.setString(6, username);
            ps.executeUpdate();
            showAlert("Success", "User updated successfully!");
            displayUsersTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not update user:\n" + ex.getMessage());
        }
    });

    deleteBtn.setOnAction(e -> {
        String username = textFieldMap.get("username").getText().trim();
        if (username.isEmpty()) {
            showAlert("Validation Error", "Username must be provided to delete a user.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            // Check if user exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                showAlert("Delete Failed", "User does not exist.");
                return;
            }

            String deleteQuery = "DELETE FROM users WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setString(1, username);
            ps.executeUpdate();
            showAlert("Success", "User deleted successfully!");
            displayUsersTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not delete user:\n" + ex.getMessage());
        }
    });

    // Populate fields on table row click
    table.setOnMouseClicked(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String field : inputFields) {
                Object value = selected.get(field);
                if (value != null) {
                    textFieldMap.get(field).setText(value.toString());
                }
            }
        }
    });

    HBox btnBox = new HBox(15, insertBtn, updateBtn, deleteBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(10));

    contentArea.getChildren().addAll(new Label("Table: USERS"), table, formLabel, form, btnBox);
}
private void displayServicesTab() {
    contentArea.getChildren().clear();

    TableView<Map<String, Object>> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPrefHeight(500);
    table.setId("admin-table");

    List<String> columnNames = Arrays.asList("service_id", "service_name", "description");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM services")) {

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
            col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (String colName : columnNames) {
                row.put(colName, rs.getObject(colName));
            }
            table.getItems().add(row);
        }

    } catch (SQLException e) {
        showAlert("Database Error", "Error loading services: " + e.getMessage());
        return;
    }

    // Input Form
    Label formLabel = new Label("Add / Update / Delete Service");
    formLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);
    form.setPadding(new Insets(10));

    List<String> inputFields = Arrays.asList("service_name", "description");
    Map<String, TextField> textFieldMap = new HashMap<>();

    int rowIndex = 0;
    for (String col : inputFields) {
        Label lbl = new Label(col + ":");
        TextField txt = new TextField();
        form.add(lbl, 0, rowIndex);
        form.add(txt, 1, rowIndex);
        textFieldMap.put(col, txt);
        rowIndex++;
    }

    Button insertBtn = new Button("Insert");
    insertBtn.setId("confirm-btn");

    Button updateBtn = new Button("Update");
    updateBtn.setId("confirm-btn");

    Button deleteBtn = new Button("Delete Selected");
    deleteBtn.setId("remove-btn");

    insertBtn.setOnAction(e -> {
        String name = textFieldMap.get("service_name").getText().trim();
        String desc = textFieldMap.get("description").getText().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "INSERT INTO services (service_name, description) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.executeUpdate();
            showAlert("Success", "Service added successfully!");
            displayServicesTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not insert service:\n" + ex.getMessage());
        }
    });

    updateBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Update Error", "No row selected for update.");
            return;
        }

        int serviceId = (int) selected.get("service_id");
        String name = textFieldMap.get("service_name").getText().trim();
        String desc = textFieldMap.get("description").getText().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled!");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String updateQuery = "UPDATE services SET service_name=?, description=? WHERE service_id=?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, serviceId);
            ps.executeUpdate();
            showAlert("Success", "Service updated successfully!");
            displayServicesTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not update service:\n" + ex.getMessage());
        }
    });

    deleteBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Delete Error", "No row selected to delete.");
            return;
        }

        int serviceId = (int) selected.get("service_id");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String deleteQuery = "DELETE FROM services WHERE service_id=?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, serviceId);
            ps.executeUpdate();
            showAlert("Success", "Service deleted successfully!");
            displayServicesTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not delete service:\n" + ex.getMessage());
        }
    });

    // Populate form on row click
    table.setOnMouseClicked(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String field : inputFields) {
                Object value = selected.get(field);
                if (value != null) {
                    textFieldMap.get(field).setText(value.toString());
                }
            }
        }
    });

    HBox btnBox = new HBox(15, insertBtn, updateBtn, deleteBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(10));

    contentArea.getChildren().addAll(new Label("Table: SERVICES"), table, formLabel, form, btnBox);
}
private void displayProductsTab() {
    contentArea.getChildren().clear();

    TableView<Map<String, Object>> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPrefHeight(500);
    table.setId("admin-table");

    List<String> columnNames = Arrays.asList("id", "product_name", "description", "price");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
            col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (String colName : columnNames) {
                row.put(colName, rs.getObject(colName));
            }
            table.getItems().add(row);
        }

    } catch (SQLException e) {
        showAlert("Database Error", "Error loading products: " + e.getMessage());
        return;
    }

    // Form
    Label formLabel = new Label("Add / Update / Delete Product");
    formLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);
    form.setPadding(new Insets(10));

    List<String> inputFields = Arrays.asList("product_name", "description", "price");
    Map<String, TextField> textFieldMap = new HashMap<>();

    int rowIndex = 0;
    for (String col : inputFields) {
        Label lbl = new Label(col + ":");
        TextField txt = new TextField();
        form.add(lbl, 0, rowIndex);
        form.add(txt, 1, rowIndex);
        textFieldMap.put(col, txt);
        rowIndex++;
    }

    Button insertBtn = new Button("Insert");
    insertBtn.setId("confirm-btn");

    Button updateBtn = new Button("Update");
    updateBtn.setId("confirm-btn");

    Button deleteBtn = new Button("Delete Selected");
    deleteBtn.setId("remove-btn");

    insertBtn.setOnAction(e -> {
        String name = textFieldMap.get("product_name").getText().trim();
        String desc = textFieldMap.get("description").getText().trim();
        String priceStr = textFieldMap.get("price").getText().trim();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
                String query = "INSERT INTO products (product_name, description, price) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, desc);
                ps.setDouble(3, price);
                ps.executeUpdate();
                showAlert("Success", "Product added successfully!");
                displayProductsTab();
            }
        } catch (NumberFormatException nfe) {
            showAlert("Validation Error", "Price must be a valid number.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not insert product:\n" + ex.getMessage());
        }
    });

    updateBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Update Error", "No row selected for update.");
            return;
        }

        int id = (int) selected.get("id");
        String name = textFieldMap.get("product_name").getText().trim();
        String desc = textFieldMap.get("description").getText().trim();
        String priceStr = textFieldMap.get("price").getText().trim();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
                String query = "UPDATE products SET product_name=?, description=?, price=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, desc);
                ps.setDouble(3, price);
                ps.setInt(4, id);
                ps.executeUpdate();
                showAlert("Success", "Product updated successfully!");
                displayProductsTab();
            }
        } catch (NumberFormatException nfe) {
            showAlert("Validation Error", "Price must be a valid number.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not update product:\n" + ex.getMessage());
        }
    });

    deleteBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Delete Error", "No row selected to delete.");
            return;
        }

        int id = (int) selected.get("id");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "DELETE FROM products WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            showAlert("Success", "Product deleted successfully!");
            displayProductsTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not delete product:\n" + ex.getMessage());
        }
    });

    table.setOnMouseClicked(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String field : inputFields) {
                Object value = selected.get(field);
                if (value != null) {
                    textFieldMap.get(field).setText(value.toString());
                }
            }
        }
    });

    HBox btnBox = new HBox(15, insertBtn, updateBtn, deleteBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(10));

    contentArea.getChildren().addAll(new Label("Table: PRODUCTS"), table, formLabel, form, btnBox);
}
private void displayTransactionsTab() {
    contentArea.getChildren().clear();

    TableView<Map<String, Object>> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPrefHeight(500);
    table.setId("admin-table");

    List<String> columnNames = Arrays.asList("id", "user_name", "buyed_items", "total_price");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM transactions")) {

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
            col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (String colName : columnNames) {
                row.put(colName, rs.getObject(colName));
            }
            table.getItems().add(row);
        }

    } catch (SQLException e) {
        showAlert("Database Error", "Error loading transactions: " + e.getMessage());
        return;
    }

    // Form for editing
    Label formLabel = new Label("Update / Delete Transaction");
    formLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);
    form.setPadding(new Insets(10));

    List<String> editableFields = Arrays.asList("buyed_items", "total_price");
    Map<String, TextField> textFieldMap = new HashMap<>();

    int rowIndex = 0;
    for (String col : editableFields) {
        Label lbl = new Label(col + ":");
        TextField txt = new TextField();
        form.add(lbl, 0, rowIndex);
        form.add(txt, 1, rowIndex);
        textFieldMap.put(col, txt);
        rowIndex++;
    }

    Button updateBtn = new Button("Update");
    updateBtn.setId("confirm-btn");

    Button deleteBtn = new Button("Delete Selected");
    deleteBtn.setId("remove-btn");

    updateBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Update Error", "No transaction selected for update.");
            return;
        }

        int id = (int) selected.get("id");
        String items = textFieldMap.get("buyed_items").getText().trim();
        String priceStr = textFieldMap.get("total_price").getText().trim();

        if (items.isEmpty() || priceStr.isEmpty()) {
            showAlert("Validation Error", "Both fields must be filled!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
                String query = "UPDATE transactions SET buyed_items=?, total_price=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, items);
                ps.setDouble(2, price);
                ps.setInt(3, id);
                ps.executeUpdate();
                showAlert("Success", "Transaction updated successfully!");
                displayTransactionsTab();
            }
        } catch (NumberFormatException nfe) {
            showAlert("Validation Error", "Total price must be a valid number.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not update transaction:\n" + ex.getMessage());
        }
    });

    deleteBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Delete Error", "No transaction selected to delete.");
            return;
        }

        int id = (int) selected.get("id");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "DELETE FROM transactions WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            showAlert("Success", "Transaction deleted successfully!");
            displayTransactionsTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not delete transaction:\n" + ex.getMessage());
        }
    });

    table.setOnMouseClicked(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String field : editableFields) {
                Object value = selected.get(field);
                if (value != null) {
                    textFieldMap.get(field).setText(value.toString());
                }
            }
        }
    });

    HBox btnBox = new HBox(15, updateBtn, deleteBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(10));

    contentArea.getChildren().addAll(new Label("Table: TRANSACTIONS"), table, formLabel, form, btnBox);
}

private void displaySubServicesTab() {
    contentArea.getChildren().clear();

    TableView<Map<String, Object>> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPrefHeight(500);
    table.setId("admin-table");

    List<String> columnNames = Arrays.asList("id", "service_name", "subservice_name", "description", "price");

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM sub_services")) {

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
            col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
            table.getColumns().add(col);
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (String colName : columnNames) {
                row.put(colName, rs.getObject(colName));
            }
            table.getItems().add(row);
        }

    } catch (SQLException e) {
        showAlert("Database Error", "Error loading sub-services: " + e.getMessage());
        return;
    }

    // Input Form
    Label formLabel = new Label("Add / Update / Delete Sub-Service");
    formLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

    GridPane form = new GridPane();
    form.setHgap(10);
    form.setVgap(10);
    form.setPadding(new Insets(10));

    List<String> inputFields = Arrays.asList("service_name", "subservice_name", "description", "price");
    Map<String, TextField> textFieldMap = new HashMap<>();

    int rowIndex = 0;
    for (String col : inputFields) {
        Label lbl = new Label(col + ":");
        TextField txt = new TextField();
        form.add(lbl, 0, rowIndex);
        form.add(txt, 1, rowIndex);
        textFieldMap.put(col, txt);
        rowIndex++;
    }

    Button insertBtn = new Button("Insert");
    insertBtn.setId("confirm-btn");

    Button updateBtn = new Button("Update");
    updateBtn.setId("confirm-btn");

    Button deleteBtn = new Button("Delete Selected");
    deleteBtn.setId("remove-btn");

    insertBtn.setOnAction(e -> {
        try {
            String serviceName = textFieldMap.get("service_name").getText().trim();
            String subName = textFieldMap.get("subservice_name").getText().trim();
            String desc = textFieldMap.get("description").getText().trim();
            double price = Double.parseDouble(textFieldMap.get("price").getText().trim());

            if (serviceName.isEmpty() || subName.isEmpty() || desc.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled!");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
                String query = "INSERT INTO sub_services (service_name, subservice_name, description, price) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, serviceName);
                ps.setString(2, subName);
                ps.setString(3, desc);
                ps.setDouble(4, price);
                ps.executeUpdate();
                showAlert("Success", "Sub-service added successfully!");
                displaySubServicesTab();
            }

        } catch (NumberFormatException ex) {
            showAlert("Validation Error", "Price must be a valid number.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not insert sub-service:\n" + ex.getMessage());
        }
    });

    updateBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Update Error", "No row selected for update.");
            return;
        }

        try {
            int id = (int) selected.get("id");
            String serviceName = textFieldMap.get("service_name").getText().trim();
            String subName = textFieldMap.get("subservice_name").getText().trim();
            String desc = textFieldMap.get("description").getText().trim();
            double price = Double.parseDouble(textFieldMap.get("price").getText().trim());

            if (serviceName.isEmpty() || subName.isEmpty() || desc.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled!");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
                String updateQuery = "UPDATE sub_services SET service_name=?, subservice_name=?, description=?, price=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(updateQuery);
                ps.setString(1, serviceName);
                ps.setString(2, subName);
                ps.setString(3, desc);
                ps.setDouble(4, price);
                ps.setInt(5, id);
                ps.executeUpdate();
                showAlert("Success", "Sub-service updated successfully!");
                displaySubServicesTab();
            }

        } catch (NumberFormatException ex) {
            showAlert("Validation Error", "Price must be a valid number.");
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not update sub-service:\n" + ex.getMessage());
        }
    });

    deleteBtn.setOnAction(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Delete Error", "No row selected to delete.");
            return;
        }

        int id = (int) selected.get("id");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String deleteQuery = "DELETE FROM sub_services WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, id);
            ps.executeUpdate();
            showAlert("Success", "Sub-service deleted successfully!");
            displaySubServicesTab();
        } catch (SQLException ex) {
            showAlert("Database Error", "Could not delete sub-service:\n" + ex.getMessage());
        }
    });

    table.setOnMouseClicked(e -> {
        Map<String, Object> selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (String field : inputFields) {
                Object value = selected.get(field);
                if (value != null) {
                    textFieldMap.get(field).setText(value.toString());
                }
            }
        }
    });

    HBox btnBox = new HBox(15, insertBtn, updateBtn, deleteBtn);
    btnBox.setAlignment(Pos.CENTER);
    btnBox.setPadding(new Insets(10));

    contentArea.getChildren().addAll(new Label("Table: SUB_SERVICES"), table, formLabel, form, btnBox);
}



private boolean validateInputs(Map<String, TextField> inputs) {
    String username = inputs.get("username").getText().trim();
    String password = inputs.get("password").getText().trim();
    String email = inputs.get("email").getText().trim();
    String phone = inputs.get("phone").getText().trim();

    if (username.isEmpty() || password.isEmpty() || inputs.get("fname").getText().trim().isEmpty()
            || inputs.get("lname").getText().trim().isEmpty() || email.isEmpty() || phone.isEmpty()) {
        showAlert("Validation Error", "All fields are required!");
        return false;
    }

    if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
        showAlert("Validation Error", "Invalid email format!");
        return false;
    }

    if (!phone.matches("\\d{10}")) {
        showAlert("Validation Error", "Phone number must be 10 digits!");
        return false;
    }

    if (password.length() < 8 || !password.matches(".*[^a-zA-Z0-9].*")) {
        showAlert("Validation Error", "Password must be atleast 8 characters and contain at least one special character!");
        return false;
    }

    return true;
}

    private void loadTableData(String tableName) {
        contentArea.getChildren().clear();
        TableView<Map<String, Object>> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                String columnName = meta.getColumnName(colIndex);
                TableColumn<Map<String, Object>, Object> col = new TableColumn<>(columnName);
                col.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().get(columnName)));
                table.getColumns().add(col);
            }

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int colIndex = 1; colIndex <= columnCount; colIndex++) {
                    row.put(meta.getColumnName(colIndex), rs.getObject(colIndex));
                }
                table.getItems().add(row);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Could not load data from table: " + tableName + "\n\n" + e.getMessage());
        }

        Label header = new Label("Table: " + tableName.toUpperCase());
        header.setStyle("-fx-font-size: 28px; -fx-text-fill: #333333;");
        contentArea.getChildren().addAll(header, table);
    }
    

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
