package com.axell.tukutiket.acara;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcaraRepository extends JpaRepository<Event, String> {
    List<Event> findAllByEventStatus(EventStatus eventStatus);
}
