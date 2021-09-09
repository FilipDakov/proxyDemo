package services;

import entities.BaseEntity;

public interface EntityService<T extends BaseEntity> {
    T load(Class<T> clazz,Long id) ;
    void save(T person);
    void delete(Class<T> clazz,Long personId);
}
