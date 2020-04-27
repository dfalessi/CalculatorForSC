package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class Main extends Application {
	@Override
public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("calculator_form.fxml"));
		primaryStage.setTitle("Calculator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        Alert a = new Alert(AlertType.INFORMATION);
        a.setHeaderText("Tip");
        a.setContentText("You can click the result of a calculation to use it again."); 
        a.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
