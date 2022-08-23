package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class SellerFormController implements Initializable {

	@FXML
	private TextField textFieldId;

	@FXML
	private TextField textFieldName;

	@FXML
	private TextField textFieldEmail;

	@FXML
	private DatePicker datePickerBirthDate;

	@FXML
	private TextField textFieldBaseSalary;

	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("SaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("CancelAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
}
