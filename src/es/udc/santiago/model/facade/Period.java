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
