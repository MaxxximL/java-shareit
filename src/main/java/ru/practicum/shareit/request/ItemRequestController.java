package ru.practicum.shareit.request;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/requests")
@Transactional(readOnly = true)
public class ItemRequestController {

    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto) {

        return ItemRequestMapper.toDto(itemRequestService.createRequest(itemRequestDto));

    }

    @GetMapping("/{itemRequestId}")
    public Optional<ItemRequest> getRequest(@PathVariable Long itemRequestId) {

        return itemRequestService.getRequest(itemRequestId);

    }

    @GetMapping
    public List<ItemRequest> getAllRequests() {

        return itemRequestService.getAllRequests();
    }

    @DeleteMapping("/{itemRequestId}")
    public void deleteRequest(@PathVariable Long itemRequestId) {

        itemRequestService.deleteRequest(itemRequestId);
    }

}
