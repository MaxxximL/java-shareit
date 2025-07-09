package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {

  ItemRequest createRequest(ItemRequestDto itemRequestDto);

  Optional<ItemRequest> getRequest(Long itemRequestId);

  List<ItemRequest> getAllRequests();

  void deleteRequest(Long itemRequestId);

}
