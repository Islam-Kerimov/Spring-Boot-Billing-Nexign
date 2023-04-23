package ru.nexign.spring.boot.billing.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nexign.spring.boot.billing.model.dto.UserDto;
import ru.nexign.spring.boot.billing.model.entity.User;
import ru.nexign.spring.boot.billing.model.mapper.UserMapper;
import ru.nexign.spring.boot.billing.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAll();
        return userMapper.entityUserListToDtoList(users);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Min(1) Integer id) {
        Optional<User> user = userService.findById(id);
        return userMapper.entityUserToDto(user
                .orElseThrow(() -> new EntityNotFoundException("There is no user with ID = " + id + " in Database")));
    }
}
