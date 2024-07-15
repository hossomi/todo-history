package br.com.hossomi.sample.todohistory.model;

import com.google.common.collect.Maps;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.hibernate.envers.Audited;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    private Collection<Tag> tags;

    public void setTags(Map<String, String> tags) {
        var currentTags = Maps.uniqueIndex(this.tags, Tag::getName);
        this.tags = tags.entrySet().stream()
                .map(e -> {
                    Tag currentTag = currentTags.get(e.getKey());
                    if (currentTag == null) return Tag.create(e.getKey(), e.getValue());
                    currentTag.setValue(e.getValue());
                    return currentTag;
                })
                .collect(toList());
    }
}
