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

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represens the CashFlow's value object.
 * 
 * @author Santiago Munín González
 * 
 */
@DatabaseTable(tableName = "cashflows")
public class CashFlowVO {
	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField
	private String concept;
	@DatabaseField(canBeNull = false)
	private float amount;
	@DatabaseField(foreign = true, foreignAutoCreate = true, canBeNull = false, foreignAutoRefresh = true)
	private CategoryVO category;
	@DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING)
	private Date date;
	@DatabaseField(dataType = DataType.DATE_STRING)
	private Date endDate;
	@DatabaseField(canBeNull = false)
	private int period;
	@DatabaseField(canBeNull = false)
	private int movType;

	public CashFlowVO() {

	}

	public CashFlowVO(long id) {
		this.id = id;
	}

	public CashFlowVO(long id, String concept, float amount,
			CategoryVO category, Date date, Date endDate, int period,
			int movType) {
		super();
		this.id = id;
		this.concept = concept;
		this.amount = amount;
		this.category = category;
		this.date = date;
		this.endDate = endDate;
		this.period = period;
		this.movType = movType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getMovType() {
		return movType;
	}

	public void setMovType(int movType) {
		this.movType = movType;
	}

	public CategoryVO getCategory() {
		return category;
	}

	public void setCategory(CategoryVO category) {
		this.category = category;
	}
}
