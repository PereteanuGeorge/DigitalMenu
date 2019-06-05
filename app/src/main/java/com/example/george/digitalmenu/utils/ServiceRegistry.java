package com.example.george.digitalmenu.utils;

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
        Object result = services.get(object);
        if (result == null) {
            throw new RuntimeException("Cannot find service in registry for: " + object.getName());
        }

        return (T) result;
    }

    public <T> void registerService(Class<T> objClass, T object) {
        services.put(objClass, object);
    }
}