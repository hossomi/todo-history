package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mapping {

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

    public static Mapping create(GenericEntity parent, GenericEntity child) {
        return Mapping.builder()
                .parentType(parent.getClass())
                .parentId(parent.id())
                .childType(child.getClass())
                .childId(child.id())
                .build();
    }
}
