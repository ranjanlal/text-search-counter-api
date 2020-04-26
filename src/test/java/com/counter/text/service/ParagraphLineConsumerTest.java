package com.counter.text.service;

import com.counter.text.dao.TextCounterDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
public class ParagraphLineConsumerTest {

    @TestConfiguration
    static class ParagraphLineConsumerTestContextConfiguration {
        @Bean
        public ParagraphLineConsumer paragraphLineConsumer() {
            return new ParagraphLineConsumer();
        }
    }

    @Autowired
    private ParagraphLineConsumer paragraphLineConsumer;

    @MockBean
    private TextCounterDatabase textCounterDatabase;

    @Test
    public void givenEmptyMessageString_whenConsumerParses_thenReturnEmptyMap() {

        // when
        paragraphLineConsumer.consumeMessage("");

        // then
        verify(textCounterDatabase).save(Collections.EMPTY_MAP);
    }

    @Test
    public void givenEmptyMessageString_whenConsumerParses_thenReturnMapWithWordCounts() {

        // given
        Map<String, Long> wordsCountMap = Map.of(
                "abc", 1L,
                "xyz", 2L,
                "pqr", 2L,
                "ABC", 1L
        );

        // when
        paragraphLineConsumer.consumeMessage("abc xyz, pqr, pqr xyz ABC");

        // then
        verify(textCounterDatabase).save(wordsCountMap);
    }


}