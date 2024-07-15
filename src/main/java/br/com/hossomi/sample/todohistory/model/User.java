package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @JoinTable(
            name = "Mapping",
            joinColumns = @JoinColumn(name = "parentId"),
            inverseJoinColumns = @JoinColumn(name = "childId"))
    @SQLJoinTableRestriction("`parentType` = 'br.com.hossomi.sample.todohistory.model.User'")
    @OneToMany
    private Set<Tag> tags;
}
