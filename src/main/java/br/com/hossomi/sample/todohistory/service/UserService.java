package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import br.com.hossomi.sample.todohistory.model.User;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@AllArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final TagService tagService;

    @Transactional
    public User create(User user) {
        return userRepo.save(user);
    }

    public Collection<User> find() {
        return newArrayList(userRepo.findAll());
    }

    @Transactional
    public Optional<User> find(long userId) {
        return userRepo.findById(userId);
    }

    @Transactional
    public User update(long userId, User update) {
        User user = find(userId).orElseThrow();
        if (update.name() != null) { user.name(update.name()); }
        // if (update.getTags() != null) { tagService.setTags(user, update.getTags()); }
        return user;
    }

    public void delete(Long userId) {
        userRepo.deleteById(userId);
    }

    private static UserDto convert(User user) {
        return UserDto.builder()
                .id(user.id())
                .name(user.name())
                // .tags(Tag.toMap(user.getTags()))
                .build();
    }
}
