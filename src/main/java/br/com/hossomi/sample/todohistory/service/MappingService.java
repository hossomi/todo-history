package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import com.google.common.collect.Multimaps;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.google.common.collect.Collections2.transform;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class MappingService {

    private final MappingRepository mappingRepository;
    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public void associate(GenericEntity parent, Collection<? extends GenericEntity> children) {
        if (children == null || children.isEmpty()) { return; }
        mappingRepository.saveAll(children.stream()
                .map(child -> Mapping.create(parent, child))
                .toList());
    }

    @Transactional
    public void dissociate(GenericEntity parent, Collection<? extends GenericEntity> children) {
        if (children == null || children.isEmpty()) { return; }
        mappingRepository.delete(hasParent(parent).and(hasAnyOfChildren(children)));
    }

    public <T extends GenericEntity> Iterable<T> findChildren(GenericEntity parent, Class<T> childType) {
        return findChildren(parent, childType, (root, query, criteria) -> null);
    }

    public <T extends GenericEntity> Iterable<T> findChildren(GenericEntity parent, Class<T> childType, Specification<T> specification) {
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<T> query = criteria.createQuery(childType);

        Root<T> child = query.from(childType);
        Root<Mapping> mapping = query.from(Mapping.class);
        return em.createQuery(query.select(child).where(
                        criteria.equal(child.get("id"), mapping.get("childId")),
                        hasParent(parent).toPredicate(mapping, query, criteria),
                        criteria.equal(mapping.get("childType"), childType)))
                // .where(specification.toPredicate(child, query, criteria))
                .getResultList();
    }

    private static Specification<Mapping> hasAnyOfChildren(Collection<? extends GenericEntity> children) {
        var childrenByType = Multimaps.index(children, child -> child.getClass());
        return Specification.anyOf(childrenByType.asMap().entrySet().stream()
                .map(e -> hasAnyOfChildren(e.getKey(), transform(e.getValue(), GenericEntity::id)))
                .collect(toList()));
    }

    private static <T extends GenericEntity> Specification<Mapping> hasAnyOfChildren(Class<T> childType, Collection<Long> childIds) {
        return (root, query, criteria) -> criteria.and(
                criteria.equal(root.get("childType"), childType),
                criteria.in(root.get("childId")).value(childIds));
    }

    public static Specification<Mapping> hasParent(GenericEntity entity) {
        return (root, query, criteria) -> entity != null
                ? criteria.and(
                criteria.equal(root.get("parentType"), entity.getClass()),
                criteria.equal(root.get("parentId"), entity.id()))
                : null;
    }

}
