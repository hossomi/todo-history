package br.com.hossomi.sample.todohistory.controller.item;

import br.com.hossomi.sample.todohistory.controller.item.model.CreateItemRequest;
import br.com.hossomi.sample.todohistory.controller.item.model.UpdateItemRequest;
import br.com.hossomi.sample.todohistory.model.Item;
import br.com.hossomi.sample.todohistory.repository.ItemRepository;
import br.com.hossomi.sample.todohistory.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.StreamSupport.stream;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemRepository items;
    private final UserRepository users;

    @PostMapping
    public Item create(@RequestBody CreateItemRequest request) {
        Item.ItemBuilder item = Item.builder()
                .name(request.name());

        if (request.assigneeId() != null) {
            item.assignee(users.findById(request.assigneeId()).orElseThrow());
        }

        return items.save(item.build());
    }

    @GetMapping
    public Iterable<Item> list() {
        return stream(items.findAll().spliterator(), false).toList();
    }

    @GetMapping("/{itemId}")
    public Item get(@PathVariable("itemId") Long itemId) {
        return items.findById(itemId).orElseThrow();
    }

    @PutMapping("/{itemId}")
    @Transactional
    public Item update(
            @PathVariable("itemId") Long itemId,
            @RequestBody UpdateItemRequest request) {
        Item item = items.findById(itemId).orElseThrow();
        if (request.name() != null) item.setName(request.name());
        if (request.assigneeId() != null) item.setAssignee(users.findById(request.assigneeId()).orElseThrow());
        return items.save(item);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable("itemId") Long itemId) {
        items.deleteById(itemId);
    }
}
