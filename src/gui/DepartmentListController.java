package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.exceptions.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService departmentService;

	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEdit;

	@FXML
	private TableColumn<Department, Department> tableColumnRemove;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;

	@FXML
	public void onBtNewAction(ActionEvent ae) {
		Department department = new Department();
		createDialogForm(department, "/gui/DepartmentForm.fxml", Utils.currentStage(ae));
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNotes();
	}

	@Override
	public void onDataChange() {
		updateTableView();
	}

	private void initializeNotes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
		tableViewDepartment.prefWidthProperty().bind(stage.widthProperty());
	}

	public void updateTableView() {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Department obj, String path, Stage parentStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
			Pane pane = fxmlLoader.load();
			pane.setPrefSize(300, 200);

			DepartmentFormController controller = fxmlLoader.getController();
			controller.setDepartment(obj);
			controller.updateFormData();
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Edit");

			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				setAlignment(Pos.CENTER);
				button.setOnAction(ae -> createDialogForm(item, "/gui/DepartmentForm.fxml", Utils.currentStage(ae)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Remove");

			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				setAlignment(Pos.CENTER);
				button.setOnAction(ae -> removeEntity(item));
			}
		});
	}

	private void removeEntity(Department item) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get().equals(ButtonType.OK)) {
			if (departmentService == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				departmentService.remove(item);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
