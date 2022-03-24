package cn.howardliu.demo.spring.seh.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-03-24
 */
public class MethodHandlerTypePredicate implements Predicate<Class<?>> {

    private final Set<String> basePackages;

    private final List<Class<?>> assignableTypes;

    private final List<Class<? extends Annotation>> annotations;

    /**
     * Private constructor. See static factory methods.
     */
    private MethodHandlerTypePredicate(Set<String> basePackages, List<Class<?>> assignableTypes,
            List<Class<? extends Annotation>> annotations) {
        this.basePackages = Collections.unmodifiableSet(basePackages);
        this.assignableTypes = Collections.unmodifiableList(assignableTypes);
        this.annotations = Collections.unmodifiableList(annotations);
    }

    @Override
    public boolean test(Class<?> controllerType) {
        return test(controllerType, null);
    }

    public boolean test(Class<?> controllerType, Method method) {
        if (!hasSelectors()) {
            return true;
        } else if (controllerType != null) {
            for (String basePackage : this.basePackages) {
                if (controllerType.getName().startsWith(basePackage)) {
                    return true;
                }
            }
            for (Class<?> clazz : this.assignableTypes) {
                if (ClassUtils.isAssignable(clazz, controllerType)) {
                    return true;
                }
            }
            for (Class<? extends Annotation> annotationClass : this.annotations) {
                if (AnnotationUtils.findAnnotation(controllerType, annotationClass) != null) {
                    return true;
                }
                if (AnnotationUtils.findAnnotation(method, annotationClass) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasSelectors() {
        return (!this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty());
    }


    // Static factory methods

    /**
     * {@code Predicate} that applies to any handlers.
     */
    public static MethodHandlerTypePredicate forAnyHandlerType() {
        return new MethodHandlerTypePredicate(
                Collections.emptySet(), Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Match handlers declared under a base package, e.g. "org.example".
     *
     * @param packages one or more base package names
     */
    public static MethodHandlerTypePredicate forBasePackage(String... packages) {
        return new MethodHandlerTypePredicate.Builder().basePackage(packages).build();
    }

    /**
     * Type-safe alternative to {@link #forBasePackage(String...)} to specify a
     * base package through a class.
     *
     * @param packageClasses one or more base package classes
     */
    public static MethodHandlerTypePredicate forBasePackageClass(Class<?>... packageClasses) {
        return new MethodHandlerTypePredicate.Builder().basePackageClass(packageClasses).build();
    }

    /**
     * Match handlers that are assignable to a given type.
     *
     * @param types one or more handler super types
     */
    public static MethodHandlerTypePredicate forAssignableType(Class<?>... types) {
        return new MethodHandlerTypePredicate.Builder().assignableType(types).build();
    }

    /**
     * Match handlers annotated with a specific annotation.
     *
     * @param annotations one or more annotations to check for
     */
    @SafeVarargs
    public static MethodHandlerTypePredicate forAnnotation(Class<? extends Annotation>... annotations) {
        return new MethodHandlerTypePredicate.Builder().annotation(annotations).build();
    }

    /**
     * Return a builder for a {@code MethodHandlerTypePredicate}.
     */
    public static MethodHandlerTypePredicate.Builder builder() {
        return new MethodHandlerTypePredicate.Builder();
    }


    /**
     * A {@link MethodHandlerTypePredicate} builder.
     */
    public static class Builder {

        private final Set<String> basePackages = new LinkedHashSet<>();

        private final List<Class<?>> assignableTypes = new ArrayList<>();

        private final List<Class<? extends Annotation>> annotations = new ArrayList<>();

        /**
         * Match handlers declared under a base package, e.g. "org.example".
         *
         * @param packages one or more base package classes
         */
        public MethodHandlerTypePredicate.Builder basePackage(String... packages) {
            Arrays.stream(packages).filter(StringUtils::hasText).forEach(this::addBasePackage);
            return this;
        }

        /**
         * Type-safe alternative to {@link #forBasePackage(String...)} to specify a
         * base package through a class.
         *
         * @param packageClasses one or more base package names
         */
        public MethodHandlerTypePredicate.Builder basePackageClass(Class<?>... packageClasses) {
            Arrays.stream(packageClasses).forEach(clazz -> addBasePackage(ClassUtils.getPackageName(clazz)));
            return this;
        }

        private void addBasePackage(String basePackage) {
            this.basePackages.add(basePackage.endsWith(".") ? basePackage : basePackage + ".");
        }

        /**
         * Match handlers that are assignable to a given type.
         *
         * @param types one or more handler super types
         */
        public MethodHandlerTypePredicate.Builder assignableType(Class<?>... types) {
            this.assignableTypes.addAll(Arrays.asList(types));
            return this;
        }

        /**
         * Match types that are annotated with one of the given annotations.
         *
         * @param annotations one or more annotations to check for
         */
        @SuppressWarnings("unchecked")
        public final MethodHandlerTypePredicate.Builder annotation(Class<? extends Annotation>... annotations) {
            this.annotations.addAll(Arrays.asList(annotations));
            return this;
        }

        public MethodHandlerTypePredicate build() {
            return new MethodHandlerTypePredicate(this.basePackages, this.assignableTypes, this.annotations);
        }
    }

}
