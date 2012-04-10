package es.udc.santiago.model.backend;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Class of value object CashFlow.
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
	@DatabaseField(foreign = true, foreignAutoCreate = true, canBeNull = true)
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
