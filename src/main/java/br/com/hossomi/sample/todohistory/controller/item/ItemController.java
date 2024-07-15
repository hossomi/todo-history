package br.com.hossomi.sample.todohistory.controller.item;

import br.com.hossomi.sample.todohistory.controller.item.model.CreateItemRequest;
import br.com.hossomi.sample.todohistory.controller.item.model.ItemDto;
import br.com.hossomi.sample.todohistory.controller.item.model.UpdateItemRequest;
import br.com.hossomi.sample.todohistory.controller.user.model.UserDto;
import br.com.hossomi.sample.todohistory.model.Item;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.repository.ItemRepository;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import br.com.hossomi.sample.todohistory.service.TagService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import static java.util.stream.StreamSupport.stream;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final TagService tagService;

    @PostMapping
    @Transactional
    public ItemDto create(@RequestBody CreateItemRequest request) {
        Item.ItemBuilder item = Item.builder()
                .name(request.name());

        if (request.assigneeId() != null) {
            item.assignee(userRepo.findById(request.assigneeId()).orElseThrow());
        }

        return convert(itemRepo.save(item.build()));
    }

    @GetMapping
    public List<ItemDto> list() {
        return stream(itemRepo.findAll().spliterator(), false)
                .map(ItemController::convert)
                .toList();
    }

    @GetMapping("/{itemId}")
    @Transactional
    public ItemDto get(@PathVariable("itemId") Long itemId) {
        return convert(itemRepo.findById(itemId).orElseThrow());
    }

    @PutMapping("/{itemId}")
    @Transactional
    public ItemDto update(
            @PathVariable("itemId") Long itemId,
            @RequestBody UpdateItemRequest request) {
        Item item = itemRepo.findById(itemId).orElseThrow();
        if (request.name() != null) item.setName(request.name());
        if (request.assigneeId() != null) item.setAssignee(userRepo.findById(request.assigneeId()).orElseThrow());
        if (request.tags() != null) tagService.setTags(item, request.tags());
        return convert(itemRepo.save(item));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable("itemId") Long itemId) {
        itemRepo.deleteById(itemId);
    }

    private static ItemDto convert(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .assignee(UserDto.builder()
                        .id(item.getAssignee().getId())
                        .name(item.getAssignee().getName())
                        .build())
                .tags(Tag.toMap(item.getTags()))
                .build();
    }
}
