package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.BaseEntity;
import br.com.hossomi.sample.todohistory.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Query("""
            select t
            from Mapping m
            join Tag t on t.id = m.childId
            where m.parentType = :parentType
                and m.parentId = :parentId
                and m.childType = 'br.com.hossomi.sample.todohistory.model.Tag'
            """)
    Iterable<Tag> findByParent(Class<? extends BaseEntity> parentType, Long parentId);
}
