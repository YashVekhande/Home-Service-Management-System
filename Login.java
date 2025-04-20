package urban;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.sql.*;

public class Login extends Application {

    public static String loggedInUser; // ✅ Store the logged-in username

    @Override
    public void start(Stage primaryStage) {
        // Welcome Section
        VBox welcomeBox = new VBox(10);
        welcomeBox.getStyleClass().add("welcome-box");
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(40));

        Image logoImage = new Image(getClass().getResourceAsStream("logo.png")); // adjust path if needed
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(400);  // Adjust size as needed
        logoImageView.setPreserveRatio(true);

        Text welcomeText = new Text("Welcome to ServEase");
        welcomeText.getStyleClass().add("welcome-text");

        Text descText = new Text("Service made easy");
        descText.getStyleClass().add("desc-text");

        welcomeBox.getChildren().addAll(logoImageView, welcomeText, descText);


        // Login Section
        VBox loginBox = new VBox(15);
        loginBox.getStyleClass().add("login-box");
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(40));

        Label loginLabel = new Label("User Login");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("input-field");
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("input-field");
        passwordField.setPromptText("Password");

        Label loginError = new Label();
        loginError.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setPrefHeight(40);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> validateLogin(usernameField, passwordField, loginError, primaryStage));

        Hyperlink signUpLink = new Hyperlink("Don't have an account? Sign up.");
        signUpLink.setOnAction(e -> showSignUpPage(loginBox, primaryStage));
        
        Hyperlink adminLoginLink = new Hyperlink("Admin? Login here.");
        adminLoginLink.setOnAction(e -> showAdminLoginPage(loginBox, primaryStage));


        loginBox.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton, signUpLink, adminLoginLink,loginError);

        // Main Layout
        HBox mainLayout = new HBox();
        HBox.setHgrow(welcomeBox, Priority.ALWAYS);
        HBox.setHgrow(loginBox, Priority.ALWAYS);
        mainLayout.getChildren().addAll(welcomeBox, loginBox);

        Scene scene = new Scene(mainLayout, 800, 450);
        scene.getStylesheets().add(getClass().getResource("LoginStyle.css").toExternalForm());

        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
private void showAdminLoginPage(VBox loginBox, Stage primaryStage) {
    loginBox.getChildren().clear();

    Label adminLoginLabel = new Label("Admin Login");
    adminLoginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

    TextField adminUsername = new TextField();
    adminUsername.setPromptText("Admin Username");

    PasswordField adminPassword = new PasswordField();
    adminPassword.setPromptText("Admin Password");

    Label errorLabel = new Label();
    errorLabel.setStyle("-fx-text-fill: red;");

    Button loginButton = new Button("Login as Admin");
    loginButton.getStyleClass().add("login-button");
    loginButton.setPrefHeight(40);
    loginButton.setMaxWidth(Double.MAX_VALUE);

    loginButton.setOnAction(e -> {
        String user = adminUsername.getText().trim();
        String pass = adminPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {

            String query = "SELECT * FROM admins WHERE a_username = ? AND a_password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Admin login successful!");
                AdminPage adminPage = new AdminPage();
                adminPage.start(primaryStage);
            } else {
                errorLabel.setText("Invalid admin credentials.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Database error.");
        }
    });

    Button backButton = new Button("Back to User Login");
    backButton.getStyleClass().add("back-button");
    backButton.setPrefHeight(40);
    backButton.setMaxWidth(Double.MAX_VALUE);
    backButton.setOnAction(e -> start(primaryStage));

    loginBox.getChildren().addAll(adminLoginLabel, adminUsername, adminPassword, loginButton, backButton, errorLabel);
}

    private void validateLogin(TextField username, PasswordField password, Label loginError, Stage primaryStage) {
        String userText = username.getText().trim();
        String passText = password.getText().trim();

        if (userText.isEmpty() || passText.isEmpty()) {
            loginError.setText("All fields are required!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userText);
            stmt.setString(2, passText);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loggedInUser = userText; // ✅ Store logged-in username
                System.out.println("Login Successful! Welcome, " + loggedInUser);

                // Show loading page
                LoadingPage loadingPage = new LoadingPage();
                loadingPage.start(primaryStage);
            } else {
                loginError.setText("Invalid username or password.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            loginError.setText("Database error. Please try again.");
        }
    }

    private void showSignUpPage(VBox loginBox, Stage primaryStage) {
        loginBox.getChildren().clear();
        Label signUpLabel = new Label("User Sign Up");
        signUpLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (Min 8 chars & 1 special char)");

        TextField fnameField = new TextField();
        fnameField.setPromptText("First Name");

        TextField lnameField = new TextField();
        lnameField.setPromptText("Last Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (10 digits)");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button signUpButton = new Button("Sign Up");
        signUpButton.getStyleClass().add("signup-button");
        signUpButton.setPrefHeight(40);
        signUpButton.setMaxWidth(Double.MAX_VALUE);
        signUpButton.setOnAction(e -> insertSignUpData(usernameField, passwordField, fnameField, lnameField, emailField, phoneField, errorLabel, primaryStage));

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().add("back-button");
        backButton.setPrefHeight(40);
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setOnAction(e -> start(primaryStage)); // Reloads login page

        loginBox.getChildren().addAll(signUpLabel, usernameField, passwordField, fnameField, lnameField, emailField, phoneField, signUpButton, backButton, errorLabel);
    }

    private void insertSignUpData(TextField username, PasswordField password, TextField fname, TextField lname, TextField email, TextField phone, Label errorLabel, Stage primaryStage) {
        String userText = username.getText().trim();
        String passText = password.getText().trim();
        String firstName = fname.getText().trim();
        String lastName = lname.getText().trim();
        String emailText = email.getText().trim();
        String phoneText = phone.getText().trim();

        if (userText.isEmpty() || passText.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || emailText.isEmpty() || phoneText.isEmpty()) {
            errorLabel.setText("All fields are required!");
            return;
        }

        if (passText.length() < 8 || !passText.matches(".*[!@#$%^&*()].*")) {
            errorLabel.setText("Password must be at least 8 characters & contain 1 special character.");
            return;
        }

        if (!emailText.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        if (!phoneText.matches("\\d{10}")) {
            errorLabel.setText("Phone number must be 10 digits.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/servease", "root", "Ya$h2025")) {

            // 🔍 Check if Username Already Exists
            String checkUserQuery = "SELECT username FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                checkStmt.setString(1, userText);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    username.setText("");
                    username.setPromptText("Username already taken. Please choose another.");
                    return;
                }
            }

            // ✅ Insert New User
            String query = "INSERT INTO users (username, password, fname, lname, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userText);
            stmt.setString(2, passText);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setString(5, emailText);
            stmt.setString(6, phoneText);
            stmt.executeUpdate();

            loggedInUser = userText; 
            System.out.println("Sign Up Successful! Welcome, " + loggedInUser);

            // 🎬 Show Loading Page After Sign-Up
            LoadingPage loadingPage = new LoadingPage();
            loadingPage.start(primaryStage);

        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Error signing up. Please try again.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
