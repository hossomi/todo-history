package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.BaseEntity;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import com.google.common.collect.Sets;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final MappingRepository mappingRepository;

    @Transactional
    public void setTags(BaseEntity parent, Map<String, String> tags) {
        Class<? extends BaseEntity> parentType = parent.getClass();
        Set<Tag> existingTags = newHashSet(tagRepository.findByParent(parentType, parent.getId()));
        Map<String, Tag> existingTagMap = uniqueIndex(existingTags, Tag::getName);
        Set<Tag> updatedTags = tags.entrySet().stream()
                .map(e -> {
                    Tag currentTag = existingTagMap.get(e.getKey());
                    if (currentTag == null) return Tag.create(e.getKey(), e.getValue());

                    currentTag.setValue(e.getValue());
                    return currentTag;
                })
                .collect(toSet());
        Set<Tag> oldTags = Sets.difference(existingTags, updatedTags);
        Set<Tag> newTags = Sets.difference(updatedTags, existingTags);

        tagRepository.saveAll(updatedTags);
        tagRepository.deleteAll(oldTags);
        if (!oldTags.isEmpty()) mappingRepository.deleteByChildren(Tag.class, transform(oldTags, Tag::getId));
        if (!newTags.isEmpty()) mappingRepository.saveAll(transform(newTags, tag -> Mapping.create(parent, tag)));
    }
}
