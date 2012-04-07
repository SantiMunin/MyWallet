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
