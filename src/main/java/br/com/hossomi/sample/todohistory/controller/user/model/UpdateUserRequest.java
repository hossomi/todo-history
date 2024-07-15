package br.com.hossomi.sample.todohistory.controller.user.model;

import java.util.Map;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
        String name,
        Map<String, String> tags
) {}
