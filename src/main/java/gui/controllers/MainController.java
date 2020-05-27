package gui.controllers;

import db.models.*;
import db.models.Record;
import extra.Alerts;
import gui.custom.LimitedTextFieldTableCell;
import gui.custom.RangeTextField;
import gui.views.MainStage;
import gui.views.NewRecordDialog;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MainController implements Initializable {
	
	private final DB db = DB.INSTANCE;
	
	private MainStage mainStage;
	
	private TableView<Record> recordsTable;
	private TableView<Item> itemsTable;
	private TableView<Location> locationsTable;
	private TableView<DropType> dropTypesTable;
	private TableView<Category> categoriesTable;
	private TableView<? extends BaseModel> currentTable;
	
	private ObservableList<Record> records;
	private ObservableList<Item> items;
	private ObservableList<Location> locations;
	private ObservableList<DropType> dropTypes;
	private ObservableList<Category> categories;
	private ObservableList<? extends BaseModel> currentDataset;
	
	private ObservableList<Record> recordsSearches;

	private List<Integer> recordsChanged;
	private List<Integer> itemsChanged;
	private List<Integer> locationsChanged;
	private List<Integer> dropTypesChanged;
	private List<Integer> categoriesChanged;
	
	private Class<? extends BaseModel> currentTableClass;
	
	@FXML
	private AnchorPane tablePane;
	
	@FXML
	private TextField searchArea;
	
	@FXML
	private Button deleteRecordButton;
	
	@FXML
	private Button commitButton;
	
	@FXML
	private Button removeAllButton;
	
	@FXML
	private Button rollbackButton;
	
	@FXML
	private Button truncateAllButton;
	
	@FXML
	private Button truncateButton;
	
	@FXML
	private ComboBox<Location> locationFilter;
	
	@FXML
	private ComboBox<DropType> dropTypeFilter;
	
	@FXML
	private ComboBox<Category> categoryFilter;
	
	@FXML
	private VBox filterBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initRecordsTable();
		initItemsTable();
		initLocationsTable();
		initDropTypesTable();
		initCategoriesTable();
		
		try {
			setRecords();
			setItems();
			setLocations();
			setDropTypes();
			setCategories();
			setLocationFilter();
			setDropTypeFilter();
			setCategoryFilter();
		} catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException throwables) {
			throwables.printStackTrace();
		}
		
		setCurrentTable(recordsTable);
		setCurrentDataset(records);
		showRecordsTable();
	}
	
	public void showRecordsTable() {
		currentTableClass = Record.class;
		if (recordsTable.getItems() == recordsSearches) {
			recordsTable.setItems(records);
		}
		showTable(recordsTable, records);
	}

	public void showItemsTable() {
		currentTableClass = Item.class;
		showTable(itemsTable, items);
	}

	public void showLocationsTable() {
		currentTableClass = Location.class;
		showTable(locationsTable, locations);
	}
	
	public void showDropTypesTable() {
		currentTableClass = DropType.class;
		showTable(dropTypesTable, dropTypes);
	}
	
	public void showCategoriesTable() {
		currentTableClass = Category.class;
		showTable(categoriesTable, categories);
	}
	
	public void showTable(TableView<? extends BaseModel> tableView, ObservableList<? extends BaseModel> dataset) {
		if (tablePane.getChildren().size() != 0) {
			tablePane.getChildren().remove(0);
		}
		
		tablePane.getChildren().add(tableView);
		currentTable = tableView;
		currentDataset = dataset;
		
		if (currentDataset.isEmpty()) {
			truncateButton.setDisable(true);
			truncateAllButton.setDisable(true);
		} else {
			truncateButton.setDisable(false);
			truncateAllButton.setDisable(false);
		}
		
		if (currentTableClass.isAssignableFrom(Record.class)) {
			filterBox.setDisable(false);
			searchArea.setDisable(false);
		} else {
			filterBox.setDisable(true);
			searchArea.setDisable(true);
		}
		
		deleteRecordButton.setDisable(true);
		removeAllButton.setDisable(true);
		currentTable.getSelectionModel().clearSelection();
		searchArea.setText("");
		locationFilter.setValue(null);
		dropTypeFilter.setValue(null);
		categoryFilter.setValue(null);
	}
	
	public void setRecords() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		List<Record> recordsNew = db.getRecords();
		if (records == null) {
			records = FXCollections.observableList(recordsNew);
		} else {
			records.clear();
			records.setAll(recordsNew);
		}
		recordsTable.setItems(records);
	}

	public void setItems() throws InvocationTargetException, SQLException, InstantiationException, NoSuchMethodException, IllegalAccessException {
		List<Item> itemsNew = db.getItems();
		if (items == null) {
			items = FXCollections.observableList(itemsNew);
			itemsTable.setItems(items);
		} else {
			items.clear();
			items.setAll(itemsNew);
		}
		itemsTable.setItems(items);
	}
	
	public void setLocations() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		List<Location> locationsNew = db.getLocations();
		if (locations == null) {
			locations = FXCollections.observableList(locationsNew);
			locationsTable.setItems(locations);
		} else {
			locations.clear();
			locations.setAll(locationsNew);
		}
		locationsTable.setItems(locations);
	}
	
	public void setDropTypes() throws InvocationTargetException, SQLException, InstantiationException, NoSuchMethodException, IllegalAccessException {
		List<DropType> dropTypesNew = db.getDropTypes();
		if (dropTypes == null) {
			dropTypes = FXCollections.observableList(db.getDropTypes());
		} else {
			dropTypes.clear();
			dropTypes.setAll(dropTypesNew);
		}
		dropTypesTable.setItems(dropTypes);
	}
	
	public void setCategories() throws InvocationTargetException, SQLException, InstantiationException, NoSuchMethodException, IllegalAccessException {
		List<Category> categoriesNew = db.getCategories();
		if (categories == null) {
			categories = FXCollections.observableList(db.getCategories());
		} else {
			categories.clear();
			categories.setAll(categoriesNew);
		}
		categoriesTable.setItems(categories);
	}

	public void setLocationFilter() {
		locationFilter.setItems(locations);
	}
	
	public void setDropTypeFilter() {
		dropTypeFilter.setItems(dropTypes);
	}
	
	public void setCategoryFilter() {
		categoryFilter.setItems(categories);
	}
	
	public void initRecordsTable() {
		recordsTable = new TableView<>();
		TableColumn<Record, String> recordId = new TableColumn<>("ID");
		TableColumn<Record, String> item = new TableColumn<>("Title");
		TableColumn<Record, String> location = new TableColumn<>("Location");
		TableColumn<Record, String> dropType = new TableColumn<>("Drop type");
		TableColumn<Record, String> category = new TableColumn<>("Category");
		TableColumn<Record, Integer> ng = new TableColumn<>("NG");
		TableColumn<Record, Integer> sellingPrice = new TableColumn<>("Selling price");
		
		recordId.setCellValueFactory(cellData -> cellData.getValue().recordIdProperty());
		item.setCellValueFactory(cellData -> cellData.getValue().itemProperty());
		location.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
		dropType.setCellValueFactory(cellData -> cellData.getValue().dropTypeProperty());
		category.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
		ng.setCellValueFactory(cellData -> cellData.getValue().ngProperty().asObject());
		sellingPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
		
		recordsChanged = new ArrayList<>();
		
		item.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(recordsChanged)));
		location.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(recordsChanged)));
		dropType.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(recordsChanged)));
		category.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(recordsChanged)));
		ng.setCellFactory(LimitedTextFieldTableCell.forTableColumn(new RangeTextField(), stringConverter(), getCellEvent(recordsChanged)));
		
		ObservableList<TableColumn<Record, ?>> columns = FXCollections.observableList(Arrays.asList(
				recordId,
				item,
				location,
				dropType,
				category,
				ng,
				sellingPrice
		));
		
		recordsTable.getSelectionModel().selectedItemProperty().addListener(rowSelected);
		
		makeTable(columns, recordsTable);
	}

	public void initItemsTable() {
		itemsTable = new TableView<>();
		TableColumn<Item, Integer> itemId = new TableColumn<>("Item ID");
		TableColumn<Item, String> itemTitle = new TableColumn<>("Item");

		itemId.setCellValueFactory(cellData -> cellData.getValue().itemIdProperty().asObject());
		itemTitle.setCellValueFactory(cellData -> cellData.getValue().itemTitleProperty());

		itemsChanged = new ArrayList<>();

		itemTitle.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(itemsChanged)));

		ObservableList<TableColumn<Item, ?>> columns = FXCollections.observableList(Arrays.asList(itemId, itemTitle));

		itemsTable.getSelectionModel().selectedItemProperty().addListener(rowSelected);

		makeTable(columns, itemsTable);
	}
	
	public void initLocationsTable() {
		locationsTable = new TableView<>();
		TableColumn<Location, Integer> locationId = new TableColumn<>("Location ID");
		TableColumn<Location, String> locationTitle = new TableColumn<>("Location");
		
		locationId.setCellValueFactory(cellData -> cellData.getValue().locationIdProperty().asObject());
		locationTitle.setCellValueFactory(cellData -> cellData.getValue().locationTitleProperty());
		
		locationsChanged = new ArrayList<>();
		
		locationTitle.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(locationsChanged)));
		
		ObservableList<TableColumn<Location, ?>> columns = FXCollections.observableList(Arrays.asList(locationId, locationTitle));
		
		locationsTable.getSelectionModel().selectedItemProperty().addListener(rowSelected);
		
		makeTable(columns, locationsTable);
	}
	
	public void initDropTypesTable() {
		dropTypesTable = new TableView<>();
		TableColumn<DropType, Integer> dropTypeId = new TableColumn<>("Drop type ID");
		TableColumn<DropType, String> dropType = new TableColumn<>("Drop type");
		
		dropTypeId.setCellValueFactory(cellData -> cellData.getValue().dropTypeIdProperty().asObject());
		dropType.setCellValueFactory(cellData -> cellData.getValue().dropTypeProperty());
		
		dropTypesChanged = new ArrayList<>();
		
		dropType.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(dropTypesChanged)));
		
		ObservableList<TableColumn<DropType, ?>> columns = FXCollections.observableList(Arrays.asList(dropTypeId, dropType));
		
		dropTypesTable.getSelectionModel().selectedItemProperty().addListener(rowSelected);
		
		makeTable(columns, dropTypesTable);
	}
	
	public void initCategoriesTable() {
		categoriesTable = new TableView<>();
		TableColumn<Category, Integer> categoryId = new TableColumn<>("Category ID");
		TableColumn<Category, String> categoryTitle = new TableColumn<>("Category");
		
		categoryId.setCellValueFactory(cellData -> cellData.getValue().categoryIdProperty().asObject());
		categoryTitle.setCellValueFactory(cellData -> cellData.getValue().categoryTitleProperty());
		
		categoriesChanged = new ArrayList<>();
		
		categoryTitle.setCellFactory(LimitedTextFieldTableCell.forTableColumn(getCellEvent(categoriesChanged)));
		
		ObservableList<TableColumn<Category, ?>> columns = FXCollections.observableList(Arrays.asList(categoryId, categoryTitle));
		
		categoriesTable.getSelectionModel().selectedItemProperty().addListener(rowSelected);
		
		makeTable(columns, categoriesTable);
	}
	
	public <T extends BaseModel> void makeTable(ObservableList<TableColumn<T, ?>> columns, TableView<T> table) {
		table.getColumns().addAll(columns);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(true);
		stretchTable(table);
	}
	
	public void stretchTable (Node childNode) {
		AnchorPane.setTopAnchor(childNode, 0.0);
		AnchorPane.setBottomAnchor(childNode, 0.0);
		AnchorPane.setLeftAnchor(childNode, 0.0);
		AnchorPane.setRightAnchor(childNode, 0.0);
	}
	
	public void setCurrentDataset(ObservableList<? extends BaseModel> currentDataset) {
		this.currentDataset = currentDataset;
	}
	
	public void setCurrentTable(TableView<? extends BaseModel> currentTable) {
		this.currentTable = currentTable;
	}
	
	
	/**
	 * Database interactions
	 */
	
	@FXML
	private void addRecord() throws IOException, SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		NewRecordDialog newRecordDialog = NewRecordDialog.createNewRecordScene(mainStage.getStage());
		Record newRecord = newRecordDialog.showAndWait();
		if (newRecord != null) {
			records.add(newRecord);
			setItems();
			setLocations();
			setDropTypes();
			setCategories();
		}
	}
	
	@FXML
	private void deleteSelectedRow() throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(showConfirmationAlert("Confirm deletion of the selected row")){
			return;
		}
		
		Class<? extends BaseModel> modelClass = currentTable.getSelectionModel().getSelectedItem().getClass();
		boolean response = false;
		BaseModel selectedRow = currentTable.getSelectionModel().getSelectedItem();
		
		if (modelClass != null) {
			if (modelClass.isAssignableFrom(Record.class)) {
			    response = db.deleteRecord(Integer.parseInt(((Record)selectedRow).getRecordId(), 16));
			}

			if (modelClass.isAssignableFrom(Item.class)) {
				assert selectedRow instanceof Item;
				response = db.deleteItem(((Item)selectedRow).getItemTitle());
				setRecords();
			}

			if (modelClass.isAssignableFrom(Location.class)) {
				assert selectedRow instanceof Location;
				response = db.deleteLocation(((Location)selectedRow).getLocationTitle());
				setRecords();
			}
			
			if (modelClass.isAssignableFrom(DropType.class)) {
				assert selectedRow instanceof DropType;
				response = db.deleteDropType(((DropType)selectedRow).getDropType());
				setRecords();
			}
			
			if (modelClass.isAssignableFrom(Category.class)) {
				assert selectedRow instanceof Category;
				response = db.deleteCategory(((Category)selectedRow).getCategoryTitle());
				setRecords();
			}

		}
		
		if (response) {
			int selectedRowIndex = currentTable.getSelectionModel().getSelectedIndex();
			currentDataset.remove(selectedRowIndex);
		}
	}
	
	@FXML
	private void deleteAllRows() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		if(showConfirmationAlert("Confirm the deletion of all rows with the selected title")){
			return;
		}
		Record selectedRecord = (Record) currentTable.getSelectionModel().getSelectedItem();
		boolean response = db.deleteRecords(selectedRecord.getItem());
		if (response) {
			setRecords();
		}
	}
	
	@FXML
	private void commitChanges() throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Record record;
		Item item;
		Location location;
		Category category;
		DropType dropType;

		ListIterator<Integer> itemsIter = itemsChanged.listIterator();
		ListIterator<Integer> locationIter = locationsChanged.listIterator();
		ListIterator<Integer> dropTypesIter = dropTypesChanged.listIterator();
		ListIterator<Integer> categoriesIter = categoriesChanged.listIterator();

		boolean response;

		Alert uniqueConstraintError = Alerts.getErrorAlert("A field with this value already exists! Row: ");

		for (int index : recordsChanged) {
			record = records.get(index);
			db.editRecord(
					record.getRecordId(),
					record.getItem(),
					record.getLocation(),
					record.getDropType(),
					record.getCategory(),
					record.getNg()
			);
		}

		while (itemsIter.hasNext()) {
			int index = itemsIter.next();
			item = items.get(index);
			response = db.editItem(
					item.getItemId(),
					item.getItemTitle()
			);

			if (!response) {
				uniqueConstraintError.setContentText(uniqueConstraintError.getContentText() + index);
				uniqueConstraintError.showAndWait();
			} else {
				itemsIter.remove();
			}
		}

		while (locationIter.hasNext()) {
			int index = locationIter.next();
			location = locations.get(index);
			response = db.editLocation(
					location.getLocationId(),
					location.getLocationTitle()
			);

			if (!response) {
				uniqueConstraintError.setContentText(uniqueConstraintError.getContentText() + index);
				uniqueConstraintError.showAndWait();
			} else {
				locationIter.remove();
			}
		}
		
		while (dropTypesIter.hasNext()){
			int index = dropTypesIter.next();
			dropType = dropTypes.get(index);
			response = db.editDropType(
					dropType.getDropTypeId(),
					dropType.getDropType()
			);
			
			if (!response) {
				uniqueConstraintError.setContentText(uniqueConstraintError.getContentText() + index);
				uniqueConstraintError.showAndWait();
			} else {
				dropTypesIter.remove();
			}
		}
		
		while (categoriesIter.hasNext()){
			int index = categoriesIter.next();
			category = categories.get(index);
			response = db.editCategory(
					category.getCategoryId(),
					category.getCategoryTitle()
			);
			
			if (!response) {
				uniqueConstraintError.setContentText(uniqueConstraintError.getContentText() + index);
				uniqueConstraintError.showAndWait();
			} else {
				categoriesIter.remove();
			}
		}

		setRecords();
		setItems();
		setLocations();
		setDropTypes();
		setCategories();

		commitButton.setDisable(true);
		rollbackButton.setDisable(true);
	}
	
	@FXML
	private void rollbackChanges() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		if (!recordsChanged.isEmpty()) {
			setRecords();
			recordsChanged.clear();
		}

		if (!itemsChanged.isEmpty()) {
			setItems();
			itemsChanged.clear();
		}
		
		if (!locationsChanged.isEmpty()) {
			setLocations();
			locationsChanged.clear();
		}
		
		if (!dropTypesChanged.isEmpty()) {
			setDropTypes();
			dropTypesChanged.clear();
		}
		
		if (!categoriesChanged.isEmpty()) {
			setCategories();
			categoriesChanged.clear();
		}
		
		rollbackButton.setDisable(true);
		commitButton.setDisable(true);
		
	}
	
	@FXML
	private void truncateCurrentTable() throws SQLException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if(showConfirmationAlert("Confirm truncating the current table")){
			return;
		}
		
		if (!currentDataset.isEmpty()) {
			if (currentTableClass.isAssignableFrom(Record.class)) {
				if (db.truncateRecords()) {
					records.clear();
					recordsChanged.clear();
				}
			}

			if (currentTableClass.isAssignableFrom(Item.class)) {
				if (db.truncateItems()) {
					items.clear();
					itemsChanged.clear();
				}
			}
			
			if (currentTableClass.isAssignableFrom(Location.class)) {
				if (db.truncateLocations()) {
					locations.clear();
					locationsChanged.clear();
				}
			}
			
			if (currentTableClass.isAssignableFrom(DropType.class)) {
				if (db.truncateDropTypes()) {
					dropTypes.clear();
					dropTypesChanged.clear();
				}
			}
			
			if (currentTableClass.isAssignableFrom(Category.class)) {
				if (db.truncateCategories()) {
					categories.clear();
					categoriesChanged.clear();
				}
			}

			setRecords();
			truncateButton.setDisable(true);
		}
	}
	
	@FXML
	private void truncateAllTables() throws SQLException {
		if(showConfirmationAlert("Confirm truncating all tables")){
			return;
		}
		
		if (db.truncateAll()) {
			
			records.clear();
			items.clear();
			locations.clear();
			dropTypes.clear();
			categories.clear();
			
			recordsChanged.clear();
			itemsChanged.clear();
			locationsChanged.clear();
			dropTypesChanged.clear();
			categoriesChanged.clear();
			
			truncateButton.setDisable(true);
			truncateAllButton.setDisable(true);
		}
	}
	
	@FXML
	private void findItems() throws InvocationTargetException, SQLException, InstantiationException, NoSuchMethodException, IllegalAccessException {
		Alert notFoundWarning = Alerts.getWarningAlert("Records with the specified filters not found");
		
		recordsSearches = FXCollections.observableList(db.findRecords(
				searchArea.getText(),
				getFilterValue(locationFilter),
				getFilterValue(dropTypeFilter),
				getFilterValue(categoryFilter)
		));
		
		if (!recordsSearches.isEmpty()) {
			recordsTable.setItems(recordsSearches);
		} else {
			notFoundWarning.showAndWait();
		}
	}
	
	public String getFilterValue(ComboBox<? extends BaseModel> filter) {
		if (filter.getValue() != null) {
			return filter.getValue().toString();
		}
		else return "";
	}
	
	public void setMainStage(MainStage mainStage) {
		this.mainStage = mainStage;
	}
	
	private final InvalidationListener rowSelected = (selection) -> {
		if (currentTable.getSelectionModel().getSelectedItems().size() != 0) {
			if (currentTable.getSelectionModel().getSelectedItem().getClass().isAssignableFrom(Record.class)) {
				removeAllButton.setDisable(false);
			}
			deleteRecordButton.setDisable(false);
		} else {
			removeAllButton.setDisable(true);
			deleteRecordButton.setDisable(true);
		}
	};
	
	private <T extends BaseModel, E> EventHandler<TableColumn.CellEditEvent<T, E>> getCellEvent(List<Integer> changedIndices) {
		return ((TableColumn.CellEditEvent<T, E> event) -> {
			if (!event.getNewValue().equals(event.getOldValue())) {
				Integer changedRow = event.getTablePosition().getRow();
				if (!changedIndices.contains(changedRow)) {
					changedIndices.add(changedRow);
				}
				if (commitButton.isDisabled()) {
					commitButton.setDisable(false);
				}
				if (rollbackButton.isDisabled()) {
					rollbackButton.setDisable(false);
				}
			}
			System.out.println(changedIndices.size());
		});
	}
	
	private StringConverter<Integer> stringConverter() {
		return new StringConverter<>() {
			@Override
			public String toString(Integer object) {
				return String.valueOf(object);
			}
			
			@Override
			public Integer fromString(String string) {
				return Integer.parseInt(string);
			}
		};
	}
	
	private boolean showConfirmationAlert(String content) {
		Optional<ButtonType> response = Alerts.getConfirmationAlert(content).showAndWait();
		return response.get() != ButtonType.OK;
	}
}
