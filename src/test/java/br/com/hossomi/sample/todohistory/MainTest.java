package br.com.hossomi.sample.todohistory;

import br.com.hossomi.sample.todohistory.controller.item.ItemController;
import br.com.hossomi.sample.todohistory.controller.item.model.CreateItemRequest;
import br.com.hossomi.sample.todohistory.controller.item.model.ItemDto;
import br.com.hossomi.sample.todohistory.controller.item.model.UpdateItemRequest;
import br.com.hossomi.sample.todohistory.controller.user.UserController;
import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.model.Item;
import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class MainTest {

    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;

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

        log.info("===> {}\n", userController.get(user1.id()));
        log.info("===> {}\n", userController.get(user2.id()));
        log.info("===> {}\n", itemController.get(item.id()));

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

        log.info("===> {}\n", userController.get(user1.id()));
        log.info("===> {}\n", userController.get(user2.id()));
        log.info("===> {}\n", itemController.get(item.id()));

        item = itemController.update(item.id(), UpdateItemRequest.builder()
                .assigneeId(user2.id())
                .build());
        item = itemController.update(item.id(), UpdateItemRequest.builder()
                .tags(Map.of(
                        "target", "house",
                        "actor", "person",
                        "state", "done"))
                .build());

        log.info("===> {}\n", userController.get(user1.id()));
        log.info("===> {}\n", userController.get(user2.id()));
        log.info("===> {}\n", itemController.get(item.id()));
    }
}