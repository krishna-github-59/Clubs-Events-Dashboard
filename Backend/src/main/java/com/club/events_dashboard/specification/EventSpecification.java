package com.club.events_dashboard.specification;

import com.club.events_dashboard.entity.Event;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EventSpecification {

    public static Specification<Event> hasClubId(Long clubId) {
        return (root, query, builder) ->
                clubId == null ? null : builder.equal(root.get("club").get("id"), clubId);
    }

    public static Specification<Event> hasNameLike(String name) {
        return (root, query, builder) -> {
            if (name == null || name.isEmpty()) return null;
            // Case-insensitive search
            return builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Event> hasStartDate(LocalDate startDate) {
        return (root, query, builder) ->
                startDate == null ? null : builder.greaterThanOrEqualTo(root.get("eventDate"), startDate);
    }

    public static Specification<Event> hasEndDate(LocalDate endDate) {
        return (root, query, builder) ->
                endDate == null ? null : builder.lessThanOrEqualTo(root.get("eventDate"), endDate);
    }
}
