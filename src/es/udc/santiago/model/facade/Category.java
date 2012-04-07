package es.udc.santiago.model.facade;


/**
 * This class defines a value object of "Category".
 * 
 * @author Santiago Munín González
 * 
 */
public class Category implements Comparable<Category> {

	private long id;
	private String name;

	public Category() {

	}

	public Category(long id) {
		this.id = id;
	}

	public Category(long id, String name) {
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
	
	@Override
	public int compareTo(Category arg0) {
		String name2 = arg0.getName();
		return (this.name.compareTo(name2));
	}
	
}
