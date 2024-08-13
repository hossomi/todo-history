package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.test.TestEntity;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class MappingServiceTest {

    @Mock
    private MappingRepository mappingRepository;
    @Mock
    private EntityManagerFactory entityManagerFactory;
    @Mock
    private EntityManager entityManager;
    @Captor
    private ArgumentCaptor<Specification<Mapping>> specification;

    private final TestEntity parent = new TestEntity(0L);

    private MappingService mappingService;

    @BeforeEach
    void setup() {
        mappingService = new MappingService(mappingRepository, entityManager);
    }

    @Test
    void associateIgnoresNullChildren() {
        mappingService.associate(parent, null);
        verifyNoInteractions(mappingRepository);
    }

    @Test
    void associateIgnoresEmptyChildren() {
        mappingService.associate(parent, List.of());
        verifyNoInteractions(mappingRepository);
    }

    @Test
    void associateCreatesMappingForChildren() {
        TestEntity child1 = new TestEntity(1L);
        TestEntity child2 = new TestEntity(2L);

        mappingService.associate(parent, List.of(child1, child2));
        verify(mappingRepository).saveAll(List.of(
                Mapping.create(parent, child1),
                Mapping.create(parent, child2)));
    }

    @Test
    void dissociateIgnoresNullChildren() {
        mappingService.dissociate(parent, null);
        verifyNoInteractions(mappingRepository);
    }

    @Test
    void dissociateIgnoresEmptyChildren() {
        mappingService.dissociate(parent, List.of());
        verifyNoInteractions(mappingRepository);
    }
}
