package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag implements BaseEntity {

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

    public static Map<String, String> toMap(Collection<Tag> tags) {
        if (tags == null) return null;
        return tags.stream().collect(Collectors.toMap(
                Tag::getName,
                Tag::getValue));
    }
}
