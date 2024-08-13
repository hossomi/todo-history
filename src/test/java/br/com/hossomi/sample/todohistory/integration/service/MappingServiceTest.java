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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Collections2.filter;
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

    private ParentEntity parentA = new ParentEntity();
    private ChildEntity childA1 = new ChildEntity();
    private ChildEntity childA2 = new ChildEntity();
    private ParentEntity parentB = new ParentEntity();
    private ChildEntity childB1 = new ChildEntity();
    private ChildEntity childB2 = new ChildEntity();

    private MappingService mappingService;
    private Collection<Mapping> mappingsA;
    private Collection<Mapping> mappingsB;

    @BeforeEach
    void setup() {
        parentA = parentRepository.save(parentA);
        childA1 = childRepository.save(childA1);
        childA2 = childRepository.save(childA2);
        parentB = parentRepository.save(parentB);
        childB1 = childRepository.save(childB1);
        childB2 = childRepository.save(childB2);

        mappingService = new MappingService(mappingRepository, entityManager);
        mappingsA = mappingService.associate(parentA, List.of(childA1, childA2));
        mappingsB = mappingService.associate(parentB, List.of(childB1, childB2));
    }

    @Test
    void associateCreatesMappings() {
        // Associations created on setup
        assertThat(mappingRepository.findAll())
                .hasSize(4)
                .containsAll(mappingsA)
                .containsAll(mappingsB);
    }

    @Test
    void dissociateDeletesMappingsForSingleChild() {
        mappingService.dissociate(parentA, List.of(childA1));

        assertThat(mappingRepository.findAll())
                .hasSize(3)
                .containsAll(filter(mappingsA, m -> !Objects.equals(m.childId(), childA1.id())))
                .containsAll(mappingsB);
    }

    @Test
    void dissociateDeletesMappingsForMultipleChildren() {
        mappingService.dissociate(parentA, List.of(childA1, childA2));

        assertThat(mappingRepository.findAll())
                .hasSize(2)
                .doesNotContainAnyElementsOf(mappingsA)
                .containsAll(mappingsB);
    }

    @Test
    void dissociateDoesNotDeleteMappingsForUnrelatedChild() {
        mappingService.dissociate(parentA, List.of(childB1, childB2));

        assertThat(mappingRepository.findAll())
                .hasSize(4)
                .containsAll(mappingsA)
                .containsAll(mappingsB);
    }

    @Test
    void findChildrenReturnsChildren() {
        assertThat(mappingService.findChildren(parentA, ChildEntity.class))
                .containsExactlyInAnyOrder(childA1, childA2);
    }

    @Test
    void findChildrenDoesNotReturnOtherChildType() {
        assertThat(mappingService.findChildren(parentA, ParentEntity.class)).isEmpty();
    }
}
