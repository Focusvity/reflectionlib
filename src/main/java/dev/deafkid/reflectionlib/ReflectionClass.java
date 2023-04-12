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

public record ReflectionClass(Class<?> realClass) {

    public static ReflectionClass ofString(String pattern) {
        try {
            Class<?> clazz = Class.forName(pattern);
            return new ReflectionClass(clazz);
        } catch (ClassNotFoundException ex) {
            throw new ReflectionFailException("Unable to find any classes with pattern " + pattern);
        }
    }

    public ReflectionMethod<?> findMethod(String methodName, Class<?>... parameterTypes) {
        return ReflectionMethod.findMethod(realClass, methodName, parameterTypes);
    }

    public ReflectionField<?> findField(String fieldName) {
        return ReflectionField.findField(realClass, fieldName);
    }
}
