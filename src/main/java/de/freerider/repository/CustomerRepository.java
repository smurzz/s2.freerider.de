package de.freerider.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import de.freerider.datamodel.Customer;

@Component
public class CustomerRepository implements CrudRepository<Customer, Long> {
	
	HashMap<Customer, Long> mapCustom = new HashMap<>();

	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 *
	 * @param entity must not be {@literal null}.
	 * @return the saved entity; will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
	 */
	@Override
	public <S extends Customer> S save(S entity) {
		// TODO Auto-generated method stub
		if(!entity.equals(null) && entity.getId() > 0 && !mapCustom.values().contains(entity.getId())) {
			mapCustom.put(entity, entity.getId());
			return entity;
		}  else if(!entity.equals(null) && (entity.getId()) == -1 ) {
			long id = Collections.max(mapCustom.values()) + 1;
			entity.setId(id);
			mapCustom.put(entity, entity.getId());
			return entity;
		} else {
			throw new IllegalArgumentException("Entity is not Customer-Typ or is null");
		}
	}

	/**
	 * Saves all given entities.
	 *
	 * @param entities must not be {@literal null} nor must it contain {@literal null}.
	 * @return the saved entities; will never be {@literal null}. The returned {@literal Iterable} will have the same size
	 *         as the {@literal Iterable} passed as an argument.
	 * @throws IllegalArgumentException in case the given {@link Iterable entities} or one of its entities is
	 *           {@literal null}.
	 */
	@Override
	public <S extends Customer> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		//boolean hasNullEntity = StreamSupport.stream(entities.spliterator(), true).anyMatch(entity -> entity.equals(null));
		boolean hasNullEntity = mapCustom.values().contains(null);
		
		if(!entities.equals(null) && !hasNullEntity) {
			entities.forEach(entity -> save(entity));
			return entities;
		} else {
			throw new IllegalArgumentException("Entities container is null or contains null-element.");
		}
	}

	/**
	 * Returns whether an entity with the given id exists.
	 *
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		if(!id.equals(null)) {
			return mapCustom.containsValue(id);
		} else {
			throw new IllegalArgumentException("id is null or smaller than 0");
		}
	}

	/**
	 * Retrieves an entity by its id.
	 *
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found.
	 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
	 */
	@Override
	public Optional<Customer> findById(Long id) {
		// TODO Auto-generated method stub
		if(existsById(id)) {
			for(HashMap.Entry<Customer, Long> entity : mapCustom.entrySet()) {
				if(entity.getValue() == id) {
					return Optional.of(entity.getKey());
				} 
			}
		} 
		return Optional.empty();
	}

	/**
	 * Returns all instances of the type.
	 *
	 * @return all entities
	 */
	@Override
	public Iterable<Customer> findAll() {
		// TODO Auto-generated method stub
		return mapCustom.keySet();
	}

	/**
	 * Returns all instances of the type {@code T} with the given IDs.
	 * <p>
	 * If some or all ids are not found, no entities are returned for these IDs.
	 * <p>
	 * Note that the order of elements in the result is not guaranteed.
	 *
	 * @param ids must not be {@literal null} nor contain any {@literal null} values.
	 * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
	 *         {@literal ids}.
	 * @throws IllegalArgumentException in case the given {@link Iterable ids} or one of its items is {@literal null}.
	 */
	@Override
	public Iterable<Customer> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub

		boolean hasAllIds = mapCustom.values().containsAll(Arrays.asList(ids));
		
		if(!ids.equals(null) && hasAllIds) {
			List<Customer> customers = new ArrayList<>(); 
			for(Long id : ids) {
				if(!findById(id).equals(null)) {
					customers.add(findById(id).get());
				}
			}
			return customers;
		} else {
			throw new IllegalArgumentException("Entities container is null or contains null-element.");
		}
	}

	/**
	 * Returns the number of entities available.
	 *
	 * @return the number of entities.
	 */
	@Override
	public long count() {
		// TODO Auto-generated method stub
		return mapCustom.size();
	}

	/**
	 * Deletes the entity with the given id.
	 *
	 * @param id must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
	 */
	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		if(!findById(id).equals(null)) {
			mapCustom.remove(findById(id).get(), id);
		}
	}

	/**
	 * Deletes a given entity.
	 *
	 * @param entity must not be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	@Override
	public void delete(Customer entity) {
		// TODO Auto-generated method stub
		if(!entity.equals(null) && mapCustom.keySet().contains(entity)) {
			mapCustom.remove(entity);
		} else {
			throw new IllegalArgumentException("Entity is null or is not in container.");
		}
	}


	/**
	 * Deletes all instances of the type {@code T} with the given IDs.
	 *
	 * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
	 * @since 2.5
	 */
	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		// TODO Auto-generated method stub
		if(mapCustom.values().containsAll(Arrays.asList(ids))) {
			ids.forEach(id -> deleteById(id));
		} else {
			throw new IllegalArgumentException("Entity is null or is not in container.");
		}
	}

	/**
	 * Deletes the given entities.
	 *
	 * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
	 * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
	 */
	@Override
	public void deleteAll(Iterable<? extends Customer> entities) {
		// TODO Auto-generated method stub
		if(mapCustom.keySet().containsAll(Arrays.asList(entities))) {
			entities.forEach(customer -> delete(customer));
		} else {
			throw new IllegalArgumentException("Entity is null or is not in container.");
		}
	}

	/**
	 * Deletes all entities managed by the repository.
	 */
	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		mapCustom.clear();
	}

}
