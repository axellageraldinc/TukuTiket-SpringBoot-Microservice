package com.axell.tukutiket.acara;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AcaraServiceImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private AcaraRepository acaraRepository;

    @InjectMocks
    private AcaraServiceImpl acaraService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findOngoingEvents() {
        when(acaraRepository.findAll())
                .thenReturn(Collections.singletonList(
                        Event.builder()
                                .id("1")
                                .name("1")
                                .date(new Date())
                                .eventStatus(EventStatus.TO_BE_HELD)
                                .venue("1")
                                .ticketPrice(BigDecimal.valueOf(1000))
                                .build()
                ));

        List<EventResponse> eventResponseList = acaraService.findOngoingEvents();

        assertThat(eventResponseList, notNullValue());
        assertThat(eventResponseList.isEmpty(), equalTo(false));
        assertThat(eventResponseList.size(), equalTo(1));

        verify(acaraRepository, times(1)).findAll();
    }

    @Test(expected = RuntimeException.class)
    public void getEventDetailFailedEventNotFound() {
        when(acaraRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        acaraService.getEventDetail("99");

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.EVENT_NOT_FOUND.toString());

        verify(acaraRepository, times(1)).findById(anyString());
    }

    @Test
    public void getEventDetailSuccess() {
        when(acaraRepository.findById(anyString()))
                .thenReturn(Optional.of(Event.builder()
                        .id("1")
                        .name("1")
                        .date(new Date())
                        .eventStatus(EventStatus.TO_BE_HELD)
                        .venue("1")
                        .ticketPrice(BigDecimal.valueOf(1000))
                        .build()));

        EventDetailResponse eventDetailResponse = acaraService.getEventDetail("1");

        assertThat(eventDetailResponse, notNullValue());
        assertThat(eventDetailResponse.getName(), equalTo("1"));

        verify(acaraRepository, times(1)).findById(anyString());
    }
}