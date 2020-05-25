package gui.custom;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class RangeTextField extends TextField {
	
	private int rangeMin;
	private int rangeMax;
	
	public RangeTextField() {
		super();
		setRangeMin(1);
		setRangeMax(9);
		this.setTextFormatter(new TextFormatter<>(this::filter));
	}
	
	public int getRangeMin() {
		return rangeMin;
	}
	
	public void setRangeMin(int rangeMin) {
		this.rangeMin = rangeMin;
	}
	
	public int getRangeMax() {
		return rangeMax;
	}
	
	public void setRangeMax(int rangeMax) {
		this.rangeMax = rangeMax;
	}
	
	private TextFormatter.Change filter(TextFormatter.Change change) {
		if (!change.getControlNewText().matches(("([1-5])"))) {
			change.setText("");
		}
		return change;
	}
}
