package urban;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoadingPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        showLoadingScreen(primaryStage);
    }

    private void showLoadingScreen(Stage primaryStage) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(400);
        progressBar.getStyleClass().add("progress-bar");

        Label loadingLabel = new Label("Loading, please wait...");
        loadingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loadingLabel.getStyleClass().add("loading-text");

        VBox loadingBox = new VBox(30, progressBar, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.getStyleClass().add("loading-background");

        Scene loadingScene = new Scene(loadingBox, 800, 450);
        loadingScene.getStylesheets().add(getClass().getResource("LoadingStyle.css").toExternalForm());

        primaryStage.setScene(loadingScene);
        primaryStage.setTitle("Loading...");
        primaryStage.setMaximized(true);  // Fullscreen mode
        primaryStage.show();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);  // Smooth loading animation
                    final double progress = i / 100.0;
                    Platform.runLater(() -> progressBar.setProgress(progress));
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            Urban homeApp = new Urban();
            try {
                homeApp.start(primaryStage);  // Launch homepage after loading
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));

        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
