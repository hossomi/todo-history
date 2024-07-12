package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Item {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    private User assignee;
}
