package vn.taidung.jobhunter.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User postManUser) {

        User tdUser = this.userService.handleCreateUser(postManUser);

        return tdUser;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        this.userService.handleDeleteUser(id);
        return "tdung";
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return this.userService.fetchUserById(id);
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        return this.userService.fetchAllUsers();
    }
}
