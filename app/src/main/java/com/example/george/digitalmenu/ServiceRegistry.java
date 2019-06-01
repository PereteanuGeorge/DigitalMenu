package com.example.george.digitalmenu;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {

    private static ServiceRegistry instance;
    private static Map<Class, Object> services;

    private ServiceRegistry() {
        services = new HashMap<>();
    }

    public static ServiceRegistry getInstance() {
        if(instance == null) {
            instance = new ServiceRegistry();

        }

        return instance;
    }

    public <T> T getService(Class<T> object) {
        return (T) services.get(object);
    }

    public <T> void registerService(Class<T> objClass, T object) {
        services.put(objClass, object);
    }
}
