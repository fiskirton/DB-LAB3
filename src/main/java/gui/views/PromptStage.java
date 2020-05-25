package gui.views;

import gui.controllers.PromptController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PromptStage {
	
	private Stage promptStage;
	private PromptController controller;
	
	private PromptStage () throws IOException {
		FXMLLoader loader = new FXMLLoader(PromptStage.class.getResource("prompt.fxml"));
		Parent root = loader.load();
		promptStage = new Stage();
		promptStage.setResizable(false);
		promptStage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		Scene promptScene = new Scene(root);
		promptStage.setScene(promptScene);
		controller = loader.getController();
		controller.setPromptStage(this);
	}
	
	public static PromptStage createPromptStage() throws IOException {
		return new PromptStage();
	}
	
	public Stage getStage() {
		return promptStage;
	}
	
	public PromptController getController() {
		return controller;
	}
}
