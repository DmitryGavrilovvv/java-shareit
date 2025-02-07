package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto dto) {
        checkEmail(dto);
        log.debug("Добавление нового пользователя с именем: {}", dto.getName());
        User user = UserMapper.mapToUser(dto);
        return UserMapper.mapToUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto dto) {
        checkId(userId);
        User user = userRepository.getUserById(userId);
        log.debug("Обновление пользователя с id = {}", userId);
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            checkEmail(dto);
            user.setEmail(dto.getEmail());
        }
        return UserMapper.mapToUserDto(userRepository.updateUser(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        checkId(id);
        log.debug("Получение пользователя с id = {}", id);
        return UserMapper.mapToUserDto(userRepository.getUserById(id));
    }

    @Override
    public void deleteUser(Long id) {
        checkId(id);
        log.debug("Удаление пользователя с id = {}", id);
        userRepository.deleteUserById(id);
    }

    private void checkId(Long id) {
        if (id == null) {
            log.error("id пользователя не указан");
            throw new ValidationException("id должен быть указан");
        }
    }

    private void checkEmail(UserDto dto) {
        String email = dto.getEmail();
        for (User user : userRepository.getAllUsers()) {
            if (user.getEmail().equals(email)) {
                log.error("E-mail = {}, уже присутствует у другого пользователя", email);
                throw new ShareItException(ShareItExceptionCodes.DUPLICATE_EMAIL, email);
            }
        }
    }
}
