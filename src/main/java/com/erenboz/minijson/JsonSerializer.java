package com.erenboz.minijson;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class JsonSerializer {

    private JsonSerializer() {}

    public static String serialize(Object obj) {
        StringBuilder sb = new StringBuilder();
        serializeValue(obj, sb);
        return sb.toString();
    }

    private static void serializeValue(Object obj, StringBuilder sb) {
        if (obj == null) {
            sb.append("null");
            return;
        }

        if (obj instanceof String || obj instanceof Character) {
            sb.append('"').append(escapeString(obj.toString())).append('"');
        } else if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj);
        } else if (obj instanceof Map<?, ?> map) {
            serializeMap(map, sb);
        } else if (obj instanceof Iterable<?> iterable) {
            serializeIterable(iterable, sb);
        } else if (obj.getClass().isArray()) {
            serializeArray(obj, sb);
        } else {
            serializeCustomObject(obj, sb);
        }
    }

    private static void serializeMap(Map<?, ?> map, StringBuilder sb) {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(',');
            sb.append('"').append(escapeString(String.valueOf(entry.getKey()))).append("\":");
            serializeValue(entry.getValue(), sb);
            first = false;
        }
        sb.append('}');
    }

    private static void serializeIterable(Iterable<?> iterable, StringBuilder sb) {
        sb.append('[');
        boolean first = true;
        for (Object item : iterable) {
            if (!first) sb.append(',');
            serializeValue(item, sb);
            first = false;
        }
        sb.append(']');
    }

    private static void serializeArray(Object array, StringBuilder sb) {
        sb.append('[');
        int len = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < len; i++) {
            if (i > 0) sb.append(',');
            serializeValue(java.lang.reflect.Array.get(array, i), sb);
        }
        sb.append(']');
    }

    private static void serializeCustomObject(Object obj, StringBuilder sb) {
        sb.append('{');
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object val = field.get(obj);
                if (!first) sb.append(',');
                sb.append('"').append(field.getName()).append("\":");
                serializeValue(val, sb);
                first = false;
            } catch (IllegalAccessException ignored) {}
        }
        sb.append('}');
    }

    private static String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
