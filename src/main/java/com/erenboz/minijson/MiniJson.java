package com.erenboz.minijson;

import java.util.Map;

public final class MiniJson {

    private MiniJson() {}

    public static Object parse(String json) {
        return JsonParser.parse(json);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object result = parse(json);
        if (result instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new IllegalArgumentException("JSON bir obje degil: " + json);
    }

    public static String stringify(Object obj) {
        return JsonSerializer.serialize(obj);
    }
}
