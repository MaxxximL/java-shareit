package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User create(UserDto userDto);

    User update(UserDto userDto);

    User getById(Long userId);

    List<User> getAll();

    void delete(Long userId);

}
