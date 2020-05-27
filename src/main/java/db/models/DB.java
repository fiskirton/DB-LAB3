package db.models;

import extra.SQLFuncs;
import javafx.beans.property.StringProperty;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public enum DB {
	INSTANCE;
	
	private static final String HOST_DB_URL = "jdbc:postgresql://localhost:5432/sodae";
	private static final String MAIN_DB_URL = "jdbc:postgresql://localhost:5432/ds1_armory";
	private static final String PG_PASS = System.getenv("PGPASSWORD");
	private static final String PG_USER = System.getenv("PGUSER");
	private Connection host_connection;
	private Connection main_connection;
	
	public void storeHostFunctions() throws SQLException {
		host_connection = openConnection(HOST_DB_URL);
		Statement storeHostFuncs = host_connection.createStatement();
		storeHostFuncs.execute(SQLFuncs.HOST_FUNCS);
		closeConnection(host_connection);
	}
	
	public boolean createDB() throws SQLException {
		host_connection = openConnection(HOST_DB_URL);
		
		CallableStatement createDBStatement = host_connection.prepareCall("{? = call create_db(?)}");
		createDBStatement.registerOutParameter(1, Types.BOOLEAN);
		createDBStatement.setString(2, PG_PASS);
		createDBStatement.executeUpdate();
		
		boolean response = createDBStatement.getBoolean(1);
		
		closeConnection(host_connection);
		
		return response;
	}
	
	public boolean dropDB() throws SQLException {
		host_connection = openConnection(HOST_DB_URL);
		
		CallableStatement dropDBStatement = host_connection.prepareCall("{? = call drop_db(?)}");
		dropDBStatement.registerOutParameter(1, Types.BOOLEAN);
		dropDBStatement.setString(2, PG_PASS);
		dropDBStatement.executeUpdate();
		
		boolean response = dropDBStatement.getBoolean(1);
		
		closeConnection(host_connection);
		
		return response;
	}
	
	public boolean isExists() throws SQLException {
		host_connection = openConnection(HOST_DB_URL);
		
		CallableStatement isExistStatement = host_connection.prepareCall("{? = call is_exists}");
		isExistStatement.registerOutParameter(1, Types.BOOLEAN);
		isExistStatement.executeUpdate();
		
		boolean response = isExistStatement.getBoolean(1);
		
		closeConnection(host_connection);
		
		return response;
	}
	
	public void storeMainFunctions() throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		Statement storeMainFuncs = main_connection.createStatement();
		storeMainFuncs.execute(SQLFuncs.MAIN_FUNCS);
		closeConnection(main_connection);
	}
	
	public void initDB() throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		
		CallableStatement initDBStatement = main_connection.prepareCall("{call init_db}");
		initDBStatement.executeUpdate();
		
		closeConnection(main_connection);
	}
	
	public <T extends BaseModel> List<T> getData(Class<T> tClass, String funcStatement) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement getDataStatement = main_connection.prepareCall(funcStatement);
		ResultSet rs = getDataStatement.executeQuery();
		
		closeConnection(main_connection);
		
		return extractData(tClass, rs);
	}
	
	public <T extends  BaseModel> List<T> extractData(Class<T> tClass, ResultSet rs) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		List<T> data = new ArrayList<>();
		
		int countColumn = rs.getMetaData().getColumnCount();
		
		while (rs.next()) {
			String[] row = new String[countColumn];
			
			for (int i = 1; i <= countColumn; i++) {
				row[i - 1] = rs.getString(i);
			}
			
			data.add(
					T.getInstance(tClass, row)
			);
		}
		
		return data;
	}

	public List<Record> getRecords() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return getData(Record.class, "{call get_records}");
	}
 
	public List<Item> getItems() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return getData(Item.class, "{call get_items}");
	}
	
	public List<Location> getLocations() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return getData(Location.class, "{call get_locations}");
	}
	
	public List<DropType> getDropTypes() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return getData(DropType.class, "{call get_drop_types}");
	}
	
	public List<Category> getCategories() throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		return getData(Category.class, "{call get_categories}");
	}
	
	public String addRecord(String item, String location, String dropType, String category, int basePrice, int ng) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement addRecordStatment = main_connection.prepareCall("{? = call add_record(?,?,?,?,?,?)}");
		
		addRecordStatment.registerOutParameter(1, Types.VARCHAR);
		addRecordStatment.setString(2, item);
		addRecordStatment.setString(3, location);
		addRecordStatment.setString(4, dropType);
		addRecordStatment.setString(5, category);
		addRecordStatment.setInt(6, basePrice);
		addRecordStatment.setInt(7, ng);
		
		try {
			addRecordStatment.executeUpdate();
		} catch (SQLException ex) {
			return "error";
		}
		
		closeConnection(main_connection);
		return addRecordStatment.getString(1);
	}
	
	public Record getRecordById(String id) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement getRecordByIdStatement = main_connection.prepareCall("{call get_record_by_id(?)}");
		getRecordByIdStatement.setString(1, id);
		ResultSet rs = getRecordByIdStatement.executeQuery();
		rs.next();
		int columnsNum = rs.getMetaData().getColumnCount();
		String[] data = new String[columnsNum];
		for (int i = 1; i <= columnsNum ; i++) {
			data[i - 1] = rs.getString(i);
		}
		
		closeConnection(main_connection);
		return Record.getInstance(Record.class, data);
	}
	
	public boolean deleteRecords(String title) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteRecordsStatement = main_connection.prepareCall("{? = call delete_records(?)}");
		deleteRecordsStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteRecordsStatement.setString(2, title);
		
		deleteRecordsStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteRecordsStatement.getBoolean(1);
	}
	
	public boolean deleteRecord(int id) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteRecordStatement = main_connection.prepareCall("{? = call delete_record(?)}");
		deleteRecordStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteRecordStatement.setInt(2, id);
		
		deleteRecordStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteRecordStatement.getBoolean(1);
	}

	public boolean deleteItem(String title) throws SQLException {
		return deleteData(title, "{? = call delete_item(?)}");
	}
	
	public boolean deleteLocation(String title) throws SQLException {
		return deleteData(title, "{? = call delete_location(?)}");
	}
	
	public boolean deleteDropType(String type) throws SQLException {
		return deleteData(type, "{? = call delete_drop_type(?)}");
	}
	
	public boolean deleteCategory(String title) throws SQLException {
		return deleteData(title, "{? = call delete_category(?)}");
	}

	public boolean deleteData(String title, String funcStatement) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteDataStatement = main_connection.prepareCall(funcStatement);
		deleteDataStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteDataStatement.setString(2, title);

		deleteDataStatement.executeUpdate();

		closeConnection(main_connection);
		return deleteDataStatement.getBoolean(1);
	}
	
	public boolean editRecord(String id, String newItem, String newLocation, String newDropType, String newCategory, int newNG) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement editItemStatement = main_connection.prepareCall("{? = call edit_record(?,?,?,?,?,?)}");
		editItemStatement.registerOutParameter(1, Types.BOOLEAN);
		editItemStatement.setString(2, id);
		editItemStatement.setString(3, newItem);
		editItemStatement.setString(4, newLocation);
		editItemStatement.setString(5, newDropType);
		editItemStatement.setString(6, newCategory);
		editItemStatement.setInt(7, newNG);
		
		editItemStatement.executeUpdate();
		
		closeConnection(main_connection);
		
		return editItemStatement.getBoolean(1);
	}

	public boolean editItem(int id, String newTitle) throws SQLException {
		return editForeignTable(id, newTitle, "{? = call edit_item(?, ?)}");
	}
	
	public boolean editLocation(int id, String newTitle) throws SQLException {
		return editForeignTable(id, newTitle, "{? = call edit_location(?,?)}");
	}
	
	public boolean editDropType(int id, String newTitle) throws SQLException {
		return editForeignTable(id, newTitle, "{? = call edit_drop_type(?,?)}");
	}
	
	public boolean editCategory(int id, String newTitle) throws SQLException {
		return editForeignTable(id, newTitle, "{? = call edit_category(?,?)}");
	}
	
	private boolean editForeignTable(int id, String newTitle, String funcStatement) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement editRecordStatement = main_connection.prepareCall(funcStatement);
		editRecordStatement.registerOutParameter(1, Types.BOOLEAN);
		editRecordStatement.setInt(2, id);
		editRecordStatement.setString(3, newTitle);
		
		editRecordStatement.executeUpdate();
		
		closeConnection(main_connection);
		
		return editRecordStatement.getBoolean(1);
	}
	
	public boolean truncateAll() throws SQLException {
		return truncateTable("{? = call truncate_all}");
	}
	
	public boolean truncateRecords() throws SQLException {
		return truncateTable("{? = call truncate_records}");
	}

	public boolean truncateItems() throws SQLException {
		return truncateTable("{? = call truncate_items}");
	}
	
	public boolean truncateLocations() throws SQLException {
		return truncateTable("{? = call truncate_locations}");
	}
	
	public boolean truncateDropTypes() throws SQLException {
		return truncateTable("{? = call truncate_drop_types}");
	}
	
	public boolean truncateCategories() throws SQLException {
		return truncateTable("{? = call truncate_categories}");
	}
	
	private boolean truncateTable(String funcStatement) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement truncateTableStatement = main_connection.prepareCall(funcStatement);
		truncateTableStatement.registerOutParameter(1, Types.BOOLEAN);
		
		truncateTableStatement.executeUpdate();
		
		closeConnection(main_connection);
		
		return truncateTableStatement.getBoolean(1);
	}
	
	public List<Record> findRecords(String item, String location, String dropType, String category) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement findRecordsStatement =  main_connection.prepareCall("{call find_records(?,?,?,?)}");
		findRecordsStatement.setString(1, item);
		findRecordsStatement.setString(2, location);
		findRecordsStatement.setString(3, dropType);
		findRecordsStatement.setString(4, category);
		
		ResultSet rs = findRecordsStatement.executeQuery();
		
		closeConnection(main_connection);
		
		return extractData(Record.class, rs);
	}
	
	private Connection openConnection(String URL) throws SQLException {
		return DriverManager.getConnection(URL, PG_USER, PG_PASS);
	}
	
	private void closeConnection(Connection connection) throws SQLException {
		if (!connection.isClosed()) {
			connection.close();
		}
	}
}
