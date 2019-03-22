package com.axell.tukutiket.bayarbayar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BayarbayarRepository extends JpaRepository<Bank, String> {
}
