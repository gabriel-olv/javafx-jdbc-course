package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.utils.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {

	private Department obj;
	
	@FXML
	private TextField textFieldId;

	@FXML
	private TextField textFieldName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}

	public void setDepartment(Department obj) {
		this.obj = obj;
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 30);
	}
	
	public void updateFormData() {
		if (obj == null) {
			throw new IllegalStateException("Entity was null");
		}
		textFieldId.setText(String.valueOf(obj.getId()));
		textFieldName.setText(obj.getName());
	}
}
