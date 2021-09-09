import entities.Person;
import interceptors.OptimisticInterceptor;
import net.sf.cglib.proxy.Enhancer;
import services.EntityServiceImpl;
import services.ProxyCreator;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        EntityServiceImpl<Person> entityService =  new EntityServiceImpl<>();
        entityService =ProxyCreator.createCacheProxy(entityService);
        entityService=ProxyCreator.createOptimisticProxy(entityService);
        entityService=ProxyCreator.createSynchronizationProxy(entityService);
        entityService.delete(Person.class,8L);
        Person load = entityService.load(Person.class, 3L);
        System.out.println();


    }
}

