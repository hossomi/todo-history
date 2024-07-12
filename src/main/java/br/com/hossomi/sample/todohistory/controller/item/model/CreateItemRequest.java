package br.com.hossomi.sample.todohistory.controller.item.model;

public record CreateItemRequest(
        String name,
        Long assigneeId
) {}
