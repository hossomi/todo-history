package br.com.hossomi.sample.todohistory.controller.user.model;

import java.util.Map;
import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String name,
        Map<String, String> tags
) {}
