package br.com.hossomi.sample.todohistory.controller.user.model;

import lombok.Builder;

@Builder
public record CreateUserRequest(
        String name
) {}
