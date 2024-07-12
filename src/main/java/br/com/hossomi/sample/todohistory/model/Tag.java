package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
public class Tag {

    @Id
    private Long id;

    private String name;
    private String value;
}
