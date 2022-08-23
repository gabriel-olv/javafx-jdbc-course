package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.dao.exceptions.ValidationException;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller obj;
	private SellerService sellerService;
	private DepartmentService departmentService;

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

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	public void setSeller(Seller obj) {
		this.obj = obj;
	}

	@FXML
	public void onBtSaveAction() {
		if (departmentService == null || sellerService == null) {
			throw new IllegalStateException("Service was null");
		}
		if (obj == null) {
			throw new IllegalStateException("Entity was null");
		}
		try {
			obj = getFormData();
			sellerService.SaveOrUpdate(obj);
		} catch (DbException e) {
			Alerts.showAlert("Error savign object", e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessage(e.getErrors());
		}
	}

	private void setErrorMessage(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		if (fields.contains("email")) {
			labelErrorEmail.setText(errors.get("email"));
		}
		if (fields.contains("birthDate")) {
			labelErrorBirthDate.setText(errors.get("birthDate"));
		}
		if (fields.contains("baseSalary")) {
			labelErrorBaseSalary.setText(errors.get("baseSalary"));
		}
	}

	private Seller getFormData() {
		Integer id = Utils.tryParseToInt(textFieldId.getText());
		String name = textFieldName.getText();

		ValidationException ex = new ValidationException("Error validation");
		String msg = "Field can't be empty";
		if (name == null || name.isBlank()) {
			ex.addError("name", msg);
		}
		if (ex.getErrors().size() > 0) {
			throw ex;
		}

		return new Seller(id, name, null, null, null, null);
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
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		listener.onDataChange();
	}

	public void updateFormData() {
		textFieldId.setText(String.valueOf(obj.getId()));
		textFieldName.setText(obj.getName());
		textFieldEmail.setText(obj.getEmail());
		Locale.setDefault(Locale.US);
		textFieldBaseSalary.setText(String.format("%.2f", obj.getBaseSalary()));
		if (obj.getBirthDate() != null) {			
			datePickerBirthDate.setValue(LocalDate.ofInstant(obj.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
	}
}
