package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
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
        return userRepository.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto dto) {
        log.debug("Добавление нового пользователя с именем: {}", dto.getName());
        User user = UserMapper.mapToUser(dto);
        if (user.getName().isBlank()) {
            log.error("Имя пользователя не указано");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_NAME);
        }
        if (user.getEmail().isBlank()) {
            log.error("Email пользователя не указано");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_EMAIL);
        }
        return UserMapper.mapToUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        checkId(userId);
        dto.setId(userId);
        log.debug("Обновление пользователя с id = {}", userId);
        return UserMapper.mapToUserDto(userRepository.updateUser(UserMapper.mapUserUpdateDtoToUser(dto)));
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
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_ID);
        }
    }
}
