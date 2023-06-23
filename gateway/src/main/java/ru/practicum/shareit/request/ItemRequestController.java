package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getItemRequestByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequestByPage(@RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllItemRequestByPage(from, size, userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return requestClient.getItemRequest(userId, requestId);
    }
}