package br.com.hossomi.sample.todohistory.integration.service;

import br.com.hossomi.sample.todohistory.model.MMapping;
import br.com.hossomi.sample.todohistory.model.MTag;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import br.com.hossomi.sample.todohistory.service.MappingService;
import br.com.hossomi.sample.todohistory.service.TagService;
import br.com.hossomi.sample.todohistory.test.ParentEntity;
import br.com.hossomi.sample.todohistory.test.ParentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class TagServiceTest {

    private static final Map<String, String> TAGS = Map.of(
            "A", "1",
            "B", "2",
            "C", "3");

    @Autowired
    private MappingRepository mappingRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ParentRepository parentRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private ParentEntity entity = new ParentEntity();

    private TagService tagService;

    @BeforeEach
    void setup() {
        MappingService mappingService = new MappingService(mappingRepository, entityManager);
        tagService = new TagService(tagRepository, mappingService);

        entity = parentRepository.save(entity);
        tagService.setTags(entity, TAGS);
    }

    @Test
    void setTagCreatesTags() {
        // Tags created on setup
        assertThat(tagRepository.findAll()).containsExactlyInAnyOrderElementsOf(MTag.fromMap(TAGS));
        assertTagsAreAssociated();
    }

    @Test
    void setTagDeletesMissingTags() {
        Map<String, String> newTags = tagService.setTags(entity, Map.of(
                "A", "1",
                "B", "2"));
        assertThat(tagRepository.findAll()).containsExactlyInAnyOrderElementsOf(MTag.fromMap(newTags));
        assertTagsAreAssociated();
    }

    private void assertTagsAreAssociated() {
        List<MMapping> mappings = tagRepository.findAll().stream()
                .map(t -> MMapping.create(entity, t))
                .toList();
        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(mappings);
    }
}
