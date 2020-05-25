package db.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Category extends BaseModel {
	
	private final IntegerProperty categoryId;
	private final StringProperty categoryTitle;
	
	protected Category (String[] data) {
		this.categoryId = new SimpleIntegerProperty(Integer.parseInt(data[0]));
		this.categoryTitle = new SimpleStringProperty(data[1]);
	}
	
	public int getCategoryId() {
		return categoryId.get();
	}
	
	public IntegerProperty categoryIdProperty() {
		return categoryId;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId.set(categoryId);
	}
	
	public String getCategoryTitle() {
		return categoryTitle.get();
	}
	
	public StringProperty categoryTitleProperty() {
		return categoryTitle;
	}
	
	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle.set(categoryTitle);
	}
	
	@Override
	public String toString() {
		return categoryTitle.get();
	}
}
