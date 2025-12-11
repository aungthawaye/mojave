/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.mojave.component.misc.handy;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectConverter {

    // Serialization: Object -> Map -> JSON Path Map
    //--------------------------------------------------

    public static <T> T convertJsonPathMapToObject(Map<String, Object> jsonPathMap, Class<T> clazz)
        throws ReflectiveOperationException {

        Map<String, Object> nestedMap = unflattenJsonPathMap(jsonPathMap);
        return convertNestedMapToObject(nestedMap, clazz);
    }

    private static Object convertList(List<?> list, Type targetType)
        throws ReflectiveOperationException {

        Class<?> targetClass = getRawType(targetType);
        if (targetClass.isArray()) {
            Class<?> componentType = targetClass.getComponentType();
            Object array = Array.newInstance(componentType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, convertValue(list.get(i), componentType));
            }
            return array;
        } else if (List.class.isAssignableFrom(targetClass)) {
            Type componentType = getComponentType(targetType);
            List<Object> converted = new ArrayList<>();
            for (Object item : list) {
                converted.add(convertValue(item, componentType));
            }
            return converted;
        }
        return list;
    }

    public static Map<String, Object> convertMapToJsonPathMap(Map<String, Object> map) {

        Map<String, Object> flatMap = flattenMap(map);
        Map<String, Object> jsonPathMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            jsonPathMap.put("$." + entry.getKey(), entry.getValue());
        }
        return jsonPathMap;
    }

    private static <T> T convertNestedMapToObject(Map<String, Object> nestedMap, Class<T> clazz)
        throws ReflectiveOperationException {

        if (clazz.isRecord()) {
            RecordComponent[] components = clazz.getRecordComponents();
            Object[] args = new Object[components.length];
            for (int i = 0; i < components.length; i++) {
                RecordComponent rc = components[i];
                String name = rc.getName();
                Object value = nestedMap.get(name);
                Type componentType = rc.getGenericType();
                args[i] = convertValue(value, componentType);
            }
            Class<?>[] paramTypes = Arrays
                                        .stream(components)
                                        .map(RecordComponent::getType)
                                        .toArray(Class<?>[]::new);
            Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } else {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : nestedMap.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Type fieldType = field.getGenericType();
                    Object convertedValue = convertValue(value, fieldType);
                    field.set(obj, convertedValue);
                } catch (NoSuchFieldException e) {
                    // Ignore unknown fields
                }
            }
            return obj;
        }
    }

    public static Map<String, Object> convertObjectToMap(Object obj) throws IllegalAccessException {

        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return convertObjectToMap(obj, visited);
    }

    private static Map<String, Object> convertObjectToMap(Object obj, Set<Object> visited)
        throws IllegalAccessException {

        if (obj == null || visited.contains(obj)) {
            return null;
        }

        visited.add(obj);

        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            map.put(field.getName(), processValue(value, visited));
        }

        visited.remove(obj);
        return map;
    }

    // Map -> JSON Path Map
    //--------------------------------------------------

    private static Object convertValue(Object value, Type targetType)
        throws ReflectiveOperationException {

        Class<?> targetClass = getRawType(targetType);
        if (value instanceof Map) {
            return convertNestedMapToObject((Map<String, Object>) value, targetClass);
        } else if (value instanceof List) {
            return convertList((List<?>) value, targetType);
        }
        return value;
    }

    private static void flattenList(String key, List<?> list, Map<String, Object> flatMap) {

        for (int i = 0; i < list.size(); i++) {
            String indexedKey = key + "[" + i + "]";
            Object item = list.get(i);
            if (item instanceof Map) {
                flattenMap(indexedKey, (Map<String, Object>) item, flatMap);
            } else if (item instanceof List) {
                flattenList(indexedKey, (List<?>) item, flatMap);
            } else {
                flatMap.put(indexedKey, item);
            }
        }
    }

    private static Map<String, Object> flattenMap(Map<String, Object> map) {

        Map<String, Object> flatMap = new HashMap<>();
        flattenMap("", map, flatMap);
        return flatMap;
    }

    private static void flattenMap(String prefix,
                                   Map<String, Object> map,
                                   Map<String, Object> flatMap) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String newKey = prefix.isEmpty() ? key : prefix + "." + key;
            processEntry(newKey, value, flatMap);
        }
    }

    private static Type getComponentType(Type targetType) {

        if (targetType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length >= 1) {
                return typeArgs[0];
            }
        }
        return Object.class;
    }

    // Deserialization: JSON Path Map -> Map -> Object
    //--------------------------------------------------

    private static Class<?> getRawType(Type type) {

        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private static boolean isSimpleType(Class<?> clazz) {

        return clazz.isPrimitive() || clazz.equals(String.class) ||
                   Number.class.isAssignableFrom(clazz) || clazz.equals(Boolean.class) ||
                   clazz.equals(Character.class) || clazz.isEnum();
    }

    private static List<Object> processArray(Object array, Set<Object> visited)
        throws IllegalAccessException {

        List<Object> processed = new ArrayList<>();

        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            processed.add(processValue(Array.get(array, i), visited));
        }

        return processed;
    }

    private static void processEntry(String key, Object value, Map<String, Object> flatMap) {

        if (value instanceof Map) {

            flattenMap(key, (Map<String, Object>) value, flatMap);

        } else if (value instanceof List) {

            flattenList(key, (List<?>) value, flatMap);

        } else {

            flatMap.put(key, value);
        }
    }

    private static List<Object> processList(List<?> list, Set<Object> visited)
        throws IllegalAccessException {

        List<Object> processed = new ArrayList<>();

        for (Object item : list) {
            processed.add(processValue(item, visited));
        }

        return processed;
    }

    private static Object processValue(Object value, Set<Object> visited)
        throws IllegalAccessException {

        if (value == null) {
            return null;
        }

        if (isSimpleType(value.getClass())) {
            return value;
        }

        if (value.getClass().isArray()) {
            return processArray(value, visited);
        }

        if (value instanceof List) {
            return processList((List<?>) value, visited);
        }

        return convertObjectToMap(value, visited);
    }

    private static Map<String, Object> unflattenJsonPathMap(Map<String, Object> jsonPathMap) {

        Map<String, Object> nestedMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : jsonPathMap.entrySet()) {

            String jsonPath = entry.getKey();

            if (!jsonPath.startsWith("$.")) {
                continue;
            }

            String[] parts = jsonPath.substring(2).split("\\.|\\[|\\]", -1);

            Map<String, Object> current = nestedMap;

            for (int i = 0; i < parts.length; i++) {

                String part = parts[i];

                if (part.isEmpty()) {
                    continue;
                }

                boolean isLast = i == parts.length - 1;
                boolean listOrArray = i < parts.length - 1 && parts[i + 1].matches("\\d+");

                if (!current.containsKey(part)) {
                    current.put(
                        part,
                        listOrArray ? new ArrayList<Object>() : new HashMap<String, Object>());
                }

                Object next = current.get(part);

                if (listOrArray) {

                    int index = Integer.parseInt(parts[i + 1]);
                    List<Object> list = (List<Object>) next;

                    while (list.size() <= index) list.add(new HashMap<>());

                    current = (Map<String, Object>) list.get(index);
                    i++; // Skip index

                } else if (!isLast) {
                    current = (Map<String, Object>) next;
                }

                if (isLast) {
                    current.put(part, entry.getValue());
                }
            }
        }
        return nestedMap;
    }

}
