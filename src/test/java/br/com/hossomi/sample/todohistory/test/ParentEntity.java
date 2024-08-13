package br.com.hossomi.sample.todohistory.test;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ParentEntity implements GenericEntity {

    @Id
    @GeneratedValue
    private Long id;

}
