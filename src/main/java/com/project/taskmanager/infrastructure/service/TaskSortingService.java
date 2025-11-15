package com.project.taskmanager.infrastructure.service;


import com.project.taskmanager.domain.entity.Task;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public class TaskSortingService {

    public enum SortCriteria {
        PRIORITY,
        DUE_DATE,
        CATEGORY,
        CREATED_AT
    }

    private static final ConcurrentHashMap<SortCriteria, Comparator<Task>> comparators =
            new ConcurrentHashMap<>();

    static {
        comparators.put(SortCriteria.PRIORITY,
                Comparator.comparing((Task t) -> t.getPriority().getValue()).reversed());

        comparators.put(SortCriteria.DUE_DATE,
                Comparator.comparing(Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())));

        comparators.put(SortCriteria.CATEGORY,
                Comparator.comparing(task -> task.getCategory().name()));

        comparators.put(SortCriteria.CREATED_AT,
                Comparator.comparing(Task::getCreatedAt).reversed());
    }

    public Comparator<Task> getComparator(SortCriteria criteria) {
        return comparators.get(criteria);
    }

    public Comparator<Task> getComparator(String criteriaStr) {
        try {
            SortCriteria criteria = SortCriteria.valueOf(criteriaStr.toUpperCase());
            return getComparator(criteria);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort criteria: " + criteriaStr +
                    ". Valid values are: PRIORITY, DUE_DATE, CATEGORY, CREATED_AT");
        }
    }

    public Comparator<Task> getMultiCriteriaComparator(SortCriteria... criteriaArray) {
        if (criteriaArray == null || criteriaArray.length == 0) {
            return getComparator(SortCriteria.CREATED_AT);
        }

        Comparator<Task> comparator = getComparator(criteriaArray[0]);
        for (int i = 1; i < criteriaArray.length; i++) {
            comparator = comparator.thenComparing(getComparator(criteriaArray[i]));
        }
        return comparator;
    }
}