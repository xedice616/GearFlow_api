package com.xadice.gearflow.service;

import com.xadice.gearflow.dto.request.EquipmentRequest;
import com.xadice.gearflow.dto.response.EquipmentResponse;
import com.xadice.gearflow.dto.response.PageResponse;

import com.xadice.gearflow.exception.InvalidOperationException;
import com.xadice.gearflow.exception.ResourceNotFoundException;

import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.EquipmentCategory;

import com.xadice.gearflow.repository.EquipmentRepository;

import com.xadice.gearflow.specification.EquipmentSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(
            EquipmentRepository equipmentRepository
    ) {
        this.equipmentRepository = equipmentRepository;
    }

    public EquipmentResponse createEquipment(
            EquipmentRequest request
    ) {

        Equipment equipment = new Equipment();

        equipment.setName(
                request.getName().trim()
        );

        equipment.setDescription(
                request.getDescription().trim()
        );

        equipment.setCategory(
                request.getCategory()
        );

        equipment.setDailyPrice(
                request.getDailyPrice()
        );

        equipment.setTotalQuantity(
                request.getTotalQuantity()
        );

        equipment.setImageUrl(
                normalizeImageUrl(request.getImageUrl())
        );

        equipment.setActive(true);

        Equipment savedEquipment =
                equipmentRepository.save(equipment);

        return mapToResponse(savedEquipment);
    }

    @Transactional(readOnly = true)
    public PageResponse<EquipmentResponse> getAllEquipment(
            String search,
            EquipmentCategory category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        validatePriceRange(minPrice, maxPrice);

        Specification<Equipment> specification =
                Specification
                        .where(EquipmentSpecification.isActive())
                        .and(EquipmentSpecification.search(search))
                        .and(EquipmentSpecification.hasCategory(category))
                        .and(EquipmentSpecification.minimumPrice(minPrice))
                        .and(EquipmentSpecification.maximumPrice(maxPrice));

        Page<EquipmentResponse> responsePage =
                equipmentRepository
                        .findAll(specification, pageable)
                        .map(this::mapToResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(
            Long id
    ) {

        Equipment equipment = findActiveEquipment(id);

        return mapToResponse(equipment);
    }

    public EquipmentResponse updateEquipment(
            Long id,
            EquipmentRequest request
    ) {

        Equipment equipment = findActiveEquipment(id);

        equipment.setName(
                request.getName().trim()
        );

        equipment.setDescription(
                request.getDescription().trim()
        );

        equipment.setCategory(
                request.getCategory()
        );

        equipment.setDailyPrice(
                request.getDailyPrice()
        );

        equipment.setTotalQuantity(
                request.getTotalQuantity()
        );

        equipment.setImageUrl(
                normalizeImageUrl(request.getImageUrl())
        );

        Equipment updatedEquipment =
                equipmentRepository.save(equipment);

        return mapToResponse(updatedEquipment);
    }

    public void deleteEquipment(
            Long id
    ) {

        Equipment equipment = findActiveEquipment(id);

        equipment.setActive(false);

        equipmentRepository.save(equipment);
    }

    private Equipment findActiveEquipment(
            Long id
    ) {

        Equipment equipment =
                equipmentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Equipment not found with id: " + id
                                )
                        );

        if (!equipment.isActive()) {
            throw new ResourceNotFoundException(
                    "Equipment not found with id: " + id
            );
        }

        return equipment;
    }

    private void validatePriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {

        if (minPrice != null
                && minPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidOperationException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice != null
                && maxPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidOperationException(
                    "Maximum price cannot be negative"
            );
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new InvalidOperationException(
                    "Minimum price cannot be greater than maximum price"
            );
        }
    }

    private String normalizeImageUrl(
            String imageUrl
    ) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        return imageUrl.trim();
    }

    private EquipmentResponse mapToResponse(
            Equipment equipment
    ) {

        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getDescription(),
                equipment.getCategory(),
                equipment.getDailyPrice(),
                equipment.getTotalQuantity(),
                equipment.isActive(),
                equipment.getImageUrl(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt()
        );
    }
}