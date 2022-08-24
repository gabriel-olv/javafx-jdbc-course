package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.exceptions.DbException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.dao.exceptions.ValidationException;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller obj;
	private SellerService sellerService;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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
	private ComboBox<Department> comboBoxDepartments;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	public void setSeller(Seller obj) {
		this.obj = obj;
	}

	@FXML
	public void onBtSaveAction(ActionEvent ae) {
		if (departmentService == null || sellerService == null) {
			throw new IllegalStateException("Service was null");
		}
		if (obj == null) {
			throw new IllegalStateException("Entity was null");
		}
		try {
			obj = getFormData();
			sellerService.SaveOrUpdate(obj);
			notifyDataChangeListener();
			Utils.currentStage(ae).close();
		} catch (DbException e) {
			Alerts.showAlert("Error savign object", e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessage(e.getErrors());
		}
	}

	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		labelErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
		labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
	}

	private Seller getFormData() {
		Integer id = Utils.tryParseToInt(textFieldId.getText());
		String name = textFieldName.getText();
		String email = textFieldEmail.getText();
		Date birthDate = Utils.tryParseToDate(datePickerBirthDate.getEditor().getText(), datePickerBirthDate.getPromptText());
		Double baseSalary = Utils.tryParseToDouble(textFieldBaseSalary.getText());
		Department department = comboBoxDepartments.getValue();

		ValidationException ex = new ValidationException("Error validation");
		String msg = "Field can't be empty";
		if (name == null || name.isBlank()) {
			ex.addError("name", msg);
		}
		if (email == null || email.isBlank()) {
			ex.addError("email", msg);
		}
		if (birthDate == null) {
			ex.addError("birthDate", msg);
		}
		if (baseSalary == null) {
			ex.addError("baseSalary", msg);
		}
		if (ex.getErrors().size() > 0) {
			throw ex;
		}

		return new Seller(id, name, email, birthDate, baseSalary, department);
	}

	@FXML
	public void onBtCancelAction(ActionEvent ae) {
		Stage stage = Utils.currentStage(ae);
		stage.close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 70);
		Constraints.setTextFieldMaxLength(textFieldEmail, 100);
		Constraints.setTextFieldDouble(textFieldBaseSalary);
		Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}
	}

	public void updateFormData() {
		if (obj == null) {
			throw new IllegalStateException("Entity was null");
		}
		textFieldId.setText(String.valueOf(obj.getId()));
		textFieldName.setText(obj.getName());
		textFieldEmail.setText(obj.getEmail());
		Locale.setDefault(Locale.US);
		textFieldBaseSalary.setText(String.format("%.2f", obj.getBaseSalary()));
		if (obj.getBirthDate() != null) {
			datePickerBirthDate.setValue(LocalDate.ofInstant(obj.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (obj.getDepartment() == null) {
			comboBoxDepartments.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartments.setValue(obj.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartments.setItems(obsList);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartments.setCellFactory(factory);
		comboBoxDepartments.setButtonCell(factory.call(null));
	}
}
