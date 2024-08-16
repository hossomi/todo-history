package br.com.hossomi.sample.todohistory.controller.user.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record User(
        Long id,
        String name,
        Map<String, String> tags
) {}
