package com.axell.tukutiket.acara;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AcaraServiceImpl implements AcaraService {

    @Autowired
    private AcaraRepository acaraRepository;

    @Override
    public List<EventResponse> findOngoingEvents() {
        return toEventResponseList(acaraRepository.findAllByEventStatus(EventStatus.TO_BE_HELD));
    }

    private List<EventResponse> toEventResponseList(List<Event> eventList) {
        return eventList
                .stream()
                .map(this::toEventResponse)
                .collect(Collectors.toList());
    }

    private EventResponse toEventResponse(Event event) {
        EventResponse eventResponse = new EventResponse();
        BeanUtils.copyProperties(event, eventResponse);
        return eventResponse;
    }

    @Override
    public EventDetailResponse getEventDetail(String eventId) {
        Optional<Event> optionalEvent = acaraRepository.findById(eventId);
        if (!optionalEvent.isPresent())
            throw new RuntimeException(ErrorCode.EVENT_NOT_FOUND.toString());
        Event event = optionalEvent.get();
        return toEventDetailResponse(event);
    }

    private EventDetailResponse toEventDetailResponse(Event event) {
        EventDetailResponse eventDetailResponse = new EventDetailResponse();
        BeanUtils.copyProperties(event, eventDetailResponse);
        return eventDetailResponse;
    }
}
