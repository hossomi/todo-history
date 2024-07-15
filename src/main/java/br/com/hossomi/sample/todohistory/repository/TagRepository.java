package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    default Iterable<Tag> saveAll(Map<String, String> tags) {
        return saveAll(tags.entrySet().stream()
                .map(e -> Tag.create(e.getKey(), e.getValue()))
                .toList());
    }
}
