package com.nawarup.api.catalog.service;

import com.nawarup.api.catalog.dto.*;
import com.nawarup.api.catalog.model.*;
import com.nawarup.api.catalog.repository.*;
import com.nawarup.api.common.exception.ApiException;
import com.nawarup.api.user.model.Business;
import com.nawarup.api.user.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final BeautyServiceRepository services;
    private final PhotoRepository photos;
    private final BusinessScheduleRepository schedules;
    private final BusinessRepository businesses;
    private final FileStorageService storage;

    // ---------- Servicios ----------
    @Transactional(readOnly = true)
    public List<ServiceResponse> listServices(Long businessId) {
        List<BeautyService> list = services.findByBusinessIdAndActiveTrueOrderByNameService(businessId);
        Map<Long, List<Photo>> byService = photos
                .findByServiceIdIn(list.stream().map(BeautyService::getId).toList()).stream()
                .collect(Collectors.groupingBy(p -> p.getService().getId()));
        return list.stream().map(s -> ServiceResponse.from(s, byService.getOrDefault(s.getId(), List.of()))).toList();
    }

    @Transactional
    public ServiceResponse createService(Long businessId, ServiceRequest r) {
        Business business = businesses.findById(businessId)
                .orElseThrow(() -> ApiException.notFound("Negocio no encontrado"));
        BeautyService s = new BeautyService();
        s.setBusiness(business);
        apply(s, r);
        return ServiceResponse.from(services.save(s), List.of());
    }

    @Transactional
    public ServiceResponse updateService(Long serviceId, Long businessId, ServiceRequest r) {
        BeautyService s = owned(serviceId, businessId);
        apply(s, r);
        return ServiceResponse.from(s, photos.findByServiceId(serviceId));
    }

    /** Baja lógica: las citas históricas siguen referenciando el servicio. */
    @Transactional
    public void deleteService(Long serviceId, Long businessId) {
        owned(serviceId, businessId).setActive(false);
    }

    private void apply(BeautyService s, ServiceRequest r) {
        s.setNameService(r.nameService().trim());
        s.setDescription(r.description());
        s.setPrice(r.price());
        s.setDurationMinutes(r.durationMinutes());
    }

    private BeautyService owned(Long serviceId, Long businessId) {
        BeautyService s = services.findById(serviceId)
                .orElseThrow(() -> ApiException.notFound("Servicio no encontrado"));
        if (!s.getBusiness().getId().equals(businessId)) throw ApiException.forbidden("El servicio pertenece a otro negocio");
        return s;
    }

    // ---------- Fotos ----------
    @Transactional(readOnly = true)
    public List<PhotoResponse> listPhotos(Long serviceId) {
        return photos.findByServiceId(serviceId).stream().map(PhotoResponse::from).toList();
    }

    @Transactional
    public PhotoResponse addPhoto(Long serviceId, Long businessId, MultipartFile file) {
        BeautyService s = owned(serviceId, businessId);
        if (file.isEmpty()) throw ApiException.badRequest("Archivo vacío");
        Photo p = new Photo();
        p.setService(s);
        p.setName(storage.store(file));
        return PhotoResponse.from(photos.save(p));
    }

    @Transactional
    public void deletePhoto(Long photoId, Long businessId) {
        Photo p = photos.findById(photoId).orElseThrow(() -> ApiException.notFound("Foto no encontrada"));
        if (!p.getService().getBusiness().getId().equals(businessId)) throw ApiException.forbidden("La foto pertenece a otro negocio");
        photos.delete(p);
        storage.delete(p.getName());
    }

    // ---------- Horarios ----------
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedules(Long businessId) {
        return schedules.findByBusinessIdOrderByDayOfWeek(businessId).stream().map(ScheduleResponse::from).toList();
    }

    /** Reemplaza todo el horario semanal del negocio (un tramo por día). */
    @Transactional
    public List<ScheduleResponse> replaceSchedules(Long businessId, List<ScheduleRequest> reqs) {
        Business business = businesses.findById(businessId)
                .orElseThrow(() -> ApiException.notFound("Negocio no encontrado"));
        Set<DayOfWeek> seen = new HashSet<>();
        for (ScheduleRequest r : reqs) {
            if (!r.openingTime().isBefore(r.closingTime()))
                throw ApiException.badRequest("La hora de apertura debe ser anterior a la de cierre (" + r.dayOfWeek() + ")");
            if (!seen.add(r.dayOfWeek()))
                throw ApiException.badRequest("Día repetido: " + r.dayOfWeek());
        }
        schedules.deleteByBusinessId(businessId);
        schedules.flush();
        List<BusinessSchedule> saved = schedules.saveAll(reqs.stream().map(r -> {
            BusinessSchedule s = new BusinessSchedule();
            s.setBusiness(business);
            s.setDayOfWeek(r.dayOfWeek());
            s.setOpeningTime(r.openingTime());
            s.setClosingTime(r.closingTime());
            return s;
        }).toList());
        return saved.stream().map(ScheduleResponse::from).toList();
    }
}
