package br.com.hossomi.sample.todohistory.controller.item.model;

import br.com.hossomi.sample.todohistory.controller.user.model.User;
import lombok.Builder;

import java.util.Map;

@Builder
public record ItemDto(
        Long id,
        String name,
        User assignee,
        Map<String, String> tags
) {}
