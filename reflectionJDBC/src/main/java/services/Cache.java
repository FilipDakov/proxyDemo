package services;

import entities.BaseEntity;
import entities.Person;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static Map<Long, Person> cachedEntities= new HashMap<>();
    private static Map<Long, Integer> currentVersions = new HashMap<>();

    public static void putInCache(Person person){
        if(!cachedEntities.containsKey(person.getId())){
            cachedEntities.put(person.getId(),person);
            currentVersions.put(person.getId(), person.getVersion());
        }
    }

    public static void removeFromCache(Long id){
        cachedEntities.remove(id);
        currentVersions.remove(id);
    }

    public static Person getFromCache(Long id){
        if(cachedEntities.containsKey(id)){
            return cachedEntities.get(id);
        }
        return null;
    }

    public static Integer getVersion(Long id){
        if(currentVersions.containsKey(id)){
            return currentVersions.get(id);
        }
        return null;
    }

    public static void updateVersion(Long id,Integer newVersion){
        if(currentVersions.containsKey(id)){
            currentVersions.replace(id,newVersion);
        }
    }

}
