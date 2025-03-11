package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User newUser);

    User getUserById(Long id);

    void deleteUserById(Long id);
}
