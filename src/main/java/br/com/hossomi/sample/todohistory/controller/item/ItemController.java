package br.com.hossomi.sample.todohistory.controller.item;

import br.com.hossomi.sample.todohistory.controller.item.model.CreateItemRequest;
import br.com.hossomi.sample.todohistory.controller.item.model.UpdateItemRequest;
import br.com.hossomi.sample.todohistory.model.Item;
import br.com.hossomi.sample.todohistory.model.Mapping;
import br.com.hossomi.sample.todohistory.model.Tag;
import br.com.hossomi.sample.todohistory.repository.ItemRepository;
import br.com.hossomi.sample.todohistory.repository.MappingRepository;
import br.com.hossomi.sample.todohistory.repository.TagRepository;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.stream.StreamSupport.stream;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final MappingRepository mappingRepo;

    @PostMapping
    public Item create(@RequestBody CreateItemRequest request) {
        Item.ItemBuilder item = Item.builder()
                .name(request.name());

        if (request.assigneeId() != null) {
            item.assignee(userRepo.findById(request.assigneeId()).orElseThrow());
        }

        return itemRepo.save(item.build());
    }

    @GetMapping
    public Iterable<Item> list() {
        return stream(itemRepo.findAll().spliterator(), false).toList();
    }

    @GetMapping("/{itemId}")
    public Item get(@PathVariable("itemId") Long itemId) {
        return itemRepo.findById(itemId).orElseThrow();
    }

    @PutMapping("/{itemId}")
    @Transactional
    public Item update(
            @PathVariable("itemId") Long itemId,
            @RequestBody UpdateItemRequest request) {
        Item item = itemRepo.findById(itemId).orElseThrow();
        if (request.name() != null) item.setName(request.name());
        if (request.assigneeId() != null) item.setAssignee(userRepo.findById(request.assigneeId()).orElseThrow());
        return itemRepo.save(item);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable("itemId") Long itemId) {
        itemRepo.deleteById(itemId);
    }

    @Transactional
    @PostMapping("/{itemId}")
    public Item tag(@PathVariable("itemId") Long itemId, @RequestBody Map<String,String> request) {
        Item item = itemRepo.findById(itemId).orElseThrow();
        Iterable<Tag> tags = tagRepo.saveAll(request);
        mappingRepo.saveAll(stream(tags.spliterator(), false)
                .map(tag -> Mapping.create(item, tag))
                .toList());
        return item;
    }
}
