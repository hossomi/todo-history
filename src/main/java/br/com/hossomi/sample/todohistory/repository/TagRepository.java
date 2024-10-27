package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.MTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<MTag, Long>, JpaSpecificationExecutor<MTag> {

}
