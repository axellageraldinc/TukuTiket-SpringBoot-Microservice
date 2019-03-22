package com.axell.tukutiket.acara;

import com.axell.microservices.common.webmodel.WebResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/acara")
public class AcaraRestController {
    @Autowired
    private AcaraService acaraService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EventWebResponse>> findAllOngoingEvents() {
        return WebResponse.OK(
                toAcaraWebResponseList(
                        acaraService.findOngoingEvents()
                )
        );
    }

    private List<EventWebResponse> toAcaraWebResponseList(List<EventResponse> eventResponseList) {
        return eventResponseList
                .stream()
                .map(this::toAcaraWebResponse)
                .collect(Collectors.toList());
    }

    private EventWebResponse toAcaraWebResponse(EventResponse eventResponse) {
        EventWebResponse eventWebResponse = new EventWebResponse();
        BeanUtils.copyProperties(eventResponse, eventWebResponse);
        return eventWebResponse;
    }

    @GetMapping(
            value = "/{eventId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EventDetailWebResponse> getEventDetail(@PathVariable(value = "eventId") String eventId) {
        EventDetailResponse eventDetailResponse = acaraService.getEventDetail(eventId);
        return WebResponse.OK(
                toAcaraDetailWebResponse(eventDetailResponse)
        );
    }

    private EventDetailWebResponse toAcaraDetailWebResponse(EventDetailResponse eventDetailResponse) {
        EventDetailWebResponse eventDetailWebResponse = new EventDetailWebResponse();
        BeanUtils.copyProperties(eventDetailResponse, eventDetailWebResponse);
        return eventDetailWebResponse;
    }
}
