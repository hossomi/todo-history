package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import br.com.hossomi.sample.todohistory.model.MMapping;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

import static br.com.hossomi.sample.todohistory.model.MMapping.*;
import static com.google.common.collect.Sets.newHashSet;

@Service
@AllArgsConstructor
public class MappingService {

    private final MappingRepository mappingRepository;
    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public Collection<MMapping> associate(GenericEntity parent, Collection<? extends GenericEntity> children) {
        if (children == null || children.isEmpty()) { return null; }
        return mappingRepository.saveAll(children.stream()
                .map(child -> create(parent, child))
                .toList());
    }

    @Transactional
    public void dissociate(GenericEntity parent, Collection<? extends GenericEntity> children) {
        if (children == null || children.isEmpty()) { return; }
        mappingRepository.delete(mapsToParent(parent).and(mapsToAnyChild(children)));
    }

    @Transactional
    public <T extends GenericEntity> void dissociate(GenericEntity parent, Class<T> childType, Specification<T> specification) {
        Set<MMapping> mappings = newHashSet(findMappingForChildren(parent, childType, specification));
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaDelete<MMapping> query = criteria.createCriteriaDelete(MMapping.class);
        Root<MMapping> mapping = query.from(MMapping.class);

        em.createQuery(query.where(mapping.in(mappings)))
                .executeUpdate();
    }

    private <T extends GenericEntity> Iterable<MMapping> findMappingForChildren(GenericEntity parent, Class<T> childType, Specification<T> specification) {
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<MMapping> query = criteria.createQuery(MMapping.class);
        Root<MMapping> mapping = query.from(MMapping.class);
        Root<T> child = query.from(childType);

        return em.createQuery(query.select(mapping).where(
                        mapsParentAndChild(parent, child).toPredicate(mapping, query, criteria),
                        specification.toPredicate(child, query, criteria)))
                .getResultList();
    }

    public <T extends GenericEntity> Iterable<T> findChildren(GenericEntity parent, Class<T> childType) {
        return findChildren(parent, childType, (child, query, criteria) -> criteria.conjunction());
    }

    public <T extends GenericEntity> Iterable<T> findChildren(GenericEntity parent, Class<T> childType, Specification<T> specification) {
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<T> query = criteria.createQuery(childType);
        Root<MMapping> mapping = query.from(MMapping.class);
        Root<T> child = query.from(childType);

        return em.createQuery(query.select(child).where(
                        mapsParentAndChild(parent, child).toPredicate(mapping, query, criteria),
                        specification.toPredicate(child, query, criteria)))
                .getResultList();
    }
}
