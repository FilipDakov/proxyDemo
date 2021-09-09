package interceptors;

import entities.BaseEntity;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import services.Cache;
import services.EntityServiceImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OptimisticInterceptor<T extends BaseEntity> implements MethodInterceptor {

    public static Map<Long, Integer> versions = new HashMap<>();
    private final Logger logger = Logger.getLogger(OptimisticInterceptor.class.getName());
    private EntityServiceImpl<T> entityService;

    public OptimisticInterceptor(EntityServiceImpl<T> entityService) {
        this.entityService = entityService;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if ("save".equals(method.getName())) {
            T entity= (T) objects[0];
            int version = entity.getVersion();
            Long id = entity.getId();
            if (!Cache.getVersion(id).equals(version)) {
                throw new RuntimeException("versiite ne suvpadat");
            }
            Object res = methodProxy.invokeSuper(entityService, objects);
            entity.setVersion(version+1);
            Cache.updateVersion(id,version+1);
            logger.info("version upgraded, new version is " + (version+1));
            return res;
        }
        return methodProxy.invokeSuper(entityService, objects);
    }
}
