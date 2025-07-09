package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequestDto itemRequestDto) {

        ItemRequest itemRequest = ItemRequestMapper.toModel(itemRequestDto);

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public Optional<ItemRequest> getRequest(Long itemRequestId) {

        return itemRequestRepository.findById(itemRequestId);
    }

    @Override
    public List<ItemRequest> getAllRequests() {

        return itemRequestRepository.findAll();
    }

    @Override
    public void deleteRequest(Long itemRequestId) {

        itemRequestRepository.deleteById(itemRequestId);

    }

}
