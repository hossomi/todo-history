package br.com.hossomi.sample.todohistory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Data
public class Item {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    private User assignee;
}
