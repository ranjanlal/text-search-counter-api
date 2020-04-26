package com.counter.text.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TextCounterDatabaseImplTest {

    @TestConfiguration
    static class TextCounterServiceImplTestContextConfiguration {
        @Bean
        public TextCounterDatabase textCounterDatabase() {
            return new TextCounterDatabaseImpl(new HashMap<>());
        }
    }

    @Autowired
    private TextCounterDatabase textCounterDatabase;

    @Before
    public void setUp() {
        textCounterDatabase.clear();
    }

    @Test
    public void testGivenEmptyDatabaseWhenGetCountThenReturnZero() {
        Long count = textCounterDatabase.getCount("unknown");
        // then
        assertEquals(0L, count.longValue());
    }

    @Test
    public void testGivenEmptyDatabaseWhenGetTopNWordsThenReturnEmptyMap() {
        Map<String, Long> top5Words = textCounterDatabase.getTopWords(5);
        // then
        assertNotNull(top5Words);
        assertTrue(top5Words.isEmpty());
    }

    @Test
    public void testGivenEmptyDatabaseWhenSaveThenReturnEmptyMap() {
        textCounterDatabase.save(Collections.EMPTY_MAP);
        // then
        Long count = textCounterDatabase.getCount("abc");
        assertEquals(0L, count.longValue());
        // then
        Map<String, Long> top5Words = textCounterDatabase.getTopWords(5);
        assertNotNull(top5Words);
        assertTrue(top5Words.isEmpty());
    }


    @Test
    public void testSaveTextCountsInToDatabase() {
        // given
        Map<String, Long> textCounts1 = Map.of(
                "lorem", 1L,
                "Ipsem", 2L,
                "Lorem", 2L);
        // when
        textCounterDatabase.save(textCounts1);
        // then
        assertEquals(3L, textCounterDatabase.getCount("Lorem").longValue());
        assertEquals(2L, textCounterDatabase.getCount("ipsem").longValue());
        assertEquals(0L, textCounterDatabase.getCount("zoper").longValue());

        Map<String, Long> textCounts2 = Map.of(
                "valen", 7L,
                "Ipsem", 3L,
                "Lorem", 2L);
        textCounterDatabase.save(textCounts2);
        assertEquals(7L, textCounterDatabase.getCount("Valen").longValue());
        assertEquals(5L, textCounterDatabase.getCount("ipsem").longValue());
        assertEquals(5L, textCounterDatabase.getCount("Lorem").longValue());
    }

    @Test
    public void testGivenNonEmptyDatabaseWhenTopNTextCountsThenReturnMapWithNElementsSortedDescendingOnWordCount() {
        // given
        Map<String, Long> textCounts = Map.of(
                "abc", 1L,
                "radndom", 2L,
                "Abc", 1L,
                "xyz", 3L,
                "kolt", 5L,
                "KOLT", 8L,
                "brass", 9L,
                "zoo", 11L,
                "frame", 19L,
                "Bat", 6L);
        textCounterDatabase.save(textCounts);

        // when
        Map<String, Long> top5Words = textCounterDatabase.getTopWords(5);

        // then
        assertEquals(5, top5Words.entrySet().size());

        Iterator<Map.Entry<String, Long>> iterator = top5Words.entrySet().iterator();

        Map.Entry<String, Long> first = iterator.next();
        assertEquals("frame", first.getKey());
        assertEquals(19L, first.getValue().longValue());

        Map.Entry<String, Long> second = iterator.next();
        assertEquals("kolt", second.getKey());
        assertEquals(13L, second.getValue().longValue());

        Map.Entry<String, Long> third = iterator.next();
        assertEquals("zoo", third.getKey());
        assertEquals(11L, third.getValue().longValue());

        Map.Entry<String, Long> fourth = iterator.next();
        assertEquals("brass", fourth.getKey());
        assertEquals(9L, fourth.getValue().longValue());

        Map.Entry<String, Long> fifth = iterator.next();
        assertEquals("bat", fifth.getKey());
        assertEquals(6L, fifth.getValue().longValue());

    }
}