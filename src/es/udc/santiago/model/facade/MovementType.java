/*
MyWallet is an android application which helps users to manager their personal accounts.
Copyright (C) 2012 Santiago Munin

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.   
*/
package es.udc.santiago.model.facade;

/**
 * Represents different types of movements.
 * 
 * @author Santiago Munín González
 * 
 */
public enum MovementType {
	EXPENSE(1), INCOME(0);

	private MovementType(int value) {
		this.value = value;
	}

	/**
	 * Numeric value.
	 */
	private int value;

	/**
	 * 
	 * @return associated numeric value.
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
			return MovementType.EXPENSE;
		default:
			return null;
		}
	}
}
