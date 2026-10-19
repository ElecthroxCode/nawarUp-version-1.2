package com.nawarup.api.catalog.dto;

import com.nawarup.api.catalog.model.Photo;

public record PhotoResponse(Long id, String name, String url) {
    public static PhotoResponse from(Photo p) {
        return new PhotoResponse(p.getId(), p.getName(), "/uploads/" + p.getName());
    }
}
