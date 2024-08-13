package br.com.hossomi.sample.todohistory.controller.item.model;

import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import lombok.Builder;

import java.util.Map;

@Builder
public record ItemDto(
        Long id,
        String name,
        UserDto assignee,
        Map<String, String> tags
) {}
