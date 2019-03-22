package com.axell.tukutiket.acara;

import java.util.List;

public interface AcaraService {
    List<EventResponse> findOngoingEvents();

    EventDetailResponse getEventDetail(String eventId);
}
