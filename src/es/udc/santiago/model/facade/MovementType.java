package es.udc.santiago.model.facade;

/**
 * Represents different types of movements.
 * 
 * @author Santiago Munín González
 * 
 */
public enum MovementType {
	SPEND(0), INCOME(1);

	private MovementType(int value) {
		this.value = value;
	}

	/**
	 * A little trick to help DB use.
	 */
	private int value;

	/**
	 * 
	 * @return associated value.
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * 
	 * @param code
	 * @return The MovementType object mapped to the given code, or <b>null</b>
	 *         if doesn't exists.
	 */
	public static MovementType getFromCode(int code) {
		switch (code) {
		case 0:
			return MovementType.INCOME;
		case 1:
			return MovementType.SPEND;
		default:
			return null;
		}
	}
}
