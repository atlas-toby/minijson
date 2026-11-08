package com.erenboz.minijson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MiniJsonTest {

    record User(String username, int age, boolean active, List<String> roles) {}

    @Test
    @DisplayName("Karmasik JSON metnini Map ve List veri yapilarina dogru ayristirmali")
    void shouldParseComplexJson() {
        String json = """
            {
              "title": "MiniJson",
              "version": 1,
              "score": 98.5,
              "features": ["recursive-descent", "reflection"],
              "author": {
                "name": "Eren",
                "graduated": true
              },
              "extra": null
            }
            """;

        Map<String, Object> map = MiniJson.parseObject(json);

        assertEquals("MiniJson", map.get("title"));
        assertEquals(1L, map.get("version"));
        assertEquals(98.5, map.get("score"));
        assertNull(map.get("extra"));

        @SuppressWarnings("unchecked")
        List<Object> features = (List<Object>) map.get("features");
        assertEquals(2, features.size());
        assertEquals("recursive-descent", features.get(0));

        @SuppressWarnings("unchecked")
        Map<String, Object> author = (Map<String, Object>) map.get("author");
        assertEquals("Eren", author.get("name"));
        assertEquals(true, author.get("graduated"));
    }

    @Test
    @DisplayName("Java nesnesini (Record / POJO) gecerli bir JSON metnine serilestirmeli")
    void shouldSerializeCustomObject() {
        User user = new User("atlastoby", 18, true, List.of("developer", "admin"));

        String json = MiniJson.stringify(user);

        assertTrue(json.contains("\"username\":\"atlastoby\""));
        assertTrue(json.contains("\"age\":18"));
        assertTrue(json.contains("\"active\":true"));
        assertTrue(json.contains("\"roles\":[\"developer\",\"admin\"]"));

        // Ciktinin tekrar parse edilebildigini dogrula (Round-trip)
        Map<String, Object> roundTrip = MiniJson.parseObject(json);
        assertEquals("atlastoby", roundTrip.get("username"));
        assertEquals(18L, roundTrip.get("age"));
        assertEquals(true, roundTrip.get("active"));
    }

    @Test
    @DisplayName("Tirnak ve ozel karakter kacislarini (escape) dogru yonetmeli")
    void shouldHandleEscapeCharacters() {
        String json = "{\"msg\": \"Merhaba\\n\\\"Dunya\\\"!\"}";
        Map<String, Object> map = MiniJson.parseObject(json);

        assertEquals("Merhaba\n\"Dunya\"!", map.get("msg"));
    }
}
