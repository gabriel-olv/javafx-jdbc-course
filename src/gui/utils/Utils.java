package gui.utils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	public static Stage currentStage(ActionEvent ae) {
		return (Stage) ((Node) ae.getSource()).getScene().getWindow();
	}
}
