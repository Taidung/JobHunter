package vn.taidung.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.taidung.jobhunter.domain.User;
import vn.taidung.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }
}
