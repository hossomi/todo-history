package br.com.hossomi.sample.todohistory.controller.user;

import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.User;
import br.com.hossomi.sample.todohistory.model.MUser;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.service.TagService;
import br.com.hossomi.sample.todohistory.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TagService tagService;

    @PostMapping
    public User create(@RequestBody CreateUserRequest request) {
        return convert(userService.create(MUser.builder()
                        .name(request.name())
                        .build(),
                request.tags()));
    }

    @GetMapping
    public List<User> list() {
        return userService.find().stream()
                .map(UserController::convert)
                .toList();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable("userId") Long userId) {
        return convert(userService.find(userId).orElseThrow());
    }

    @PutMapping("/{userId}")
    @Transactional
    public User update(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserRequest request) {
        return convert(userService.update(userId, MUser.builder()
                .name(request.name())
                .tags(Tag.fromMap(request.tags()))
                .build()));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

    private static User convert(MUser user) {
        return User.builder()
                .id(user.id())
                .name(user.name())
                .tags(Tag.toMap(user.tags()))
                .build();
    }
}
