package br.com.hossomi.sample.todohistory.controller.item.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record UpdateItemRequest(
        String name,
        Long assigneeId,
        Map<String, String> tags
) {}
