package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepository implements UserStorage {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        user.setId(generateNewId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен!", user.getId());
        return user;
    }

    public User updateUser(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("Обновление пользователя с id = {} прошло успешно!", newUser.getId());
        return newUser;
    }

    public User getUserById(Long id) {
        checkUser(id);
        return users.get(id);
    }

    public void deleteUserById(Long id) {
        checkUser(id);
        users.remove(id);
        log.info("Удаление пользователя с id = {} прошло успешно!", id);
    }

    private long generateNewId() {
        return id++;
    }

    private void checkUser(Long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с id = {} не существует", id);
            throw new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, id);
        }
    }
}
