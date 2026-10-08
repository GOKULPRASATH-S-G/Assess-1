package com.petcare.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * In-memory implementation of generic Repository backed by a LinkedHashMap.
 * Demonstrates Generics, Functional Interfaces, and deliberate Map usage.
 *
 * @param <T>  The domain entity type
 * @param <ID> The entity identifier type
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> storage = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        if (idExtractor == null) {
            throw new IllegalArgumentException("ID extractor function cannot be null.");
        }
        this.idExtractor = idExtractor;
    }

    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save cannot be null.");
        }
        ID id = idExtractor.apply(entity);
        if (id == null) {
            throw new IllegalArgumentException("Entity ID cannot be null.");
        }
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(ID id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public boolean deleteById(ID id) {
        if (id != null && storage.containsKey(id)) {
            storage.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public int count() {
        return storage.size();
    }

    @Override
    public void saveAll(List<? extends T> entities) {
        if (entities != null) {
            for (T entity : entities) {
                if (entity != null) {
                    save(entity);
                }
            }
        }
    }
}
