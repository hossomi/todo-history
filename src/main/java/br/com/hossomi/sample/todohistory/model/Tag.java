package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Tag {

    @Id
    private Long id;

    private String name;
    private String value;
}
