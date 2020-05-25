package db.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DropType extends BaseModel {
	private final IntegerProperty dropTypeId;
	private final StringProperty dropType;
	
	protected DropType(String[] data) {
		this.dropTypeId = new SimpleIntegerProperty(Integer.parseInt(data[0]));
		this.dropType = new SimpleStringProperty(data[1]);
	}
	
	public int getDropTypeId() {
		return dropTypeId.get();
	}
	
	public IntegerProperty dropTypeIdProperty() {
		return dropTypeId;
	}
	
	public void setDropTypeId(int dropTypeId) {
		this.dropTypeId.set(dropTypeId);
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
	
	@Override
	public String toString() {
		return dropType.get();
	}
}
