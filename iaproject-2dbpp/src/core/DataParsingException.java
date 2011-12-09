package core;

public class DataParsingException extends Exception {

	private static final long serialVersionUID = 8497859099430204187L;

	public DataParsingException() {
	}

	public DataParsingException(String message) {
		super(message);
	}

	public DataParsingException(Throwable cause) {
		super(cause);
	}

	public DataParsingException(String message, Throwable cause) {
		super(message, cause);
	}

}
