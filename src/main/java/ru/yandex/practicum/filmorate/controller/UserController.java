package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public User addFriend(@PathVariable long id, @PathVariable long friend_id) {
        return userService.addFriend(id, friend_id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable long id) {
        return userService.getUserFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friend_id}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friend_id) {
        return userService.deleteFriend(id, friend_id);
    }

    @GetMapping("/{id}/friends/common/{friend_id}")
    public Collection<User> getCommonFriend(@PathVariable long id, @PathVariable long friend_id) {
        return userService.getCommonFriend(id, friend_id);
    }
}
