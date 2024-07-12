package br.com.hossomi.sample.todohistory.controller.user;

import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.model.User;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.StreamSupport.stream;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;

    @PostMapping
    public User create(@RequestBody CreateUserRequest request) {
        return users.save(User.builder()
                .name(request.name())
                .build());
    }

    @GetMapping
    public Iterable<User> list() {
        return stream(users.findAll().spliterator(), false).toList();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable("userId") Long userId) {
        return users.findById(userId).orElseThrow();
    }

    @PutMapping("/{userId}")
    @Transactional
    public User update(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserRequest request) {
        User user = users.findById(userId).orElseThrow();
        if (request.name() != null) user.setName(request.name());
        return users.save(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        users.deleteById(userId);
    }
}
