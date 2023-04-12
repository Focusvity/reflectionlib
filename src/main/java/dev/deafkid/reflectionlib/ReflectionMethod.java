package dev.deafkid.reflectionlib;

/*
 * Copyright (C) 2022  Nathan Curran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import dev.deafkid.reflectionlib.exception.ReflectionFailException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public record ReflectionMethod<T>(Method realMethod) {

    public ReflectionMethod(Method realMethod) {
        this.realMethod = realMethod;
        realMethod.setAccessible(true);
    }

    public ReflectionClass reflectionClass() {
        return new ReflectionClass(realMethod.getDeclaringClass());
    }

    public static ReflectionMethod<?> findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException | SecurityException ex) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException ex1) {
                throw new ReflectionFailException(
                    "No methods found with " + methodName + (parameterTypes.length > 0 ? " with following type(s): " + Arrays.stream(parameterTypes)
                        .map(String::valueOf).collect(Collectors.joining(", ")) : " ") + " for " + clazz.getName());
            }
        }
        return new ReflectionMethod<>(method);
    }

    public T call(Object... parameters) {
        try {
            return (T) realMethod.invoke(null, parameters);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw new ReflectionFailException("Unable to make a method call for " + realMethod.getName());
        }
    }

    public ReflectionExecutor of(Object object) {
        return new ReflectionExecutor(object);
    }

    public class ReflectionExecutor {

        private final Object object;

        public ReflectionExecutor(Object object) {
            this.object = object;
        }

        public T call(Object... parameters) {
            try {
                return (T) realMethod.invoke(object, parameters);
            } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
                throw new ReflectionFailException("Unable to make a method call for " + realMethod.getName() + " from object " + object);
            }
        }
    }
}
