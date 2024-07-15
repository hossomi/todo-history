package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.BaseEntity;
import br.com.hossomi.sample.todohistory.model.Mapping;
import java.util.Collection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingRepository extends CrudRepository<Mapping, Long> {

    @Modifying
    @Query("""
            delete from Mapping m
            where m.childType = :childType
                and m.childId in (:childId)
            """)
    void deleteByChildren(Class<?extends BaseEntity> childType, Collection<Long> childId);
}
