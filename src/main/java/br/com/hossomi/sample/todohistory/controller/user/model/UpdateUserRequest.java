package br.com.hossomi.sample.todohistory.controller.user.model;

import java.util.Map;

public record UpdateUserRequest(
        String name,
        Map<String, String> tags
) {}
