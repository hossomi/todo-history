package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
}
