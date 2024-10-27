package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Entity
@Audited
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MTag implements GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String value;

    public static MTag create(String name, String value) {
        return builder()
                .name(name)
                .value(value)
                .build();
    }

    public static MTag create(Map.Entry<String, String> entry) {
        return builder()
                .name(entry.getKey())
                .value(entry.getValue())
                .build();
    }

    public static Map<String, String> toMap(Collection<MTag> tags) {
        return tags != null
                ? tags.stream()
                .collect(Collectors.toMap(MTag::name, MTag::value, (t1, t2) -> {
                    log.warn("Duplicated tag name (first will be used): {} / {}", t1, t2);
                    return t1;
                }))
                : null;
    }

    public static Set<MTag> fromMap(Map<String, String> tags) {
        return tags != null
                ? tags.entrySet().stream().map(MTag::create).collect(toSet())
                : null;
    }

    public static Specification<MTag> nameIsAnyOf(Collection<String> names) {
        return (tag, q, c) -> tag.get("name").in(names);
    }
}
