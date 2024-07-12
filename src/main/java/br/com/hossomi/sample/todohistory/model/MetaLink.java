package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class MetaLink {

    @Id
    private Long id;

    private Class<?> parentType;
    private Long parentId;
    private Class<?> childType;
    private Long childId;
}
