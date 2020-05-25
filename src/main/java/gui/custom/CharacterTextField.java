package gui.custom;

import javafx.scene.control.TextField;

public class CharacterTextField extends TextField {
	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[a-zA-Z\\s]") || text.isEmpty()) {
			super.replaceText(start, end, text);
		}
	}
	
	@Override
	public void replaceSelection(String replacement) {
		super.replaceSelection(replacement);
	}
}
