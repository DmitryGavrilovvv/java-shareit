package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto dto);

    UserDto updateUser(Long id, UserDto dto);

    UserDto getUserById(Long id);

    void deleteUser(Long id);
}
