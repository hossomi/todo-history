package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.MTag;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import br.com.hossomi.sample.todohistory.test.ParentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    private static final List<MTag> TAGS = List.of(
            MTag.create("A", "1").id(1L),
            MTag.create("B", "2").id(2L),
            MTag.create("C", "3").id(3L));

    @Mock
    private TagRepository tagRepository;
    @Mock
    private MappingService mappingService;
    @Captor
    private ArgumentCaptor<Set<MTag>> tagsCaptor;

    private final ParentEntity entity = new ParentEntity();

    private TagService tagService;

    @BeforeEach
    void setup() {
        when(mappingService.findChildren(eq(entity), eq(MTag.class), any())).thenReturn(TAGS);
        tagService = new TagService(tagRepository, mappingService);
    }

    @Test
    void setTagsCreatesNewTag() {
        Map<String, String> newTagsMap = Map.of("X", "100");
        Collection<MTag> newTags = MTag.fromMap(newTagsMap);

        Map<String, String> tags = tagService.setTags(entity, newTagsMap);
        assertThat(tags).containsExactlyInAnyOrderEntriesOf(newTagsMap);

        verify(tagRepository).saveAll(newTags);
        verify(mappingService).associate(entity, newTags);
    }

    @Test
    void setTagsCreatesMultipleNewTag() {
        Map<String, String> newTagsMap = Map.of("X", "100", "Y", "101");
        Set<MTag> newTags = MTag.fromMap(newTagsMap);

        Map<String, String> tags = tagService.setTags(entity, newTagsMap);
        assertThat(tags).containsExactlyInAnyOrderEntriesOf(newTagsMap);

        verify(tagRepository).saveAll(newTags);
        verify(mappingService).associate(entity, newTags);
    }

    @Test
    void setTagsUpdatesExistingTag() {
        MTag existingTag = TAGS.get(0);
        Map<String, String> setTagsMap = Map.of(existingTag.name(), "999");

        Map<String, String> tags = tagService.setTags(entity, setTagsMap);
        assertThat(tags).containsExactlyInAnyOrderEntriesOf(setTagsMap);

        verify(tagRepository).saveAll(tagsCaptor.capture());
        verify(mappingService).associate(entity, Set.of());
        assertThat(tagsCaptor.getValue())
                .singleElement()
                .isSameAs(existingTag)
                .satisfies(tag -> assertThat(tag.value()).isEqualTo("999"));
    }

    @Test
    void setTagsDeletesMissingTag() {
        MTag keepTag = TAGS.get(0);
        Map<String, String> setTagsMap = Map.of(keepTag.name(), keepTag.value());

        Map<String, String> tags = tagService.setTags(entity, setTagsMap);
        assertThat(tags).containsExactlyInAnyOrderEntriesOf(setTagsMap);

        verify(tagRepository).saveAll(Set.of(keepTag));
        verify(mappingService).associate(entity, Set.of());
    }
}
