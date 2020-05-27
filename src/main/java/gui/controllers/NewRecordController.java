package gui.controllers;

import db.models.DB;
import db.models.Record;
import extra.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	private Record newRecord;
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
				Alerts.getWarningAlert("Field '" + emptyField + "' can't be empty").showAndWait();
				return;
			}
		}

		String response = db.addRecord(
				data[0],
				data[1],
				data[2],
				data[3],
				Integer.parseInt(data[4]),
				Integer.parseInt(data[5])
		);

		try {
			if (!response.equals("error")) {
				newRecord = db.getRecordById(response);
				stage.hide();
			} else {
				Alerts.getWarningAlert("Such record already exists").showAndWait();
				newRecord = null;
			}
		} catch (NumberFormatException exception) {
			Alerts.getErrorAlert("Too big number for number input field").showAndWait();
		}
	}
	
	public Record getResult() {
		return newRecord;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	@FXML
	private void cancel() {
		stage.hide();
	}
}
