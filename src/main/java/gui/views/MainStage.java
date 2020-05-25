package gui.views;

import gui.controllers.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainStage {
	
	private MainController controller;
	private Stage mainStage;
	
	private MainStage() throws IOException {
		FXMLLoader loader = new FXMLLoader(MainStage.class.getResource("scenes/main.fxml"));
		Parent root = loader.load();
		mainStage = new Stage();
		mainStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		Scene mainScene = new Scene(root);
		mainStage.setScene(mainScene);
		controller = loader.getController();
		controller.setMainStage(this);
	}
	
	public static MainStage createMainStage() throws IOException {
		return new MainStage();
	}
	
	public MainController getController() {
		return controller;
	}
	
	public Stage getStage() {
		return mainStage;
	}
}
