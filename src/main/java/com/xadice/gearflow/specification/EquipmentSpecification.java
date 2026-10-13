package com.xadice.gearflow.specification;

import com.xadice.gearflow.model.Equipment;
import com.xadice.gearflow.model.EquipmentCategory;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class EquipmentSpecification {

    private EquipmentSpecification() {
    }

    public static Specification<Equipment> isActive() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(
                        root.get("active")
                );
    }

    public static Specification<Equipment> search(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%" + search.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("name")
                            ),
                            pattern
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            pattern
                    )
            );
        };
    }

    public static Specification<Equipment> hasCategory(
            EquipmentCategory category
    ) {

        return (root, query, criteriaBuilder) -> {

            if (category == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("category"),
                    category
            );
        };
    }

    public static Specification<Equipment> minimumPrice(
            BigDecimal minPrice
    ) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("dailyPrice"),
                    minPrice
            );
        };
    }

    public static Specification<Equipment> maximumPrice(
            BigDecimal maxPrice
    ) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("dailyPrice"),
                    maxPrice
            );
        };
    }
}