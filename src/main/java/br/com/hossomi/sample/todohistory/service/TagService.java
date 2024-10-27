package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import br.com.hossomi.sample.todohistory.model.MTag;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import static br.com.hossomi.sample.todohistory.model.MTag.nameIsAnyOf;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.jpa.domain.Specification.not;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final MappingService mappingService;

    /**
     * Updates all {@link MTag} entities associated to {@code target} entity to be exactly the {@code tags} map. To do
     * so, it will create, update and/or delete tag entities accordingly.
     */
    @Transactional
    public Map<String, String> setTags(GenericEntity target, Map<String, String> tags) {
        Map<String, MTag> existingTagsByName = uniqueIndex(
                mappingService.findChildren(target, MTag.class, nameIsAnyOf(tags.keySet())),
                MTag::name);

        // For each requested tag entry, update existing tag or create a new to obtain the desired tags.
        Set<MTag> finalTags = tags.entrySet().stream()
                .map(e -> {
                    MTag existingTag = existingTagsByName.get(e.getKey());
                    if (existingTag == null) {
                        return MTag.create(e.getKey(), e.getValue());
                    }

                    existingTag.value(e.getValue());
                    return existingTag;
                })
                .collect(toSet());


        tagRepository.saveAll(finalTags);
        // Associated created tags to target
        mappingService.associate(target, Sets.difference(finalTags, Set.copyOf(existingTagsByName.values())));

        Specification<MTag> isExtraneousTag = not(nameIsAnyOf(tags.keySet()));
        // Dissociate extraneous tags from target
        mappingService.dissociate(target, MTag.class, isExtraneousTag);
        // Delete extraneous tags no longer needed
        tagRepository.delete(isExtraneousTag);

        return MTag.toMap(finalTags);
    }
}
