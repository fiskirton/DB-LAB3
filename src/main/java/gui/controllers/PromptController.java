package gui.controllers;

import db.models.DB;
import extra.Alerts;
import gui.views.CustomAlert;
import gui.views.MainStage;
import gui.views.PromptStage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PromptController implements Initializable {
	
	private final DB db = DB.INSTANCE;
	private PromptStage promptStage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			db.storeHostFunctions();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}
	
	public void createDB() throws SQLException {
		boolean response = db.createDB();
		if (response) {
			showInfoAlert("Database was created successfully");
		} else {
			showErrorAlert("Database already exists!");
		}
		
	}
	
	public void openDB() throws SQLException, IOException {
		boolean response = db.isExists();
		if (response) {
			db.storeMainFunctions();
			db.initDB();
			MainStage mainStage = MainStage.createMainStage();
			promptStage.getStage().hide();
			mainStage.getStage().show();
			
		} else {
			showErrorAlert("Database does not exist. Please, create database to continue");
		}
	}
	
	public void dropDB() throws SQLException {
		boolean response = db.dropDB();
		if (response) {
			showInfoAlert("Database was deleted successfully");
		} else {
			showErrorAlert("Database does not exist");
		}
	}
	
	private void showInfoAlert(String message) {
		Alerts.getInfoAlert(message).showAndWait();
	}
	
	private void showErrorAlert(String message) {
		Alerts.getErrorAlert(message).showAndWait();
	}
	
	public void setPromptStage(PromptStage promptStage) {
		this.promptStage = promptStage;
	}
}
