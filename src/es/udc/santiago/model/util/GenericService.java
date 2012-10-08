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