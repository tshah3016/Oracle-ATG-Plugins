package com.atg.core.http.annotations.util;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationUtils {

    public static <T extends Annotation, S extends String> Collection<S> getDuplicateAnnotationsByCriteria(
            Collection<T> services, Function<T, S> annotationCriteria) {
        return services.stream()
                .map(service -> annotationCriteria.apply(service))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
