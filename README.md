# MiniJson

Lightweight, zero-dependency **JSON Parser and Object Serializer** written in pure Java 21+.

## Key Features
- **Zero Dependencies:** Pure standard Java without Jackson, Gson, or external libraries.
- **Recursive Descent Parser:** Custom lexical and syntax analysis supporting objects, arrays, nested structures, strings, numbers, booleans, and null.
- **Reflection-Based Serializer:** Serializes any Java Record, POJO, Collection, or Map into valid JSON formatting.
- **Escape Character Handling:** Supports standard escape characters (`\"`, `\\`, `\n`, `\t`, `\r`).
- **Comprehensive Unit Tests:** Verified with JUnit 5.

## Installation (Maven)
```xml
<dependency>
    <groupId>com.erenboz</groupId>
    <artifactId>minijson</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### 1. Parse JSON to Java Map
```java
String json = "{\"title\":\"MiniJson\",\"version\":1,\"active\":true}";
Map<String, Object> map = MiniJson.parseObject(json);

System.out.println(map.get("title")); // MiniJson
System.out.println(map.get("version")); // 1
```

### 2. Serialize Java Object / Record to JSON
```java
record User(String username, int age, List<String> tags) {}

User user = new User("atlastoby", 18, List.of("developer", "backend"));
String json = MiniJson.stringify(user);

System.out.println(json);
// {"username":"atlastoby","age":18,"tags":["developer","backend"]}
```
