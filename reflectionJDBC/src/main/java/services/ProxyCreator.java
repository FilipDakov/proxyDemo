package services;

import entities.BaseEntity;
import entities.Person;
import interceptors.CacheInterceptor;
import interceptors.OptimisticInterceptor;
import interceptors.SynchronizationInterceptor;
import net.sf.cglib.proxy.Enhancer;

public class ProxyCreator {



    public static EntityServiceImpl<Person> createSynchronizationProxy(EntityServiceImpl<Person> entity){

       return (EntityServiceImpl) Enhancer.create(EntityServiceImpl.class,new SynchronizationInterceptor(entity));
    }


    public static EntityServiceImpl<Person> createOptimisticProxy(EntityServiceImpl<Person> entity){

       return (EntityServiceImpl) Enhancer.create(EntityServiceImpl.class,new OptimisticInterceptor(entity));
    }

    public  static   EntityServiceImpl<Person> createCacheProxy(EntityServiceImpl<Person> entity){
        return (EntityServiceImpl) Enhancer.create(EntityServiceImpl.class, new CacheInterceptor<>(entity));
    }


}
