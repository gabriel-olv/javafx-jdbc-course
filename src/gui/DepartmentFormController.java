package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.exceptions.DbException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department obj;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField textFieldId;

	@FXML
	private TextField textFieldName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;


	public void setDepartment(Department obj) {
		this.obj = obj;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent ae) {
		obj = getFormData();
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		if (obj == null) {
			Alerts.showAlert("Empty data", "Can't save empty department", AlertType.INFORMATION);
		} else {
			try {
				departmentService.SaveOrUpdate(obj);
				Alerts.showAlert("Success", "Department saved successfully", AlertType.INFORMATION);
				notifyDataChangeListeners();
				Utils.currentStage(ae).close();
			} catch (DbException e) {
				Alerts.showAlert("Error savign object", e.getMessage(), AlertType.ERROR);
			}
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
	}

	private Department getFormData() {
		Integer id = Utils.tryParseToInt(textFieldId.getText());
		String name = textFieldName.getText();
		if (id == null && name == null) {
			return null;
		}
		return new Department(id, name);
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
