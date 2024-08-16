package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag implements GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String value;

    public static Tag create(String name, String value) {
        return builder()
                .name(name)
                .value(value)
                .build();
    }

    public static Tag create(Map.Entry<String, String> entry) {
        return builder()
                .name(entry.getKey())
                .value(entry.getValue())
                .build();
    }

    public static Map<String, String> toMap(Collection<Tag> tags) {
        return tags != null
                ? tags.stream().collect(Collectors.toMap(Tag::name, Tag::value))
                : null;
    }

    public static Collection<Tag> fromMap(Map<String, String> tags) {
        return tags != null
                ? tags.entrySet().stream().map(Tag::create).collect(toList())
                : null;
    }
}
