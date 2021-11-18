package frontend;

import java.io.File;

import backend.Parser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {		
		//startParser();
		launch(args);
	}
	
	static void startParser() {
		File fp = new File("src/sample.lol");
		Parser parser = new Parser(fp);

		System.out.println(parser.parse().getStrChildren(0));
		parser.viewErrors();
		
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Window.fxml"));
			Scene scene = new Scene(root, WindowController.WINDOW_WIDTH, WindowController.WINDOW_HEIGHT);
			
			scene.getStylesheets().add(getClass().getResource("Window.css").toExternalForm());
			
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Loco - Lolcode Interpreter");
			
			primaryStage.setMinHeight(WindowController.WINDOW_HEIGHT);
			primaryStage.setMinWidth(WindowController.WINDOW_WIDTH);
			
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
