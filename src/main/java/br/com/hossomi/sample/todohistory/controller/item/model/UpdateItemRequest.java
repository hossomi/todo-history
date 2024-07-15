package br.com.hossomi.sample.todohistory.controller.item.model;

import java.util.Map;
import lombok.Builder;

@Builder
public record UpdateItemRequest(
        String name,
        Long assigneeId,
        Map<String, String> tags
) {}
