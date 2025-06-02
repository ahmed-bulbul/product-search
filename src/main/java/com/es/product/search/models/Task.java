package com.es.product.search.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "task_group", nullable = false)
    private String group;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "description")
    private String description;

    @Column(name = "trigger_day")
    private Integer triggerDay;
}
