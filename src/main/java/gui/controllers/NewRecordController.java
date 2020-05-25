package gui.controllers;

import db.models.DB;
import db.models.Item;
import gui.views.CustomAlert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.commons.text.*;

public class NewRecordController implements Initializable {
	
	private final DB db = DB.INSTANCE;
	private Item newItem;
	private Stage stage;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Button cancelButton;
	
	@FXML
	private VBox mainContainer;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}
	
	@FXML
	private void accept() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, SQLException {
		int nCols = mainContainer.getChildren().size();
		String[] data = new String[nCols];
		for (int i = 0; i < nCols; i++) {
			data[i] = WordUtils.capitalizeFully(
					((TextField)((HBox)mainContainer.getChildren().get(i)).getChildren().get(1)).getText()
					.strip()
					.replaceAll("\\s+"," ")
			);
			if (data[i].isEmpty() || data[i].isBlank()) {
				String emptyField = ((Label)((HBox)mainContainer.getChildren().get(i)).getChildren().get(0)).getText().strip();
				Alert fieldCannotBeEmptyWarning = CustomAlert.Builder()
						.alertType(Alert.AlertType.WARNING)
						.title("Warning")
						.content("Field '" + emptyField + "' can't be empty")
						.icon()
						.build();
				
				fieldCannotBeEmptyWarning.showAndWait();
				return;
			}
		}
		
		if (db.addItem(
				Integer.parseInt(data[0]),
				data[1],
				data[2],
				data[3],
				data[4],
				Integer.parseInt(data[5]),
				Integer.parseInt(data[6])
		)) {
			newItem = db.getItemById(Integer.parseInt(data[0]));
			stage.hide();
		} else {
			Alert itemExistsWarning = CustomAlert.Builder()
					.alertType(Alert.AlertType.WARNING)
					.title("Warning")
					.content("Item with given ID already exists")
					.icon()
					.build();
			
			itemExistsWarning.showAndWait();
			newItem = null;
		}
	}
	
	public Item getResult() {
		return newItem;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
	private void cancel() {
		stage.hide();
	}
}
