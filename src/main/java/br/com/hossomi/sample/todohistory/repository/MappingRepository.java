package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends CrudRepository<Mapping, Long> {
}
