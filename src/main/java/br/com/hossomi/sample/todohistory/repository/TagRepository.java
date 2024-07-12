package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
}
