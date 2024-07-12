package br.com.hossomi.sample.todohistory.controller.item.model;

public record UpdateItemRequest(
        String name,
        Long assigneeId
) {}
