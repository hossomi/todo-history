package br.com.hossomi.sample.todohistory.service;

import br.com.hossomi.sample.todohistory.controller.user.model.User;
import br.com.hossomi.sample.todohistory.model.MUser;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final TagService tagService;

    @Transactional
    public MUser create(MUser user, Map<String, String> tags) {
        user = userRepo.save(user);

        if (tags != null) {
            user.tags(tagService.setTags(user, tags));
        }

        return user;
    }

    public Collection<MUser> find() {
        return newArrayList(userRepo.findAll());
    }

    @Transactional
    public Optional<MUser> find(long userId) {
        return userRepo.findById(userId);
    }

    @Transactional
    public MUser update(long userId, MUser update) {
        MUser user = find(userId).orElseThrow();
        if (update.name() != null) { user.name(update.name()); }
        // if (update.getTags() != null) { tagService.setTags(user, update.getTags()); }
        return user;
    }

    public void delete(Long userId) {
        userRepo.deleteById(userId);
    }

    private static User convert(MUser user) {
        return User.builder()
                .id(user.id())
                .name(user.name())
                // .tags(Tag.toMap(user.getTags()))
                .build();
    }
}
