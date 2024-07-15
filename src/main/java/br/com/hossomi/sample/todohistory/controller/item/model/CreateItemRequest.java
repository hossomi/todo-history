package br.com.hossomi.sample.todohistory.controller.item.model;

import lombok.Builder;

@Builder
public record CreateItemRequest(
        String name,
        Long assigneeId
) {}
