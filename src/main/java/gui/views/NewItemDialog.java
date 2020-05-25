package gui.views;

import db.models.Item;
import gui.controllers.NewRecordController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NewItemDialog {
	
	private Stage newItemDialog;
	private final NewRecordController controller;
	
	private NewItemDialog(Stage parentStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(MainStage.class.getResource("new-record-dialog.fxml"));
		Parent root = loader.load();
		newItemDialog = new Stage();
		newItemDialog.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
		Scene newRecordScene = new Scene(root);
		newItemDialog.setScene(newRecordScene);
		newItemDialog.setTitle("New Item");
		controller = loader.getController();
		controller.setStage(newItemDialog);
		newItemDialog.initModality(Modality.WINDOW_MODAL);
		newItemDialog.initOwner(parentStage);
	}
	
	public static NewItemDialog createNewRecordScene(Stage parentStage) throws IOException {
		return new NewItemDialog(parentStage);
	}
	
	public Item showAndWait() {
		newItemDialog.showAndWait();
		return controller.getResult();
	}

}
