package br.com.hossomi.sample.todohistory.controller.user;

import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import br.com.hossomi.sample.todohistory.model.User;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import br.com.hossomi.sample.todohistory.service.TagService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.StreamSupport.stream;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;
    private final TagService tagService;

    @PostMapping
    @Transactional
    public UserDto create(@RequestBody CreateUserRequest request) {
        User user = userRepo.save(User.builder()
                .name(request.name())
                .build());
        UserDto.Builder dto = convert(user);

        if (request.tags() != null) { dto.tags(tagService.setTags(user, request.tags())); }
        return dto.build();
    }

    @GetMapping
    public Iterable<UserDto> list() {
        return stream(userRepo.findAll().spliterator(), false)
                .map(UserController::convert)
                .map(UserDto.Builder::build)
                .toList();
    }

    @GetMapping("/{userId}")
    @Transactional
    public UserDto get(@PathVariable("userId") Long userId) {
        return convert(userRepo.findById(userId).orElseThrow()).build();
    }

    @PutMapping("/{userId}")
    @Transactional
    public UserDto update(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserRequest request) {
        User user = userRepo.findById(userId).orElseThrow();
        if (request.name() != null) { user.name(request.name()); }

        UserDto.Builder dto = convert(userRepo.save(user));

        if (request.tags() != null) { dto.tags(tagService.setTags(user, request.tags())); }
        return dto.build();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userRepo.deleteById(userId);
    }

    private static UserDto.Builder convert(User user) {
        return UserDto.builder()
                .id(user.id())
                .name(user.name());
    }
}
