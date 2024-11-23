package com.example.app.specification;

import com.example.app.dto.specificationDTO.TaskParamDTO;
import com.example.app.models.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskSpecification {
    public Specification<Task> build(TaskParamDTO params, Sort sort) {
        Specification<Task> specification = Specification.where(null);

        specification = specification.and(withAuthorId(params.getAuthorId()))
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatusCont(params.getStatusCont()))
                .and(withPriorityCont(params.getPriorityCont()));

        if (sort != null) {
            final Specification<Task> finalSpecification = specification;
            return (root, query, criteriaBuilder) -> {
                query.orderBy(sort.get().map(order ->
                        order.isAscending()
                                ? criteriaBuilder.asc(root.get(order.getProperty()))
                                : criteriaBuilder.desc(root.get(order.getProperty()))
                ).collect(Collectors.toList()));
                return finalSpecification.toPredicate(root, query, criteriaBuilder);
            };
        }

        return specification;
    }

    private Specification<Task> withAuthorId(Long authorId) {
        return ((root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return criteriaBuilder.conjunction();
            }

            root.join("taskComments", JoinType.LEFT);

            return criteriaBuilder.equal(root.get("author").get("id"), authorId);

        });
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return ((root, query, criteriaBuilder) -> {
            if (assigneeId == null) {
                return criteriaBuilder.conjunction();
            }

            root.join("taskComments", JoinType.LEFT);

            return criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
        });
    }

    private Specification<Task> withStatusCont(String statusCont) {
        return ((root, query, criteriaBuilder) -> {
            if (statusCont == null || statusCont.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("status").get("name")),
                    "%" + statusCont.toLowerCase() + "%");
        });
    }

    private Specification<Task> withPriorityCont(String priorityCont) {
        return ((root, query, criteriaBuilder) -> {
            if (priorityCont == null || priorityCont.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("priority").get("priorityName")),
                    "%" + priorityCont.toLowerCase() + "%");
        });
    }
}

