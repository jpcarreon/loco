package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Window.fxml"));
			Scene scene = new Scene(root, WindowController.WINDOW_WIDTH, WindowController.WINDOW_HEIGHT);
			
			//scene.getStylesheets().add(getClass().getResource("Window.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Loco - Lolcode Interpreter");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
