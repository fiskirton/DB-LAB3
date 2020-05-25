package gui.custom;

import db.models.BaseModel;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.text.WordUtils;
import javafx.scene.control.TableColumn.CellEditEvent;

public class LimitedTextFieldTableCell<T extends BaseModel, E> extends TableCell<T, E> {
	
	private final TextField textField;
	private final StringConverter<E> converter;
	private final EventHandler<TableColumn.CellEditEvent<T, E>> eventHandler;
	
	public <S extends TextField> LimitedTextFieldTableCell(S textField, StringConverter<E> converter, EventHandler<TableColumn.CellEditEvent<T, E>> editEvent) {
		this.textField = textField;
		this.converter = converter;
		this.eventHandler = editEvent;
	}
	
	@Override
	public void startEdit() {
		if(editableProperty().get()){
			if (!isEmpty()) {
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.requestFocus();
			}
		}
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem() != null ? getItem().toString() : null);
		setGraphic(null);
	}
	
	@Override
	public void updateItem(E item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
					textField.selectAll();
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(null);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void commitEdit(E newValue) {
		final TableView<T> table = getTableView();
		if (table != null) {
			CellEditEvent<T, E> editEvent = new CellEditEvent<>(
					table,
					(TablePosition<T, E>) table.getEditingCell(),
					TableColumn.editCommitEvent(),
					newValue
			);
			eventHandler.handle(editEvent);
		}
		super.commitEdit(newValue);
	}
	
	private void createTextField() {
		textField.setText(getString());
		
		textField.setOnAction(evt -> {
			if(textField.getText() != null && !textField.getText().isEmpty()){
				E value = converter.fromString(WordUtils.capitalizeFully(textField.getText()
						.strip()
						.replaceAll("\\s+"," "))
				);
				commitEdit(value);
			}
		});
		
		textField.setOnKeyPressed((ke) -> {
			if (ke.getCode().equals(KeyCode.ESCAPE)) {
				cancelEdit();
			}
		});
	}
	
	private String getString() {
		return getItem() == null ? "" : String.valueOf(getItem());
	}
	
	public static <S extends TextField ,T extends BaseModel , E> Callback<TableColumn<T,E>, TableCell<T,E>> forTableColumn(S textfield,
			final StringConverter<E> converter, EventHandler<TableColumn.CellEditEvent<T,E>> editEvent) {
		return list -> new LimitedTextFieldTableCell<>(textfield, converter, editEvent);
	}
	
	public static <T extends BaseModel> Callback<TableColumn<T, String>, TableCell<T, String>> forTableColumn(EventHandler<TableColumn.CellEditEvent<T,String>> editEvent) {
		return forTableColumn(new CharacterTextField(), new DefaultStringConverter(), editEvent);
	}
}
