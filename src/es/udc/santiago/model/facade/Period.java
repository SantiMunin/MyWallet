package es.udc.santiago.model.facade;

/**
 * A simple enum which represents different types o cash movements.
 * 
 * @author Santiago Munín González
 * 
 */
public enum Period {
	ONCE(0), MONTHLY(1), YEARLY(2);

	private int code;
	/**
	 * It builds a Period with a code.
	 * @param code
	 */
	private Period(int code) {
		this.code = code;
	}
	/**
	 * 
	 * @return the code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 
	 * @param code
	 * @return The Period object mapped to the given code, or <b>null</b> if doesn't exists.
	 */
	public static Period getFromCode(int code) {
		switch (code) {
		case 0:
			return Period.ONCE;
		case 1:
			return Period.MONTHLY;
		case 2:
			return Period.YEARLY;
		default:
			return null;
		}
	}
}
