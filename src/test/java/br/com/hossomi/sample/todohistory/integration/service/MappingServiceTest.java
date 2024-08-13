package br.com.hossomi.sample.todohistory.integration.service;

import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.service.MappingService;
import br.com.hossomi.sample.todohistory.test.ChildEntity;
import br.com.hossomi.sample.todohistory.test.ChildRepository;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class MappingServiceTest {

    @Autowired
    private MappingRepository mappingRepository;
    @Autowired
    private ChildRepository childRepository;
    @Autowired
    private ParentRepository parentRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private MappingService mappingService;

    @BeforeEach
    void setup() {
        mappingService = new MappingService(mappingRepository, entityManager);
    }

    @Test
    void associateCreatesMappings() {
        Family family = createFamily(2);
        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(family.mappings());
    }

    @Test
    void dissociateDeletesMappingsForSingleChild() {
        Family family1 = createFamily(2);
        Family family2 = createFamily(2);

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(allOf(
                family1.mappings(),
                family2.mappings()));

        mappingService.dissociate(family1.parent(), List.of(family1.children().get(0)));

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(allOf(
                family1.mappings().subList(1, family1.mappings().size()),
                family2.mappings()));
    }

    @Test
    void dissociateDeletesMappingsForMultipleChildren() {
        Family family1 = createFamily(2);
        Family family2 = createFamily(2);

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(allOf(
                family1.mappings(),
                family2.mappings()));

        mappingService.dissociate(family1.parent(), family1.children());

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(family2.mappings());
    }

    @Test
    void dissociateDoesNotDeleteMappingsForUnrelatedChild() {
        Family family1 = createFamily(2);
        Family family2 = createFamily(2);

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(allOf(
                family1.mappings(),
                family2.mappings()));

        mappingService.dissociate(family2.parent(), family1.children());

        assertThat(mappingRepository.findAll()).containsExactlyInAnyOrderElementsOf(allOf(
                family1.mappings(),
                family2.mappings()));
    }

    @Test
    void findChildrenWorks() {
        Family family = createFamily(2);
        assertThat(mappingService.findChildren(family.parent(), ChildEntity.class))
                .containsExactlyInAnyOrderElementsOf(family.children());
    }

    @Test
    void findChildrenDoesNotReturnUnrelatedChildren() {
        Family family1 = createFamily(2);
        createFamily(2);
        assertThat(mappingService.findChildren(family1.parent(), ChildEntity.class))
                .containsExactlyInAnyOrderElementsOf(family1.children());
    }

    @Test
    void findChildrenDoesNotReturnUnrelatedChildType() {
        Family family = createFamily(2);
        assertThat(mappingService.findChildren(family.parent(), ParentEntity.class))
                .isEmpty();
    }

    // Utilities to create parents and children

    private record Family(
            ParentEntity parent,
            List<ChildEntity> children,
            List<Mapping> mappings) { }

    private Family createFamily(int size) {
        ParentEntity parent = parentRepository.save(new ParentEntity());
        List<ChildEntity> children = IntStream.range(0, size)
                .mapToObj(i -> childRepository.save(new ChildEntity()))
                .toList();

        mappingService.associate(parent, children);

        return new Family(parent, children, children.stream()
                .map(child -> Mapping.create(parent, child))
                .toList());
    }

    // Assertion utilities

    private static <T> List<T> allOf(List<T> a, List<T> b) {
        return Stream.concat(a.stream(), b.stream()).toList();
    }
}
