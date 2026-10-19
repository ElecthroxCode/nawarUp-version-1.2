package com.nawarup.api.catalog.dto;

import com.nawarup.api.catalog.model.BeautyService;
import com.nawarup.api.catalog.model.Photo;

import java.math.BigDecimal;
import java.util.List;

public record ServiceResponse(Long id, Long businessId, String nameService, String description,
                              BigDecimal price, int durationMinutes, boolean active, List<PhotoResponse> photos) {
    public static ServiceResponse from(BeautyService s, List<Photo> photos) {
        return new ServiceResponse(s.getId(), s.getBusiness().getId(), s.getNameService(), s.getDescription(),
                s.getPrice(), s.getDurationMinutes(), s.isActive(),
                photos.stream().map(PhotoResponse::from).toList());
    }
}
