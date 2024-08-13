package br.com.hossomi.sample.todohistory.controller.user.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record CreateUserRequest(
        String name,
        Map<String, String> tags
) { }
