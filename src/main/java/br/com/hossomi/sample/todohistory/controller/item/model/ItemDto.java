package br.com.hossomi.sample.todohistory.controller.item.model;

import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import java.util.Map;
import lombok.Builder;

@Builder
public record ItemDto(
        Long id,
        String name,
        UserDto assignee,
        Map<String, String> tags
) {}
