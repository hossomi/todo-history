package br.com.hossomi.sample.todohistory;

import br.com.hossomi.sample.todohistory.controller.item.ItemController;
import br.com.hossomi.sample.todohistory.controller.item.model.CreateItemRequest;
import br.com.hossomi.sample.todohistory.controller.item.model.ItemDto;
import br.com.hossomi.sample.todohistory.controller.item.model.UpdateItemRequest;
import br.com.hossomi.sample.todohistory.controller.user.UserController;
import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import br.com.hossomi.sample.todohistory.model.Item;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.model.Tag;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.google.common.collect.Collections2.transform;

@Slf4j
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class MainTest {

    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void test() {
        UserDto user1 = userController.create(CreateUserRequest.builder()
                .name("Marcelo")
                .build());
        UserDto user2 = userController.create(CreateUserRequest.builder()
                .name("Hossomi")
                .build());

        ItemDto item = itemController.create(CreateItemRequest.builder()
                .name("Chores")
                .assigneeId(user1.id())
                .build());

        log.info("\nUser 1: {}\nUser 2: {}\nItem:   {}\n", user1, user2, item);

        user1 = userController.update(user1.id(), UpdateUserRequest.builder()
                .tags(Map.of("class", "owner"))
                .build());
        user2 = userController.update(user2.id(), UpdateUserRequest.builder()
                .tags(Map.of("class", "visitor"))
                .build());
        item = itemController.update(item.id(), UpdateItemRequest.builder()
                .tags(Map.of(
                        "target", "house",
                        "actor", "person",
                        "state", "ready",
                        "deadline", "tomorrow"))
                .build());

        log.info("\nUser 1: {}\nUser 2: {}\nItem:   {}\n", user1, user2, item);

        int checkpointRev;
        try (var em = entityManagerFactory.createEntityManager()) {
            AuditReader reader = AuditReaderFactory.get(em);
            //noinspection deprecation
            checkpointRev = reader.getCurrentRevision(DefaultRevisionEntity.class, true).getId();
        }
        log.info("Checkpoint revision: {}", checkpointRev);

        item = itemController.update(item.id(), UpdateItemRequest.builder()
                .assigneeId(user2.id())
                .build());
        item = itemController.update(item.id(), UpdateItemRequest.builder()
                .tags(Map.of(
                        "target", "house",
                        "actor", "person",
                        "state", "done"))
                .build());

        log.info("\nUser 1: {}\nUser 2: {}\nItem:   {}\n", user1, user2, item);

        user1 = userController.update(user1.id(), UpdateUserRequest.builder()
                .name("New Marcelo")
                .build());
        user2 = userController.update(user2.id(), UpdateUserRequest.builder()
                .name("New Hossomi")
                .build());
        item = itemController.get(item.id());

        log.info("\nUser 1: {}\nUser 2: {}\nItem:   {}\n", user1, user2, item);

        try (var em = entityManagerFactory.createEntityManager()) {
            AuditReader reader = AuditReaderFactory.get(em);
            Item checkpointItem = reader.find(Item.class, item.id(), checkpointRev);
            System.out.println(checkpointItem);

            @SuppressWarnings("unchecked")
            List<Mapping> tagMappings = reader.createQuery().forEntitiesAtRevision(Mapping.class, checkpointRev)
                    .add(AuditEntity.property("parentType").eq(Item.class))
                    .add(AuditEntity.property("parentId").eq(item.id()))
                    .add(AuditEntity.property("childType").eq(Tag.class))
                    .getResultList();

            @SuppressWarnings("unchecked")
            List<Tag> tags = reader.createQuery().forEntitiesAtRevision(Tag.class, checkpointRev)
                    .add(AuditEntity.id().in(transform(tagMappings, Mapping::getChildId)))
                    .getResultList();

            System.out.println(tags);
        }
    }
}