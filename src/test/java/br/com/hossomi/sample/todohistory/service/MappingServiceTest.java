package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.MMapping;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.test.TestEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class MappingServiceTest {

    @Mock
    private MappingRepository mappingRepository;
    @Mock
    private EntityManager entityManager;

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
                MMapping.create(parent, child1),
                MMapping.create(parent, child2)));
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
