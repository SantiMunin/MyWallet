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
package es.udc.santiago.model.backend;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Class of value object Category.
 * 
 * @author Santiago Munín González
 * 
 */
@DatabaseTable(tableName = "categories")
public class CategoryVO {
	//TODO name as id?
	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField(unique = true)
	private String name;
	@ForeignCollectionField(eager = false)
	private ForeignCollection<CashFlowVO> cashFlows;

	public CategoryVO() {

	}

	public CategoryVO(long id) {
		this.id = id;
	}

	public CategoryVO(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ForeignCollection<CashFlowVO> getCashFlows() {
		return cashFlows;
	}

	public void setCashFlows(ForeignCollection<CashFlowVO> cashFlows) {
		this.cashFlows = cashFlows;
	}
}
