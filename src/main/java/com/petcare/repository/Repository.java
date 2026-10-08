package com.petcare.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining in-memory CRUD operations.
 * Demonstrates Generics (<T, ID>) and bounded wildcards (? extends T).
 *
 * @param <T>  The domain entity type
 * @param <ID> The entity identifier type
 */
public interface Repository<T, ID> {

    /**
     * Saves an entity in memory.
     *
     * @param entity the entity to persist
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param id the identifier
     * @return an Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(ID id);

    /**
     * Retrieves all entities in the repository.
     *
     * @return unmodifiable list or copy of all entities
     */
    List<T> findAll();

    /**
     * Checks if an entity exists by its unique identifier.
     *
     * @param id the identifier
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Removes an entity by identifier.
     *
     * @param id the identifier
     * @return true if removed, false otherwise
     */
    boolean deleteById(ID id);

    /**
     * Returns total number of entities stored.
     *
     * @return total count
     */
    int count();

    /**
     * Bulk saves entities using bounded wildcard generic parameter.
     * Demonstrates PECS (Producer Extends, Consumer Super).
     *
     * @param entities list of entities of type T or any subclass of T
     */
    void saveAll(List<? extends T> entities);
}
