package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.MUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<MUser, Long> {
}
