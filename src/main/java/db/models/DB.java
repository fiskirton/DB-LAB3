package db.models;

import extra.SQLFuncs;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public enum DB {
	INSTANCE;
	
	private static final String HOST_DB_URL = "jdbc:postgresql://localhost:5432/";
	private static final String MAIN_DB_URL = "jdbc:postgresql://localhost:5432/ds1_armory";
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
		
		CallableStatement createDBStatement = host_connection.prepareCall("{? = call create_db}");
		createDBStatement.registerOutParameter(1, Types.BOOLEAN);
		createDBStatement.executeUpdate();
		
		boolean response = createDBStatement.getBoolean(1);
		
		closeConnection(host_connection);
		
		return response;
	}
	
	public boolean dropDB() throws SQLException {
		host_connection = openConnection(HOST_DB_URL);
		
		CallableStatement dropDBStatement = host_connection.prepareCall("{? = call drop_db}");
		dropDBStatement.registerOutParameter(1, Types.BOOLEAN);
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
		CallableStatement getItemsStatement = main_connection.prepareCall(funcStatement);
		ResultSet rs = getItemsStatement.executeQuery();
		
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
	
	public boolean addItem(int id, String title, String location, String dropType, String category, int basePrice, int ng) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement addItemStatment = main_connection.prepareCall("{? = call add_item(?,?,?,?,?,?,?)}");
		
		addItemStatment.registerOutParameter(1, Types.BOOLEAN);
		addItemStatment.setInt(2, id);
		addItemStatment.setString(3, title);
		addItemStatment.setString(4, location);
		addItemStatment.setString(5, dropType);
		addItemStatment.setString(6, category);
		addItemStatment.setInt(7, basePrice);
		addItemStatment.setInt(8, ng);
		
		try {
			addItemStatment.executeUpdate();
		} catch (SQLException ex) {
			return false;
		}
		
		closeConnection(main_connection);
		return addItemStatment.getBoolean(1);
	}
	
	public Item getItemById(int id) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement getItemByIdStatement = main_connection.prepareCall("{call get_item_by_id(?)}");
		getItemByIdStatement.setInt(1, id);
		ResultSet rs = getItemByIdStatement.executeQuery();
		rs.next();
		int columnsNum = rs.getMetaData().getColumnCount();
		String[] data = new String[columnsNum];
		for (int i = 1; i <= columnsNum ; i++) {
			data[i - 1] = rs.getString(i);
		}
		
		closeConnection(main_connection);
		return Item.getInstance(Item.class, data);
	}
	
	public boolean deleteItems(String title) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteItemsStatement = main_connection.prepareCall("{? = call delete_items(?)}");
		deleteItemsStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteItemsStatement.setString(2, title);
		
		deleteItemsStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteItemsStatement.getBoolean(1);
	}
	
	public boolean deleteItem(int id) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteItemStatement = main_connection.prepareCall("{? = call delete_item(?)}");
		deleteItemStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteItemStatement.setInt(2, id);
		
		deleteItemStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteItemStatement.getBoolean(1);
	}
	
	public boolean deleteLocation(String title) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteLocationStatement = main_connection.prepareCall("{? = call delete_location(?)}");
		deleteLocationStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteLocationStatement.setString(2, title);
		
		deleteLocationStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteLocationStatement.getBoolean(1);
	}
	
	public boolean deleteDropType(String type) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteDropTypeStatement = main_connection.prepareCall("{? = call delete_drop_type(?)}");
		deleteDropTypeStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteDropTypeStatement.setString(2, type);
		
		deleteDropTypeStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteDropTypeStatement.getBoolean(1);
	}
	
	public boolean deleteCategory(String title) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement deleteCategoryStatement = main_connection.prepareCall("{? = call delete_category(?)}");
		deleteCategoryStatement.registerOutParameter(1, Types.BOOLEAN);
		deleteCategoryStatement.setString(2, title);
		
		deleteCategoryStatement.executeUpdate();
		
		closeConnection(main_connection);
		return deleteCategoryStatement.getBoolean(1);
	}
	
	public boolean editItem(String id, String newTitle, String newLocation, String newDropType, String newCategory, int newNG) throws SQLException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement editItemStatement = main_connection.prepareCall("{? = call edit_item(?,?,?,?,?,?)}");
		editItemStatement.registerOutParameter(1, Types.BOOLEAN);
		editItemStatement.setString(2, id);
		editItemStatement.setString(3, newTitle);
		editItemStatement.setString(4, newLocation);
		editItemStatement.setString(5, newDropType);
		editItemStatement.setString(6, newCategory);
		editItemStatement.setInt(7, newNG);
		
		editItemStatement.executeUpdate();
		
		closeConnection(main_connection);
		
		return editItemStatement.getBoolean(1);
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
	
	public List<Item> findItems(String itemTitle, String location, String dropType, String category) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		main_connection = openConnection(MAIN_DB_URL);
		CallableStatement findItemsStatement =  main_connection.prepareCall("{call find_items(?,?,?,?)}");
		findItemsStatement.setString(1, itemTitle);
		findItemsStatement.setString(2, location);
		findItemsStatement.setString(3, dropType);
		findItemsStatement.setString(4, category);
		
		ResultSet rs = findItemsStatement.executeQuery();
		
		closeConnection(main_connection);
		
		return extractData(Item.class, rs);
	}
	
	private Connection openConnection(String URL) throws SQLException {
		return DriverManager.getConnection(URL);
	}
	
	private void closeConnection(Connection connection) throws SQLException {
		if (!connection.isClosed()) {
			connection.close();
		}
	}
}
