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
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public record ReflectionField<T>(Field realField) {

    public ReflectionField(Field realField) {
        this.realField = realField;
        realField.setAccessible(true);
    }

    public ReflectionClass reflectionClass() {
        return new ReflectionClass(realField.getDeclaringClass());
    }

    public static ReflectionField<?> findField(Class<?> clazz, String fieldName) {
        Field f;
        try {
            f = clazz.getField(fieldName);
        } catch (NoSuchFieldException | SecurityException ex) {
            try {
                f = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException ex1) {
                throw new ReflectionFailException("Unable to find field " + fieldName + " for class " + clazz.getName());
            }
        }
        return new ReflectionField<>(f);
    }

    public ReflectionExecutor of(Object object) {
        return new ReflectionExecutor(object);
    }

    public class ReflectionExecutor {

        private final Object object;

        public ReflectionExecutor(Object object) {
            this.object = object;
        }

        public void set(T value) {
            try {
                realField.set(object, value);
            } catch (IllegalAccessException ex) {
                throw new ReflectionFailException("Unable to set to value " + value + " for field " + realField.getName());
            }
        }

        public T get() {
            try {
                return (T) realField.get(object);
            } catch (IllegalAccessException ex) {
                throw new ReflectionFailException("Unable to get field " + realField.getName());
            }
        }
    }
}
