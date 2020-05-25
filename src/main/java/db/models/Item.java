package db.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item extends BaseModel {
	
	private final StringProperty itemId;
	private final StringProperty itemTitle;
	private final StringProperty location;
	private final StringProperty dropType;
	private final StringProperty category;
	private final IntegerProperty ng;
	private final IntegerProperty price;
	
	protected Item(String[] data) {
		this.itemId = new SimpleStringProperty(data[0]);
		this.itemTitle = new SimpleStringProperty(data[1]);
		this.location = new SimpleStringProperty(data[2]);
		this.dropType = new SimpleStringProperty(data[3]);
		this.category = new SimpleStringProperty(data[4]);
		this.ng = new SimpleIntegerProperty(Integer.parseInt(data[5]));
		this.price = new SimpleIntegerProperty(Integer.parseInt(data[6]));
	}
	
	public String getItemId() {
		return itemId.get();
	}
	
	public StringProperty itemIdProperty() {
		return itemId;
	}
	
	public void setItemId(String itemId) {
		this.itemId.set(itemId);
	}
	
	public String getItemTitle() {
		return itemTitle.get();
	}
	
	public StringProperty itemTitleProperty() {
		return itemTitle;
	}
	
	public void setItemTitle(String itemTitle) {
		this.itemTitle.set(itemTitle);
	}
	
	public String getLocation() {
		return location.get();
	}
	
	public StringProperty locationProperty() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location.set(location);
	}
	
	public String getDropType() {
		return dropType.get();
	}
	
	public StringProperty dropTypeProperty() {
		return dropType;
	}
	
	public void setDropType(String dropType) {
		this.dropType.set(dropType);
	}
	
	public String getCategory() {
		return category.get();
	}
	
	public StringProperty categoryProperty() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category.set(category);
	}
	
	public int getNg() {
		return ng.get();
	}
	
	public IntegerProperty ngProperty() {
		return ng;
	}
	
	public void setNg(int ng) {
		this.ng.set(ng);
	}
	
	public int getPrice() {
		return price.get();
	}
	
	public IntegerProperty priceProperty() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price.set(price);
	}
	
	@Override
	public String toString() {
		return itemTitle.get();
	}
}
