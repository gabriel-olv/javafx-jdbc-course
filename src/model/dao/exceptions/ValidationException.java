package model.dao.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	Map<String, String> errors = new HashMap<>();

	public ValidationException(String message) {
		super(message);
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
	
	public void addError(String fieldName, String message) {
		errors.put(fieldName, message);
	}
}
