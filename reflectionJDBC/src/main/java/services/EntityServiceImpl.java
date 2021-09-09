package services;

import entities.BaseEntity;

import java.util.logging.Logger;

public class EntityServiceImpl<T extends BaseEntity> implements EntityService<T> {

    private final Logger logger = Logger.getLogger(EntityServiceImpl.class.getName());

    @Override
    public T load(Class<T> clazz, Long id) {
        logger.info("v metoda  LOAD sme");
        return DbService.load(clazz,id);
    }

    @Override
    public void save(T person) {
        logger.info("v metoda  SAVE sme");
        DbService.save(person);
    }

    @Override
    public void delete(Class<T> clazz,Long personId) {
        logger.info("v metoda  DELETE sme");
        DbService.delete(clazz,personId);
    }
}
