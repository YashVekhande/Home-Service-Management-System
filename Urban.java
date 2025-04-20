package urban;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Urban extends Application {

    private VBox contentArea;
    private List<String[]> cartItems = new ArrayList<>(); // Stores selected sub-services

    @Override
    public void start(Stage primaryStage) {
        // Creating Buttons for Navigation
        Pane servicesPane = createTabPane("SERVICES");
        Pane productsPane = createTabPane("PRODUCTS");
        Pane accountPane = createTabPane("ACCOUNT");
        Pane cartPane = createTabPane("YOUR CART");
        Pane backToLogin = createTabPane("BACK TO LOGIN");
        backToLogin.setOnMouseClicked(e -> loginRedirect(primaryStage));

        servicesPane.setOnMouseClicked(e -> displayServicesContent());
        productsPane.setOnMouseClicked(e -> displayProductsContent());
        accountPane.setOnMouseClicked(e -> displayAccountDetails());
        cartPane.setOnMouseClicked(e -> displayCartContent()); // Updated to show cart content

        // Side Navigation Panel
        VBox navigationBox = new VBox(servicesPane, productsPane, accountPane, cartPane, backToLogin);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setId("navigationBox");
        navigationBox.setPrefWidth(300);

        VBox.setVgrow(servicesPane, Priority.ALWAYS);
        VBox.setVgrow(productsPane, Priority.ALWAYS);
        VBox.setVgrow(accountPane, Priority.ALWAYS);
        VBox.setVgrow(cartPane, Priority.ALWAYS);
        VBox.setVgrow(backToLogin, Priority.ALWAYS);

        // Right Content Area (Initially Showing Logo)
   contentArea = new VBox(15);
contentArea.setId("contentArea");
contentArea.setAlignment(Pos.TOP_LEFT);
contentArea.setPadding(new Insets(20));

// Load and configure the image
Image logoImage = new Image(getClass().getResourceAsStream("logo.png")); // Replace with your image path
ImageView imageView = new ImageView(logoImage);
imageView.setFitWidth(600);  // Set the width of the image
imageView.setPreserveRatio(true);  // Maintain aspect ratio

// Image container
StackPane imageContainer = new StackPane(imageView);
imageContainer.setAlignment(Pos.CENTER);
contentArea.getChildren().add(imageContainer);

// Descriptive paragraph below the image
Label description = new Label(
    "In today’s fast-paced world, managing household chores and maintenance tasks can be a real challenge. " +
    "Whether it's plumbing issues, electrical repairs, deep cleaning, or appliance servicing, finding reliable help " +
    "is often time-consuming and stressful. Home services play a vital role in simplifying our daily lives by providing " +
    "professional assistance right at our doorstep, saving us valuable time and effort.\n\n" +
    
    "Urban Home Service Management System is designed to make your life easier. With this platform, you can browse and book " +
    "a wide variety of services with just a few clicks. Whether you're looking for regular maintenance, emergency repairs, " +
    "or premium household products, Urban is your one-stop solution. You can also manage your profile, track your selected " +
    "services, and review your transactions — all in one convenient interface.\n\n" +
    
    "Start exploring by clicking on the tabs on the left. Welcome to a smarter way of managing your home!"
);
description.setWrapText(true);
description.setStyle("-fx-font-size: 16px; -fx-padding: 10 0 0 0;");

// Add description to content area
contentArea.getChildren().add(description);




        // ScrollPane for Content
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setId("scrollPane");

        // Layout Setup
        BorderPane root = new BorderPane();
        root.setLeft(navigationBox);
        root.setCenter(scrollPane);

        // Scene Configuration
        Scene scene = new Scene(root, 1000, 500);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Home Service Management System");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
private void loginRedirect(Stage primaryStage){
    Login login = new Login();
    login.start(primaryStage);
}
    private Pane createTabPane(String text) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("tab-pane");

        Label label = new Label(text);
        label.setFont(new Font(22));
        label.setStyle("-fx-text-fill: white;");
        pane.getChildren().add(label);

        pane.setOnMouseEntered(e -> pane.setStyle("-fx-background-color: #1ABC9C; -fx-border-color: #16A085;"));
        pane.setOnMouseExited(e -> pane.setStyle("-fx-background-color: #1E1E1E; -fx-border-color: #1ABC9C;"));
        pane.setOnMouseClicked(e -> pane.setStyle("-fx-background-color: #1ABC9C; -fx-border-color: #16A085;"));

        return pane;
    }
private void displayProductsContent() {
    contentArea.getChildren().clear();
    List<String[]> products = getProductsFromDB();

    for (String[] product : products) {
        String title = product[0];
        String description = product[1];
        String price = product[2];
        contentArea.getChildren().add(createProductPanel(title, description, price));
    }
}
private List<String[]> getProductsFromDB() {
    List<String[]> products = new ArrayList<>();
    String query = "SELECT product_name, description, price FROM products";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            String name = rs.getString("product_name");
            String desc = rs.getString("description");
            String price = rs.getString("price");
            products.add(new String[]{name, desc, price});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return products;
}
private HBox createProductPanel(String title, String description, String price) {
    HBox productBox = new HBox(20);
    productBox.getStyleClass().add("service-panel");
    productBox.setPadding(new Insets(20));
    productBox.setAlignment(Pos.CENTER_LEFT);

    VBox textContainer = new VBox(5);
    textContainer.setAlignment(Pos.CENTER_LEFT);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("service-title");

    Label descriptionLabel = new Label(description);
    descriptionLabel.getStyleClass().add("service-description");

    Label priceLabel = new Label("$" + price);
    priceLabel.setFont(new Font(18));
    priceLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

    textContainer.getChildren().addAll(titleLabel, descriptionLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button addToCartButton = new Button("Add to Cart");
    addToCartButton.getStyleClass().add("book-now-btn");
    addToCartButton.setOnAction(e -> {
        String[] item = new String[]{title, description, price};
        if (!cartItems.contains(item)) {
            cartItems.add(item);
        }
    });

    productBox.getChildren().addAll(textContainer, spacer, priceLabel, addToCartButton);
    return productBox;
}

private void displayServicesContent() {
    contentArea.getChildren().clear();
    List<String[]> services = getServicesFromDB();

    for (String[] service : services) {
        String title = service[0];
        String description = service[1];
        contentArea.getChildren().add(createServicePanel(title, description));
    }
}
private List<String[]> getServicesFromDB() {
    List<String[]> services = new ArrayList<>();
    String query = "SELECT service_name, description FROM services";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            String name = rs.getString("service_name");
            String desc = rs.getString("description");
            services.add(new String[]{name, desc});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return services;
}

    private HBox createServicePanel(String title, String description) {
        HBox serviceBox = new HBox(20);
        serviceBox.getStyleClass().add("service-panel");
        serviceBox.setPadding(new Insets(20));
        serviceBox.setAlignment(Pos.CENTER_LEFT);

        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("service-title");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("service-description");

        textContainer.getChildren().addAll(titleLabel, descriptionLabel);

        Button bookNowButton = new Button("Book Now");
        bookNowButton.getStyleClass().add("book-now-btn");
        bookNowButton.setOnAction(e -> displaySubServiceOptions(title));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        serviceBox.getChildren().addAll(textContainer, spacer, bookNowButton);
        return serviceBox;
    }

    private void displaySubServiceOptions(String serviceTitle) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Choose a Sub-Service for " + serviceTitle);
        titleLabel.setFont(new Font(24));
        titleLabel.setStyle("-fx-text-fill: white;");

        VBox optionsBox = new VBox(15);
        optionsBox.setPadding(new Insets(20));
        optionsBox.setAlignment(Pos.TOP_LEFT);

        List<String[]> subServices = getSubServicesFromDB(serviceTitle);
        List<CheckBox> selectedCheckBoxes = new ArrayList<>();

        for (String[] subService : subServices) {
            String name = subService[0];
            String desc = subService[1];
            String price = subService[2];

            HBox serviceRow = new HBox(20);
            serviceRow.getStyleClass().add("sub-service-panel");
            serviceRow.setPadding(new Insets(15));
            serviceRow.setAlignment(Pos.CENTER_LEFT);

            CheckBox checkBox = new CheckBox();
            checkBox.setMinWidth(40);
            checkBox.setStyle("-fx-scale-x: 1.5; -fx-scale-y: 1.5;");
            selectedCheckBoxes.add(checkBox);

            VBox textContainer = new VBox(5);
            Label serviceTitleLabel = new Label(name);
            Label serviceDescLabel = new Label(desc);
            Label priceLabel = new Label("$" + price);
            priceLabel.setFont(new Font(18));
            priceLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

            textContainer.getChildren().addAll(serviceTitleLabel, serviceDescLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            serviceRow.getChildren().addAll(checkBox, textContainer, spacer, priceLabel);
            optionsBox.getChildren().add(serviceRow);
        }

        Button confirmButton = new Button("Confirm Selection");
        confirmButton.getStyleClass().add("confirm-btn");
        confirmButton.setOnAction(e -> {
            for (int i = 0; i < subServices.size(); i++) {
                if (selectedCheckBoxes.get(i).isSelected()) {
                    String[] item = subServices.get(i);
                    if (!cartItems.contains(item)) {
                        cartItems.add(item);
                    }
                }
            }
        });

        optionsBox.getChildren().add(confirmButton);
        contentArea.getChildren().addAll(titleLabel, optionsBox);
    }

private void displayCartContent() {
    contentArea.getChildren().clear();

    Label title = new Label("Your Cart");
    title.setFont(new Font(20));
    title.setStyle("-fx-text-fill: white;");
    contentArea.getChildren().add(title);

    VBox cartBox = new VBox(5);
    cartBox.setPadding(new Insets(10));

    double[] totalPriceWrapper = new double[]{0}; // Mutable container

    for (String[] item : cartItems) {
        HBox cartItemBox = new HBox(10);
        cartItemBox.getStyleClass().add("cart-item-panel");
        cartItemBox.setPadding(new Insets(5));
        cartItemBox.setAlignment(Pos.CENTER_LEFT);

        VBox textContainer = new VBox(2);
        Label itemName = new Label(item[0]);
        itemName.setFont(new Font(14));
        itemName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label itemDesc = new Label(item[1]);
        itemDesc.setFont(new Font(12));
        itemDesc.setStyle("-fx-text-fill: #B0B0B0;");

        Label itemPrice = new Label("$" + item[2]);
        itemPrice.setFont(new Font(14));
        itemPrice.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

        textContainer.getChildren().addAll(itemName, itemDesc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("remove-btn");
        removeButton.setFont(new Font(12));
        removeButton.setOnAction(e -> {
            cartItems.remove(item);
            displayCartContent();
        });

        cartItemBox.getChildren().addAll(textContainer, spacer, itemPrice, removeButton);
        cartBox.getChildren().add(cartItemBox);

        totalPriceWrapper[0] += Double.parseDouble(item[2]);
    }

    double totalPrice = totalPriceWrapper[0];

    Label totalLabel = new Label("Total Price: $" + totalPrice);
    totalLabel.setFont(new Font(16));
    totalLabel.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold;");

    // --- BUY CART BUTTON ---
    Button buyButton = new Button("BUY CART");
    buyButton.setStyle("-fx-background-color: #00C853; -fx-text-fill: white;");
    buyButton.setFont(new Font(14));
    buyButton.setOnAction(e -> {
        if (cartItems.isEmpty()) {
            showAlert2("Cart is empty!");
            return;
        }

        // Prepare buyed_items string
        StringBuilder buyedItems = new StringBuilder();
        for (String[] item : cartItems) {
            buyedItems.append(item[0]).append(" ($").append(item[2]).append("), ");
        }

        if (buyedItems.length() > 0) {
            buyedItems.setLength(buyedItems.length() - 2); // remove trailing comma
        }

        // Insert transaction into DB
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "INSERT INTO transactions (user_name, buyed_items, total_price) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, Login.loggedInUser); // Assuming this holds logged-in user ID
            pstmt.setString(2, buyedItems.toString());
            pstmt.setDouble(3, totalPrice);
            pstmt.executeUpdate();

            showAlert2("Transaction completed!");
            cartItems.clear();
            displayCartContent();
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert2("Transaction failed!");
        }
    });

    VBox wrapper = new VBox(10, cartBox, totalLabel, buyButton);
    wrapper.setAlignment(Pos.CENTER_LEFT);
    wrapper.setPadding(new Insets(10));

    contentArea.getChildren().add(wrapper);
}

private void showAlert2(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Cart");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}


private List<String[]> getSubServicesFromDB(String serviceTitle) {
    List<String[]> subServices = new ArrayList<>();

    String query = "SELECT ss.subservice_name, ss.description, ss.price " +
                   "FROM sub_services ss " +
                   "JOIN services s ON ss.service_name = s.service_name " +
                   "WHERE s.service_name = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, serviceTitle);  // Pass the service name like "Cleaning"
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("subservice_name");
            String desc = rs.getString("description");
            String price = rs.getString("price");
            subServices.add(new String[]{name, desc, price});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return subServices;
}

    private void displayAccountDetails() {
    contentArea.getChildren().clear();

    Label titleLabel = new Label("User Account");
    titleLabel.setFont(new Font(24));
    titleLabel.setStyle("-fx-text-fill: white;");

    if (Login.loggedInUser == null) {
        Label errorLabel = new Label("No user logged in.");
        errorLabel.setStyle("-fx-text-fill: red;");
        contentArea.getChildren().addAll(titleLabel, errorLabel);
        return;
    }

    String query = "SELECT fname, lname, email, phone FROM users WHERE username = ?";
    
    try {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, Login.loggedInUser);
        ResultSet rs = stmt.executeQuery();

        VBox allContent = new VBox(20);
        allContent.setPadding(new Insets(10));

        if (rs.next()) {
            String fname = rs.getString("fname");
            String lname = rs.getString("lname");
            String email = rs.getString("email");
            String phone = rs.getString("phone");

            GridPane grid = new GridPane();
            grid.setVgap(10);
            grid.setHgap(10);

            Label nameLabel = new Label("First Name:");
            TextField fnameField = new TextField(fname);
            fnameField.setEditable(false);

            Label lnameLabel = new Label("Last Name:");
            TextField lnameField = new TextField(lname);
            lnameField.setEditable(false);

            Label emailLabel = new Label("Email:");
            TextField emailField = new TextField(email);
            emailField.setEditable(false);

            Label phoneLabel = new Label("Phone:");
            TextField phoneField = new TextField(phone);
            phoneField.setEditable(false);

            Button editButton = new Button("Edit");
            Button saveButton = new Button("Save");
            saveButton.setDisable(true);

            editButton.setOnAction(e -> {
                fnameField.setEditable(true);
                lnameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);
                saveButton.setDisable(false);
            });

            saveButton.setOnAction(e -> {
                String newFname = fnameField.getText();
                String newLname = lnameField.getText();
                String newEmail = emailField.getText();
                String newPhone = phoneField.getText();

                if (!isValidEmail(newEmail)) {
                    showAlert("Invalid Email", "Please enter a valid email address.");
                    return;
                }
                if (!isValidPhone(newPhone)) {
                    showAlert("Invalid Phone", "Phone number must be 10 digits.");
                    return;
                }

                String updateQuery = "UPDATE users SET fname=?, lname=?, email=?, phone=? WHERE username=?";
                try (Connection updateConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
                     PreparedStatement updateStmt = updateConn.prepareStatement(updateQuery)) {

                    updateStmt.setString(1, newFname);
                    updateStmt.setString(2, newLname);
                    updateStmt.setString(3, newEmail);
                    updateStmt.setString(4, newPhone);
                    updateStmt.setString(5, Login.loggedInUser);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Success", "Your details have been updated.");
                        fnameField.setEditable(false);
                        lnameField.setEditable(false);
                        emailField.setEditable(false);
                        phoneField.setEditable(false);
                        saveButton.setDisable(true);
                    } else {
                        showAlert("Error", "Update failed. No rows affected.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Database Error", "Update failed. Check console for details.");
                }
            });

            grid.add(nameLabel, 0, 0);
            grid.add(fnameField, 1, 0);
            grid.add(lnameLabel, 0, 1);
            grid.add(lnameField, 1, 1);
            grid.add(emailLabel, 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(phoneLabel, 0, 3);
            grid.add(phoneField, 1, 3);
            grid.add(editButton, 0, 4);
            grid.add(saveButton, 1, 4);

            allContent.getChildren().add(grid);
        } else {
            Label errorLabel = new Label("User details not found.");
            errorLabel.setStyle("-fx-text-fill: red;");
            contentArea.getChildren().addAll(titleLabel, errorLabel);
            return;
        }

        stmt.close();
        conn.close();

        // --- Fetch and Display Transaction History ---
        Connection transConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025");
        String transQuery = "SELECT buyed_items, total_price FROM transactions WHERE user_name = ?";
        PreparedStatement transStmt = transConn.prepareStatement(transQuery);
        transStmt.setString(1, Login.loggedInUser);
        ResultSet transRs = transStmt.executeQuery();

        Label transLabel = new Label("Transaction History:");
        transLabel.setFont(new Font(18));
        transLabel.setStyle("-fx-text-fill: white; -fx-underline: true;");
        VBox transList = new VBox(10);

        while (transRs.next()) {
            String items = transRs.getString("buyed_items");
            double total = transRs.getDouble("total_price");

            VBox transBox = new VBox(5);
            transBox.setStyle("-fx-background-color: #2C2C2C; -fx-padding: 10; -fx-border-color: white; -fx-border-width: 1;");
            Label itemsLabel = new Label("Items: " + items);
            itemsLabel.setStyle("-fx-text-fill: white;");
            Label totalLabel = new Label("Total: $" + total);
            totalLabel.setStyle("-fx-text-fill: yellow;");

            transBox.getChildren().addAll(itemsLabel, totalLabel);
            transList.getChildren().add(transBox);
        }

        transStmt.close();
        transConn.close();

        allContent.getChildren().addAll(transLabel, transList);
        contentArea.getChildren().addAll(titleLabel, allContent);

    } catch (SQLException e) {
        e.printStackTrace();
        Label errorLabel = new Label("Database error. Please try again.");
        errorLabel.setStyle("-fx-text-fill: red;");
        contentArea.getChildren().addAll(titleLabel, errorLabel);
    }
}


private TextField createTextField(String value, boolean locked) {
    TextField textField = new TextField(value);
    textField.setEditable(!locked);
    textField.setStyle("-fx-background-color: #555; -fx-text-fill: white;");
    return textField;
}

/** Helper method to create labeled panels **/
private HBox createLabeledPanel(String labelText, TextField textField) {
    Label label = new Label(labelText);
    label.setStyle("-fx-text-fill: white;");
    HBox panel = new HBox(10, label, textField);
    panel.setPadding(new Insets(5));
    return panel;
}

/** Helper method to show alerts **/
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
private boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
}
private boolean isValidPhone(String phone) {
    return phone.matches("\\d{10}");
}

    public static void main(String[] args) {
        launch(args);
    }
}
