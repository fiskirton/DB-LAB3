package db.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item extends BaseModel{
    private final IntegerProperty itemId;
    private final StringProperty itemTitle;

    public Item(String[] data) {
        this.itemId = new SimpleIntegerProperty(Integer.parseInt(data[0]));
        this.itemTitle = new SimpleStringProperty(data[1]);
    }

    public int getItemId() {
        return itemId.get();
    }

    public IntegerProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(int itemId) {
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

    @Override
    public String toString() {
        return itemTitle.get();
    }
}
