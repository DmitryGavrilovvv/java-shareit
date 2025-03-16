package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto dto) {
        log.debug("Добавление нового пользователя с именем: {}", dto.getName());
        checkEmail(dto.getEmail());
        User user = UserMapper.mapToUser(dto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        checkId(userId);
        checkEmail(dto.getEmail());
        User updateUser = UserMapper.mapUserUpdateDtoToUser(dto);
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        if (updateUser.getName() != null && !updateUser.getName().isBlank()) {
            oldUser.setName(updateUser.getName());
        }
        String email = updateUser.getEmail();
        if (email != null && !email.isBlank()) {
            oldUser.setEmail(email);
        }
        log.debug("Обновление пользователя с id = {}", userId);
        return UserMapper.mapToUserDto(userRepository.save(oldUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Получение пользователя с id = {}", id);
        return UserMapper.mapToUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", id))));
    }

    @Override
    public void deleteUser(Long id) {
        checkId(id);
        log.debug("Удаление пользователя с id = {}", id);
        userRepository.deleteById(id);
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("id пользователя не указан");
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    private void checkEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.error("E-mail уже присутствует у другого пользователя");
            throw new DuplicateEmailException("Этот e-mail уже используется");
        }
    }
}
