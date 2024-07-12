package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
public class Mapping {

    @Id
    private Long id;

    private Class<?> parentType;
    private Long parentId;
    private Class<?> childType;
    private Long childId;
}
