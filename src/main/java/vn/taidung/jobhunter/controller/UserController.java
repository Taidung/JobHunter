package vn.taidung.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/create")
    public String createNewUser() {

        User user = new User();
        user.setEmail("taidung@gmail.com");
        user.setName("Dung");
        user.setPassword("123456");

        this.userService.handleCreateUser(user);

        return "create user";
    }
}
