package com.nawarup.api.catalog.controller;

import com.nawarup.api.catalog.dto.*;
import com.nawarup.api.catalog.service.CatalogService;
import com.nawarup.api.security.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService catalog;

    // ---- Público ----
    @GetMapping("/businesses/{businessId}/services")
    public List<ServiceResponse> listServices(@PathVariable Long businessId) {
        return catalog.listServices(businessId);
    }

    @GetMapping("/services/{serviceId}/photos")
    public List<PhotoResponse> listPhotos(@PathVariable Long serviceId) {
        return catalog.listPhotos(serviceId);
    }

    @GetMapping("/businesses/{businessId}/schedules")
    public List<ScheduleResponse> getSchedules(@PathVariable Long businessId) {
        return catalog.getSchedules(businessId);
    }

    // ---- Solo BUSINESS ----
    @PostMapping("/services")
    @PreAuthorize("hasRole('BUSINESS')")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse create(@AuthenticationPrincipal AuthUser user, @Valid @RequestBody ServiceRequest req) {
        return catalog.createService(user.id(), req);
    }

    @PutMapping("/services/{id}")
    @PreAuthorize("hasRole('BUSINESS')")
    public ServiceResponse update(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                                  @Valid @RequestBody ServiceRequest req) {
        return catalog.updateService(id, user.id(), req);
    }

    @DeleteMapping("/services/{id}")
    @PreAuthorize("hasRole('BUSINESS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        catalog.deleteService(id, user.id());
    }

    @PostMapping(value = "/services/{id}/photos", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('BUSINESS')")
    @ResponseStatus(HttpStatus.CREATED)
    public PhotoResponse addPhoto(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                                  @RequestParam("file") MultipartFile file) {
        return catalog.addPhoto(id, user.id(), file);
    }

    @DeleteMapping("/photos/{id}")
    @PreAuthorize("hasRole('BUSINESS')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        catalog.deletePhoto(id, user.id());
    }

    @PutMapping("/businesses/me/schedules")
    @PreAuthorize("hasRole('BUSINESS')")
    public List<ScheduleResponse> replaceSchedules(@AuthenticationPrincipal AuthUser user,
                                                   @RequestBody List<@Valid ScheduleRequest> req) {
        return catalog.replaceSchedules(user.id(), req);
    }
}
