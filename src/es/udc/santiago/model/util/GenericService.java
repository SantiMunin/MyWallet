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
import java.util.List;

import es.udc.santiago.model.exceptions.DuplicateEntryException;
import es.udc.santiago.model.exceptions.EntryNotFoundException;
/**
 * Generic service interface (thought to be extended by another interfaces).
 * @author Santiago Munín González
 * @param <PK> Primary key type.
 * @param <Entity> Entity type.
 */
public interface GenericService<PK extends Serializable, Entity> {
	/**
	 * Adds the entity and returns the associated PK value.
	 * @param entity
	 * @return Primary key assigned.
	 * @throws DuplicateEntryException if there is any constraint violation. 
	 */
	public PK add(Entity entity) throws DuplicateEntryException;
	
	public Entity get(PK key) throws EntryNotFoundException;
	
	public List<Entity> getAll();
	
	public void update(Entity object) throws EntryNotFoundException, DuplicateEntryException;
	
	public void delete(PK key) throws EntryNotFoundException;
	
	public boolean exists(Entity object);
}