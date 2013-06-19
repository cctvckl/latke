/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.ioc.annotated;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.inject.Inject;
import org.b3log.latke.ioc.util.Beans;
import org.b3log.latke.ioc.util.Reflections;
import org.b3log.latke.util.CollectionUtils;


/**
 * An annotated type.
 *
 * @param <T> the declaring type
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.5, Mar 30, 2010
 */
public class AnnotatedTypeImpl<T> implements AnnotatedType<T> {

    /**
     * Bean class.
     */
    private Class<T> beanClass;

    /**
     * Annotated constructors.
     */
    private Set<AnnotatedConstructor<T>> annotatedConstructors;

    /**
     * Annotated methods.
     */
    private Set<AnnotatedMethod<? super T>> annotatedMethods;

    /**
     * Annotated fields.
     */
    private Set<AnnotatedField<? super T>> annotatedFields;

    /**
     * Constructs an annotated type with the specified bean class.
     * 
     * @param beanClass the bean class
     */
    public AnnotatedTypeImpl(final Class<T> beanClass) {
        this.beanClass = beanClass;

        annotatedConstructors = new HashSet<AnnotatedConstructor<T>>();
        annotatedFields = new HashSet<AnnotatedField<? super T>>();
        annotatedMethods = new HashSet<AnnotatedMethod<? super T>>();

        initAnnotatedConstructor();
        initAnnotatedFields();
        initAnnotatedMethods();
    }

    @Override
    public Class<T> getJavaClass() {
        return beanClass;
    }

    @Override
    public Set<AnnotatedConstructor<T>> getConstructors() {
        return annotatedConstructors;
    }

    @Override
    public Set<AnnotatedMethod<? super T>> getMethods() {
        return annotatedMethods;
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return annotatedFields;
    }

    @Override
    public Type getBaseType() {
        return beanClass;
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Annotation> getAnnotations() {
        return CollectionUtils.arrayToSet(beanClass.getAnnotations());
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Builds the annotated constructor of this annotated type.
     */
    private void initAnnotatedConstructor() {
        for (final Constructor constructor : beanClass.getDeclaredConstructors()) {
            final boolean isNeedToBeInjected = constructor.isAnnotationPresent(Inject.class);

            if (isNeedToBeInjected) {
                @SuppressWarnings("unchecked")
                final AnnotatedConstructor<T> annotatedConstructor = new AnnotatedConstructorImpl<T>(constructor);

                constructor.setAccessible(true);
                annotatedConstructors.add(annotatedConstructor);
            }
        }
    }

    /**
     * Builds the annotated fields of this annotated type.
     */
    private void initAnnotatedFields() {
        // final Set<Field> inheritedFields =
        // ReflectionUtil.getInheritedFields(beanClass);
        final Set<Field> hiddenFields = Reflections.getHiddenFields(beanClass);
        final Set<Field> ownFields = Reflections.getOwnFields(beanClass);

        for (final Field field : hiddenFields) {
            final boolean isNeedToBeInjected = field.isAnnotationPresent(Inject.class);

            if (isNeedToBeInjected) {
                final AnnotatedField<T> annotatedField = new AnnotatedFieldImpl<T>(field);

                field.setAccessible(true);
                annotatedFields.add(annotatedField);
            }
        }

        // for (final Field field : inheritedFields) {
        // boolean isNeedToBeInjected =
        // field.isAnnotationPresent(Inject.class);
        // if (isNeedToBeInjected) {
        // final AnnotatedField<T> annotatedField =
        // new AnnotatedFieldImpl<T>(field);
        //
        // field.setAccessible(true);
        // annotatedFields.add(annotatedField);
        // }
        // }

        for (final Field field : ownFields) {
            final boolean isNeedToBeInjected = field.isAnnotationPresent(Inject.class);

            if (isNeedToBeInjected) {
                final AnnotatedField<T> annotatedField = new AnnotatedFieldImpl<T>(field);

                field.setAccessible(true);
                annotatedFields.add(annotatedField);
            }
        }
    }

    /**
     * Builds the annotated methods of this annotated type.
     */
    private void initAnnotatedMethods() {
        // final Set<Method> inheritedMethods =
        // ReflectionUtil.getInheritedMethods(beanClass);
        final Set<Method> overriddenMethods = Reflections.getOverriddenMethods(beanClass);
        final Set<Method> ownMethods = Reflections.getOwnMethods(beanClass);

        for (final Method method : overriddenMethods) {
            final boolean isNeedToBeInjected = method.isAnnotationPresent(Inject.class);

            if (isNeedToBeInjected) {
                final AnnotatedMethod<T> annotatedMethod = new AnnotatedMethodImpl<T>(method);

                method.setAccessible(true);
                annotatedMethods.add(annotatedMethod);
            }
        }

        // for (final Method method : inheritedMethods) {
        // boolean isNeedToBeInjected =
        // method.isAnnotationPresent(Inject.class);
        // if (isNeedToBeInjected) {
        // final AnnotatedMethod<T> annotatedMethod =
        // new AnnotatedMethodImpl<T>(method);
        //
        // method.setAccessible(true);
        // annotatedMethods.add(annotatedMethod);
        // }
        // }

        for (final Method method : ownMethods) {
            final boolean isNeedToBeInjected = method.isAnnotationPresent(Inject.class);

            if (isNeedToBeInjected) {
                final AnnotatedMethod<T> annotatedMethod = new AnnotatedMethodImpl<T>(method);

                method.setAccessible(true);
                annotatedMethods.add(annotatedMethod);
            }
        }
    }

    @Override
    public Set<Type> getTypeClosure() {
        return Beans.getBeanTypes(beanClass);
    }
}