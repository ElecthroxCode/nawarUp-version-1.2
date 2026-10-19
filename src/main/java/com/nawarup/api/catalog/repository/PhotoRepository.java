package com.nawarup.api.catalog.repository;

import com.nawarup.api.catalog.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByServiceId(Long serviceId);
    List<Photo> findByServiceIdIn(Collection<Long> serviceIds);
}
