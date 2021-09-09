package interceptors;

import entities.BaseEntity;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import services.EntityServiceImpl;


import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class SynchronizationInterceptor<T extends BaseEntity> implements MethodInterceptor {
    private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.writeLock();
    private final Logger logger = Logger.getLogger(SynchronizationInterceptor.class.getName());
    private EntityServiceImpl<T> entityService;

    public SynchronizationInterceptor(EntityServiceImpl<T> entityService) {
        this.entityService = entityService;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object o1;
        if ("save".equals(method.getName())) {
            writeLock.lock();
            try {
                logger.info("Write-locking in progress");
                o1 = methodProxy.invokeSuper(entityService, objects);
            } finally {
                writeLock.unlock();
                logger.info("Write-locking finished");
            }
        } else {
            readLock.lock();
            try {
                logger.info("Read-locking in progress");
                o1 = methodProxy.invokeSuper(entityService, objects);
            } finally {
                readLock.unlock();
                logger.info("Read-locking finished");

            }
        }
        return o1;
    }
}
