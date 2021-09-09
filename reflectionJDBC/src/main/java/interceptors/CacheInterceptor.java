package interceptors;

import entities.BaseEntity;
import entities.Person;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import services.Cache;
import services.EntityServiceImpl;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class CacheInterceptor<T extends BaseEntity> implements MethodInterceptor {

    private final Logger logger = Logger.getLogger(CacheInterceptor.class.getName());
    private EntityServiceImpl<T> entityService;

    public CacheInterceptor(EntityServiceImpl<T> entityService) {
        this.entityService = entityService;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        switch (method.getName()) {
            case "save":
                Person person = (Person)objects[0];
                //update cache
                Cache.removeFromCache(person.getId());
                Cache.putInCache(person);
                return method.invoke(o,objects);
            case "delete":
                Cache.removeFromCache((Long) objects[1]);
                logger.info("removing object from cache");
                return method.invoke(entityService, objects);
            case "load":
                Person result = Cache.getFromCache((Long) objects[1]);
                if (result != null) {
                    return result;
                }
                result = (Person) method.invoke(o, objects);
                Cache.putInCache(result);
                return result;
            default:
        }
        return null;
    }
}
