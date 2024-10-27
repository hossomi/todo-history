package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

import static java.util.stream.Collectors.*;
import static org.springframework.data.jpa.domain.Specification.anyOf;

@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column
    private Class<? extends GenericEntity> parentType;

    @Column
    private Long parentId;

    @Column
    private Class<? extends GenericEntity> childType;

    @Column
    private Long childId;

    public static MMapping create(GenericEntity parent, GenericEntity child) {
        return MMapping.builder()
                .parentType(parent.getClass())
                .parentId(parent.id())
                .childType(child.getClass())
                .childId(child.id())
                .build();
    }

    /**
     * Groups provided children by type {@code childType} and their respective {@code childrenId}.
     * Then, applies {@link #isParentOfAny(Class, Collection)} to each group and applies OR to all conditions.
     */
    public static Specification<MMapping> isParentOfAny(Collection<? extends GenericEntity> children) {
        var childrenIdByType = children.stream().collect(groupingBy(
                child -> child.getClass(),
                mapping(GenericEntity::id, toList())));
        return anyOf(childrenIdByType.entrySet().stream()
                .map(e -> isParentOfAny(e.getKey(), e.getValue()))
                .collect(toList()));
    }

    /**
     * Returns the condition:
     * <pre>
     *     childType = {childType} AND childId IN {childrenId}
     * </pre>
     */
    private static <T extends GenericEntity> Specification<MMapping> isParentOfAny(Class<T> childType, Collection<Long> childrenId) {
        return (mapping, query, criteria) -> criteria.and(
                criteria.equal(mapping.get("childType"), childType),
                mapping.get("childId").in(childrenId));
    }

    /**
     * Returns the condition:
     * <pre>
     *     parentType = {parentType} AND parentId = {parentId}
     * </pre>
     */
    public static Specification<MMapping> mapsToParent(GenericEntity parent) {
        return (mapping, query, criteria) -> criteria.and(
                criteria.equal(mapping.get("parentType"), parent.getClass()),
                criteria.equal(mapping.get("parentId"), parent.id()));
    }

    public static <T extends GenericEntity> Specification<MMapping> mapsToChild(Root<T> child) {
        return (mapping, query, criteria) -> criteria.and(
                criteria.equal(mapping.get("childType"), child.getJavaType()),
                criteria.equal(mapping.get("childId"), child.get("id")));
    }

    public static <T extends GenericEntity> Specification<MMapping> mapsParentAndChild(GenericEntity parent, Root<T> child) {
        return mapsToParent(parent).and(mapsToChild(child));
    }

    public static <T extends GenericEntity> Predicate isChildOf(Root<MMapping> mapping, Root<T> child, CriteriaBuilder criteria) {
        return criteria.and(
                criteria.equal(mapping.get("childType"), child.getJavaType()),
                criteria.equal(mapping.get("childId"), child.get("id")));
    }
}
