package com.nawarup.api.catalog.repository;

import com.nawarup.api.catalog.model.BeautyService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {
    List<BeautyService> findByBusinessIdAndActiveTrueOrderByNameService(Long businessId);
}
