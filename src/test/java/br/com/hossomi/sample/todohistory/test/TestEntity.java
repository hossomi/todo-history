package br.com.hossomi.sample.todohistory.test;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestEntity implements GenericEntity {

    @Id
    private Long id;

}
