package br.com.hossomi.sample.todohistory.repository;

import br.com.hossomi.sample.todohistory.model.GenericEntity;
import br.com.hossomi.sample.todohistory.model.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface MappingRepository extends JpaRepository<Mapping, Long>, JpaSpecificationExecutor<Mapping> {

    @Modifying
    @Query("""
            delete from Mapping m
            where m.childType = :childType
                and m.childId in (:childId)
            """)
    void deleteByChildren(Class<? extends GenericEntity> childType, Collection<Long> childId);
}
