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
        return userRepository.findAll().stream()
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
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        checkId(userId);
        User updateUser = UserMapper.mapUserUpdateDtoToUser(dto);
        User oldUser = userRepository.findById(userId).orElseThrow(
                () -> new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, userId));
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
        checkId(id);
        log.debug("Получение пользователя с id = {}", id);
        return UserMapper.mapToUserDto(userRepository.findById(id).orElseThrow(() -> new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, id)));
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
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_ID);
        }
    }
}
