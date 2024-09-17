package org.tuna.server.http.v2.annotation;

import java.lang.reflect.Method;

public class AnnotationProcessor {
    private final Object controllerClass;
    public AnnotationProcessor(Object controllerClass) {
        this.controllerClass = controllerClass;
    }

    public boolean checkClassIsAnnotated() {
        return controllerClass.getClass().isAnnotationPresent(Controller.class);
    }

    public Method findMatchingFunction(String requestMethod, String path) {
        Method[] methods = controllerClass.getClass().getDeclaredMethods();
        for (Method method : methods){
            Route methodAnnotation = method.getAnnotation(Route.class);
            if (methodAnnotation.method().equals(requestMethod) && methodAnnotation.path().equals(path)){
                return method;
            }
        }
        return null;
    }
}
