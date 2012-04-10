package es.udc.santiago.model.util;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
/**
 * Generic service interface (thought to be extended by another interfaces).
 * @author Santiago Munín González
 *
 * @param <PK>
 * @param <Entity>
 */
public interface GenericService<PK extends Serializable, Entity> {
	/**
	 * Adds the entity.
	 * @param object
	 * @return Primary key asigned.
	 * @throws SQLException 
	 */
	public PK add(Entity object) throws DuplicateEntryException;
	
	/**
	 * Fetches the entity
	 * @param key
	 * @return Entity or null if doesn't exist.
	 * @throws SQLException 
	 */
	public Entity get(PK key) throws EntryNotFoundException;
	
	/**
	 * Fetches all entities. 
	 * @param key
	 * @return a List filled of entities.
	 * @throws SQLException 
	 */
	public List<Entity> getAll();
	
	/**
	 * Updates an entity.
	 * @param object
	 * @throws DuplicateEntryException 
	 * @throws SQLException 
	 */
	public void update(Entity object) throws EntryNotFoundException, DuplicateEntryException;
	
	/**
	 * Deletes an entity.
	 * @param key Primary key
	 * @throws SQLException 
	 */
	public void delete(PK key) throws EntryNotFoundException;
	
	/**
	 * Checks if an entity exists. Each service uses different criteria (id, unique fields...).
	 * @param object
	 * @return
	 */
	public boolean exists(Entity object);
}