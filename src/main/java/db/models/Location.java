package db.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Location extends BaseModel{
	
	private IntegerProperty locationId;
	private StringProperty locationTitle;
	
	protected Location(String[] data) {
		this.locationId = new SimpleIntegerProperty(Integer.parseInt(data[0]));
		this.locationTitle = new SimpleStringProperty(data[1]);
	}
	
	public int getLocationId() {
		return locationId.get();
	}
	
	public IntegerProperty locationIdProperty() {
		return locationId;
	}
	
	public void setLocationId(int locationId) {
		this.locationId.set(locationId);
	}
	
	public String getLocationTitle() {
		return locationTitle.get();
	}
	
	public StringProperty locationTitleProperty() {
		return locationTitle;
	}
	
	public void setLocationTitle(String locationTitle) {
		this.locationTitle.set(locationTitle);
	}
	
	@Override
	public String toString() {
		return locationTitle.get();
	}
}
