package br.com.hossomi.sample.todohistory.controller.user;

import br.com.hossomi.sample.todohistory.controller.user.model.CreateUserRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UpdateUserRequest;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.model.User;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.StreamSupport.stream;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final MappingRepository mappingRepo;

    @PostMapping
    public User create(@RequestBody CreateUserRequest request) {
        return userRepo.save(User.builder()
                .name(request.name())
                .build());
    }

    @GetMapping
    public Iterable<User> list() {
        return stream(userRepo.findAll().spliterator(), false).toList();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable("userId") Long userId) {
        return userRepo.findById(userId).orElseThrow();
    }

    @PutMapping("/{userId}")
    @Transactional
    public User update(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserRequest request) {
        User user = userRepo.findById(userId).orElseThrow();
        if (request.name() != null) { user.setName(request.name()); }
        if (request.tags() != null) {
            // TODO: fix this
            // Iterable<Tag> tags = tagRepo.saveAll(request);
            // mappingRepo.saveAll(stream(tags.spliterator(), false)
            //         .map(tag -> Mapping.create(item, tag))
            //         .toList());
        }
        return userRepo.save(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userRepo.deleteById(userId);
    }
}
