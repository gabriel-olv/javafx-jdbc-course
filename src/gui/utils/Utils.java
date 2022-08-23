package gui.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {

	public static Stage currentStage(ActionEvent ae) {
		return (Stage) ((Node) ae.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String pattern) {
		tableColumn.setCellFactory(cell -> new TableCell<T, Date>() {
			private final SimpleDateFormat df = new SimpleDateFormat(pattern);
			@Override
			protected void updateItem(Date item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setText(null);
					return;
				}
				setText(df.format(item));
			}
		});
	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
		tableColumn.setCellFactory(cell -> new TableCell<T, Double>() {
			@Override
			protected void updateItem(Double value, boolean empty) {
				if (empty) {
					setText(null);
					return;
				}
				setText(String.format("%." + decimalPlaces + "f", value));
			};
		});
	}
	
	public static void formatDatePicker(DatePicker datePicker, String pattern) {
		datePicker.setPromptText(pattern); 
		datePicker.setConverter(new StringConverter<LocalDate>() {
			private DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
			
			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return df.format(date);
				} else {
					return "";
				}
			}
			
			@Override
			public LocalDate fromString(String dateOnPattern) {
				if (dateOnPattern != null && !dateOnPattern.isBlank()) {
					return LocalDate.parse(pattern, df);
				} else {
					return null;
				}
			}
		});
	}
}
