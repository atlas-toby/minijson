package com.erenboz.minijson;

import java.util.*;

public final class JsonParser {

    private final String src;
    private final int len;
    private int pos = 0;

    public JsonParser(String src) {
        this.src = Objects.requireNonNull(src, "JSON kaynagi null olamaz.");
        this.len = src.length();
    }

    public static Object parse(String json) {
        return new JsonParser(json).parseValue();
    }

    public Object parseValue() {
        skipWhitespace();
        if (pos >= len) {
            throw new IllegalArgumentException("Beklenmedik JSON sonu.");
        }

        char c = src.charAt(pos);
        if (c == '{') {
            return parseObject();
        } else if (c == '[') {
            return parseArray();
        } else if (c == '"') {
            return parseString();
        } else if (c == 't' || c == 'f') {
            return parseBoolean();
        } else if (c == 'n') {
            return parseNull();
        } else if (c == '-' || Character.isDigit(c)) {
            return parseNumber();
        }

        throw new IllegalArgumentException("Gecersiz JSON karakteri '" + c + "' konum: " + pos);
    }

    private Map<String, Object> parseObject() {
        match('{');
        Map<String, Object> map = new LinkedHashMap<>();
        skipWhitespace();

        if (pos < len && src.charAt(pos) == '}') {
            pos++;
            return map;
        }

        while (pos < len) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            match(':');
            Object value = parseValue();
            map.put(key, value);

            skipWhitespace();
            if (pos < len && src.charAt(pos) == ',') {
                pos++;
            } else if (pos < len && src.charAt(pos) == '}') {
                pos++;
                break;
            } else {
                throw new IllegalArgumentException("Objede ',' veya '}' bekleniyordu, konum: " + pos);
            }
        }
        return map;
    }

    private List<Object> parseArray() {
        match('[');
        List<Object> list = new ArrayList<>();
        skipWhitespace();

        if (pos < len && src.charAt(pos) == ']') {
            pos++;
            return list;
        }

        while (pos < len) {
            Object value = parseValue();
            list.add(value);

            skipWhitespace();
            if (pos < len && src.charAt(pos) == ',') {
                pos++;
            } else if (pos < len && src.charAt(pos) == ']') {
                pos++;
                break;
            } else {
                throw new IllegalArgumentException("Dizide ',' veya ']' bekleniyordu, konum: " + pos);
            }
        }
        return list;
    }

    private String parseString() {
        match('"');
        StringBuilder sb = new StringBuilder();

        while (pos < len) {
            char c = src.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            } else if (c == '\\') {
                if (pos >= len) {
                    throw new IllegalArgumentException("Gecersiz escape karakteri.");
                }
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    default -> sb.append(esc);
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Kapatilmamis String.");
    }

    private Number parseNumber() {
        int start = pos;
        if (src.charAt(pos) == '-') {
            pos++;
        }
        while (pos < len && Character.isDigit(src.charAt(pos))) {
            pos++;
        }

        boolean isFloating = false;
        if (pos < len && src.charAt(pos) == '.') {
            isFloating = true;
            pos++;
            while (pos < len && Character.isDigit(src.charAt(pos))) {
                pos++;
            }
        }

        if (pos < len && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
            isFloating = true;
            pos++;
            if (pos < len && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < len && Character.isDigit(src.charAt(pos))) {
                pos++;
            }
        }

        String numStr = src.substring(start, pos);
        if (isFloating) {
            return Double.valueOf(numStr);
        } else {
            return Long.valueOf(numStr);
        }
    }

    private Boolean parseBoolean() {
        if (src.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        } else if (src.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Gecersiz boolean token, konum: " + pos);
    }

    private Object parseNull() {
        if (src.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new IllegalArgumentException("Gecersiz null token, konum: " + pos);
    }

    private void match(char expected) {
        skipWhitespace();
        if (pos >= len || src.charAt(pos) != expected) {
            throw new IllegalArgumentException("Beklenen karakter '" + expected + "', konum: " + pos);
        }
        pos++;
    }

    private void skipWhitespace() {
        while (pos < len) {
            char c = src.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                pos++;
            } else {
                break;
            }
        }
    }
}
