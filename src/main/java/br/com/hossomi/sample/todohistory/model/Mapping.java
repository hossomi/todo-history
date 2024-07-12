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
    private Long id;

    @Column(nullable = false)
    private Class<? extends BaseEntity> parentType;
    @Column(nullable = false)
    private Long parentId;
    @Column(nullable = false)
    private Class<? extends BaseEntity> childType;
    @Column(nullable = false)
    private Long childId;

    public static Mapping create(BaseEntity parent, BaseEntity child) {
        return Mapping.builder()
                .parentType(parent.getClass())
                .parentId(parent.getId())
                .childType(child.getClass())
                .childId(child.getId())
                .build();
    }
}
