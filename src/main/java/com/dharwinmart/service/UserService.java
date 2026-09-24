package com.dharwinmart.service;

import com.dharwinmart.entity.User;
import com.dharwinmart.exception.ResourceNotFoundException;
import com.dharwinmart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticate a user by username/email, password, and optionally verify role match.
     */
    @Transactional(readOnly = true)
    public User authenticate(String usernameOrEmail, String password, String expectedRole) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Username or email is required.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        String identifier = usernameOrEmail.trim();
        User user = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!user.isEnabled()) {
            throw new IllegalStateException("Your account has been deactivated by an administrator.");
        }

        if (!user.getPassword().equals(password.trim())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        if (expectedRole != null && !expectedRole.trim().isEmpty() && !expectedRole.equalsIgnoreCase("ALL")) {
            if (!user.getRole().equalsIgnoreCase(expectedRole.trim())) {
                throw new IllegalArgumentException("This account is registered as a " + user.getRole() +
                        ", not as a " + expectedRole.toUpperCase() + ".");
            }
        }

        return user;
    }

    /**
     * Registers a new user. Strictly restricts public registration to BUYER or SELLER.
     */
    public User registerUser(String username, String password, String email, String fullName,
                             String role, String phone, String address) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        String cleanRole = role != null ? role.trim().toUpperCase() : "BUYER";
        if ("ADMIN".equalsIgnoreCase(cleanRole)) {
            throw new IllegalArgumentException("Admin accounts cannot be registered publicly.");
        }
        if (!"BUYER".equals(cleanRole) && !"SELLER".equals(cleanRole)) {
            cleanRole = "BUYER";
        }

        String cleanUsername = username.trim();
        String cleanEmail = email.trim();

        if (userRepository.existsByUsernameIgnoreCase(cleanUsername)) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' is already taken.");
        }
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new IllegalArgumentException("An account with email '" + cleanEmail + "' already exists.");
        }

        User user = new User(cleanUsername, password.trim(), cleanEmail, fullName.trim(), cleanRole, phone, address);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        if (role == null || role.trim().isEmpty() || "ALL".equalsIgnoreCase(role)) {
            return getAllUsers();
        }
        return userRepository.findByRoleIgnoreCaseOrderByCreatedAtDesc(role.trim());
    }

    public boolean toggleUserStatus(Long userId, String performingUsername) {
        User user = getUserById(userId);
        if (user.getUsername().equalsIgnoreCase(performingUsername)) {
            throw new IllegalArgumentException("You cannot disable your own active account.");
        }
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
        return user.isEnabled();
    }

    public User updateProfile(Long userId, String fullName, String phone, String address) {
        User user = getUserById(userId);
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        user.setPhone(phone != null ? phone.trim() : null);
        user.setAddress(address != null ? address.trim() : null);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countBuyers() {
        return userRepository.countByRoleIgnoreCase("BUYER");
    }

    @Transactional(readOnly = true)
    public long countSellers() {
        return userRepository.countByRoleIgnoreCase("SELLER");
    }

    @Transactional(readOnly = true)
    public long countAdmins() {
        return userRepository.countByRoleIgnoreCase("ADMIN");
    }
}
