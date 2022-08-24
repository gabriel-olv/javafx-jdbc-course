package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.dao.exceptions.ValidationException;
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
	private Label labelErrorName;

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
	public void onBtCancelAction(ActionEvent ae) {
		Stage currentStage = Utils.currentStage(ae);
		currentStage.close();
	}

	@FXML
	public void onBtSaveAction(ActionEvent ae) {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		if (obj == null) {
			throw new IllegalStateException("Entity was null");
		}
		try {
			obj = getFormData();
			departmentService.SaveOrUpdate(obj);
			notifyDataChangeListeners();
			Utils.currentStage(ae).close();
		} catch (DbException e) {
			Alerts.showAlert("Error savings object", e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessage(e.getErrors());
		}
	}

	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
	}

	private Department getFormData() {
		Integer id = Utils.tryParseToInt(textFieldId.getText());
		String name = textFieldName.getText();

		ValidationException ex = new ValidationException("Error validation");
		if (name == null || name.isBlank()) {
			ex.addError("name", "Field can't be empty");
		}
		if (ex.getErrors().size() > 0) {
			throw ex;
		}
		return new Department(id, name);
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
