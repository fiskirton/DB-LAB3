package gui.views;

import db.models.Record;
import gui.controllers.NewRecordController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NewRecordDialog {
	
	private Stage newRecordDialog;
	private final NewRecordController controller;
	
	private NewRecordDialog(Stage parentStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainStage.class.getResource("scenes/new-record-dialog.fxml"));
		Parent root = loader.load();
		newRecordDialog = new Stage();
		newRecordDialog.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		Scene newRecordScene = new Scene(root);
		newRecordDialog.setScene(newRecordScene);
		newRecordDialog.setTitle("New Record");
		controller = loader.getController();
		controller.setStage(newRecordDialog);
		newRecordDialog.initModality(Modality.WINDOW_MODAL);
		newRecordDialog.initOwner(parentStage);
		newRecordDialog.setResizable(false);
	}
	
	public static NewRecordDialog createNewRecordScene(Stage parentStage) throws IOException {
		return new NewRecordDialog(parentStage);
	}
	
	public Record showAndWait() {
		newRecordDialog.showAndWait();
		return controller.getResult();
	}

}
