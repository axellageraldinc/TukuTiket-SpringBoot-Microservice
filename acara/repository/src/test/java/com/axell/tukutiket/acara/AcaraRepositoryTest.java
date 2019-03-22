package com.axell.tukutiket.acara;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext
public class AcaraRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AcaraRepository acaraRepository;

    @Test
    public void findAllByEventStatus() {
        entityManager.persist(Event.builder()
                .id("1")
                .name("1")
                .date(new Date())
                .venue("1")
                .eventStatus(EventStatus.TO_BE_HELD)
                .ticketPrice(BigDecimal.valueOf(1000))
                .build());

        List<Event> eventList = acaraRepository.findAllByEventStatus(EventStatus.TO_BE_HELD);

        assertThat(eventList, notNullValue());
        assertThat(eventList.isEmpty(), equalTo(false));
        assertThat(eventList.size(), equalTo(1));
        assertThat(eventList.get(0).getId(), equalTo("1"));
    }
}