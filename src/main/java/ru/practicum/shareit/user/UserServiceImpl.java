package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }

        User user = UserMapper.toModel(userDto);
        validateEmail(user.getEmail(), null);
        return userRepository.save(user);
    }

    @Override
    public User update(UserDto userDto) {
        User existingUser = getById(userDto.getId());

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            validateEmail(userDto.getEmail(), userDto.getId());
            existingUser.setEmail(userDto.getEmail());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long userId) {

        userRepository.deleteById(userId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email");
        }
        validateEmail(user.getEmail(), null);
    }

    private void validateEmail(String email, Long userId) {
        userRepository.findByEmail(email)
                .ifPresent(u -> {
                    if (userId == null || !u.getId().equals(userId)) {
                        throw new ConflictException("Email already exists");
                    }
                });
    }
}