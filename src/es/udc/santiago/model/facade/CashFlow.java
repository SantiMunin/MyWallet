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

import java.util.Date;

import es.udc.santiago.model.util.ModelUtilities;

/**
 * Reprensent any cash movement.
 * 
 * @author Santiago Munín González
 * 
 */
public class CashFlow {
	private long id;
	private String concept;
	private float amount;
	private Category category;
	private Date date;
	private Date endDate;
	private Period period;
	private MovementType movType;

	public CashFlow() {

	}

	public CashFlow(long id) {
		this.id = id;
	}

	public CashFlow(long id, String concept, float amount, Category category,
			Date date, Date endDate, Period period, MovementType movType) {
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

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
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

	public MovementType getMovType() {
		return movType;
	}

	public void setMovType(MovementType movType) {
		this.movType = movType;
	}

	public boolean equals(Object o) {
		if (o instanceof CashFlow) {
			CashFlow c1 = (CashFlow) o;
			if (c1.getAmount() != this.amount) {
				return false;
			}
			if (c1.getCategory().getId() != this.category.getId()) {
				return false;
			}
			if (c1.getConcept() != this.concept) {
				return false;
			}
			if (!ModelUtilities.compareDates(c1.getDate(), this.date)) {
				return false;
			}
			if (!ModelUtilities.compareDates(c1.getEndDate(), this.endDate)) {
				return false;
			}
			if (c1.getMovType() != this.movType) {
				return false;
			}
			if (c1.getPeriod() != this.period) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	protected Object clone() {
		CashFlow c = new CashFlow();
		c.setAmount(this.amount);
		c.setCategory(this.category);
		c.setConcept(this.concept);
		c.setDate((Date) this.date.clone());
		if (this.endDate != null) {
			c.setEndDate((Date) this.endDate);
		} else {
			c.setEndDate(null);
		}
		c.setId(this.id);
		c.setMovType(this.movType);
		c.setPeriod(period);
		return c;
	}
}
