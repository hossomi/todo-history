package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import com.google.common.collect.Sets;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final MappingService mappingService;

    @Transactional
    public Collection<Tag> setTags(GenericEntity parent, Map<String, String> tags) {
        Set<Tag> existingTags = newHashSet(mappingService.findChildren(parent, Tag.class,
                (tag, query, criteria) -> criteria.in(tag.get("name")).in(tags.keySet())));
        Map<String, Tag> existingTagMap = uniqueIndex(existingTags, Tag::name);
        Set<Tag> updatedTags = tags.entrySet().stream()
                .map(e -> {
                    Tag currentTag = existingTagMap.get(e.getKey());
                    if (currentTag == null) { return Tag.create(e.getKey(), e.getValue()); }

                    currentTag.value(e.getValue());
                    return currentTag;
                })
                .collect(toSet());
        Set<Tag> oldTags = Sets.difference(existingTags, updatedTags);
        Set<Tag> newTags = Sets.difference(updatedTags, existingTags);

        tagRepository.saveAll(updatedTags);
        tagRepository.deleteAll(oldTags);
        mappingService.associate(parent, newTags);
        mappingService.dissociate(parent, oldTags);

        return updatedTags;
    }
}
