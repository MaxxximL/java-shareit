package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {

        return UserMapper.toDto(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.toDto(userService.update(userDto));
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {

        return UserMapper.toDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {

        return UserMapper.toDtoList(userService.getAll());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {

        userService.delete(userId);
    }
}