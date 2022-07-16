package com.atg.core.http;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import com.atg.core.http.annotations.HttpClientCustomizer;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.service.DuplicateDefinitionException;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.impl.WebserviceGatewayManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.impl.client.HttpClientBuilder;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractWebserviceGateway<A extends Annotation, T extends WebserviceGatewayManager>
        extends GenericService {
    @Getter
    private final Map<String, T> registeredGateways;
    @Getter
    @Setter
    private String defaultPackageToScan = "com";
    @Getter
    @Setter
    private int defaultMaxConnections;
    @Getter
    @Setter
    private int defaultMaxConnectionsPerRoute;
    @Getter
    @Setter
    private int defaultConnectionTimeout;
    @Getter
    @Setter
    private int defaultSocketTimeout;

    public AbstractWebserviceGateway() {
        registeredGateways = new ConcurrentHashMap<>();
    }

    protected abstract Class<A> gatewayTypeAnnotationClass();

    protected abstract T initializeGatewayManager(A annotation);

    @Override
    public void doStartService() throws ServiceException {
        initWebserviceGateways();
    }

    protected void initWebserviceGateways() {
        Set<Class<?>> classes = getClassesAnnotatedWithGatewayTypeAnnotation();
        classes.stream().forEach(classToInstantiate -> initializeServiceForClass(classToInstantiate));
    }

    protected Set<Class<?>> getClassesAnnotatedWithGatewayTypeAnnotation() {
        Reflections reflections = new Reflections(getDefaultPackageToScan());
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(gatewayTypeAnnotationClass());
        return classes;
    }

    public abstract Type gatewayType();

    private void initializeServiceForClass(Class<?> annotatedClass) {
        T conf = initializeGatewayManager(annotatedClass.getAnnotation(gatewayTypeAnnotationClass()));
        if (registeredGateways.containsKey(conf.getGatewayName())) {
            throw new DuplicateDefinitionException(
                    "Duplicate Gateway name defined for the name " + conf.getGatewayName());
        }
        Set<Service> services = Arrays.stream(annotatedClass.getMethods())
                .filter(method -> method.isAnnotationPresent(Service.class))
                .map(method -> method.getAnnotation(Service.class))
                .filter(service -> service.type().equals(gatewayType()))
                .collect(Collectors.toSet());
        registerHttpClientCustomizer(annotatedClass, conf);
        conf.registerServices(services);
        registeredGateways.put(conf.getGatewayName(), conf);
    }

    private void registerHttpClientCustomizer(Class<?> annotatedClass, T conf) {
        Optional<Consumer<HttpClientBuilder>> consumer = Arrays.stream(annotatedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(HttpClientCustomizer.class))
                .findFirst()
                .filter(field -> field.getGenericType() instanceof ParameterizedType &&
                        Modifier.isStatic(field.getModifiers()))
                .filter(field -> ((ParameterizedType) field.getGenericType()).getRawType() == Consumer.class &&
                        ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0] ==
                                HttpClientBuilder.class)
                .map(this::getFieldValue);
        if (consumer.isPresent()) {
            conf.addHttpClientCustomizer(consumer.get());
        }
    }

    private Consumer<HttpClientBuilder> getFieldValue(Field field) {
        try {
            field.setAccessible(true);
            return (Consumer<HttpClientBuilder>) field.get(null);
        } catch (IllegalAccessException e) {
        }
        return null;
    }
}
