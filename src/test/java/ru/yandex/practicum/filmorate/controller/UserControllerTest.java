package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTests {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User();
        user.setEmail("valid.email@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldThrowExceptionForEmptyEmail() {
        user.setEmail("");

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(user));

        assertEquals("электронная почта не может быть пустой и должна содержать символ @", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        user.setEmail("invalidEmail");

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(user));

        assertEquals("электронная почта не может быть пустой и должна содержать символ @", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyLogin() {
        user.setLogin("");

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(user));

        assertEquals("логин не может быть пустым и содержать пробелы", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForLoginWithSpaces() {
        user.setLogin("invalid login");

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(user));

        assertEquals("логин не может быть пустым и содержать пробелы", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForFutureBirthday() {
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(user));

        assertEquals("дата рождения не может быть в будущем", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyRequest() {
        User emptyUser = new User();

        ValidationException thrown = assertThrows(ValidationException.class,
                () -> userController.create(emptyUser));

        assertEquals("электронная почта не может быть пустой и должна содержать символ @", thrown.getMessage());
    }

    @Test
    void shouldCreateValidUser() {
        assertDoesNotThrow(() -> userController.create(user));
    }
}
