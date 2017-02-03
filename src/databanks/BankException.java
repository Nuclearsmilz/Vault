package databanks;

public class BankException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BankException(String message, Throwable cause) {
		super(message, cause);
	}

	public BankException(String message) {
		super(message);
	}
}