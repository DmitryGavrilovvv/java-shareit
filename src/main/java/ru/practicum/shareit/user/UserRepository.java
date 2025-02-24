package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepository implements UserStorage {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        final String email = user.getEmail();
        checkEmail(email);
        user.setId(generateNewId());
        users.put(user.getId(), user);
        emailUniqSet.add(email);
        return user;
    }

    public User updateUser(User user) {
        checkUser(user.getId());
        users.computeIfPresent(user.getId(), (id, u) -> {
                    if (user.getName() != null && !user.getName().isBlank()) {
                        user.setName(user.getName());
                    }
                    if (user.getEmail() != null && !user.getEmail().isBlank()) {
                        final @Email(message = "Поле должно содержать знак @") String email = user.getEmail();
                        if (!email.equals(u.getEmail())) {
                            checkEmail(email);
                            emailUniqSet.remove(u.getEmail());
                            emailUniqSet.add(email);
                        }
                        user.setEmail(user.getEmail());
                    }
                    return user;
                }
        );
        return user;
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
        return ++id;
    }

    private void checkUser(Long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с id = {} не существует", id);
            throw new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, id);
        }
    }

    private void checkEmail(String email) {
        if (emailUniqSet.contains(email)) {
            log.error("E-mail = {}, уже присутствует у другого пользователя", email);
            throw new ShareItException(ShareItExceptionCodes.DUPLICATE_EMAIL, email);
        }
    }
}
