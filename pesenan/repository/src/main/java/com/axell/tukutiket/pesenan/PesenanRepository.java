package com.axell.tukutiket.pesenan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesenanRepository extends JpaRepository<Order, String> {
}
