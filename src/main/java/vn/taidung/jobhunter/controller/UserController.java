package vn.taidung.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.domain.response.ResCreateUserDTO;
import vn.taidung.jobhunter.domain.response.ResUpdateUserDTO;
import vn.taidung.jobhunter.domain.response.ResUserDTO;
import vn.taidung.jobhunter.domain.response.ResultPaginationDTO;
import vn.taidung.jobhunter.service.UserService;
import vn.taidung.jobhunter.util.annotition.ApiMessage;
import vn.taidung.jobhunter.util.error.ResourceNotFoundException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch a user")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.userService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.ok(userService.convertToFetchUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.userService.fetchAllUser(spec, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user)
            throws ResourceNotFoundException {

        if (this.userService.isEmailExist(user.getEmail())) {
            throw new ResourceNotFoundException(
                    "Email " + user.getEmail() + "đã tồn tại, vui lòng sử dụng email khác");
        }

        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User savedUser = userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.convertToResCreateUserDTO(savedUser));
    }

    @PutMapping("/users")
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user)
            throws ResourceNotFoundException {
        if (!this.userService.isIdExist(user.getId())) {
            throw new ResourceNotFoundException(
                    "Id: " + user.getId() + " không tồn tại");
        }
        User savedUser = userService.handleUpdateUser(user);
        return ResponseEntity.ok(userService.convertToResUpdateUserDTO(savedUser));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws ResourceNotFoundException {
        if (!this.userService.isIdExist(id)) {
            throw new ResourceNotFoundException(
                    "Id: " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
