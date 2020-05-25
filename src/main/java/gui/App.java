package gui;

import gui.views.MainStage;
import gui.views.PromptStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		PromptStage promptStage = PromptStage.createPromptStage();
		promptStage.getStage().show();
	}
}
