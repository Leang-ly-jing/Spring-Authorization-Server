package co.istad.spring_authorization_server.service;

import co.istad.spring_authorization_server.dto.CreateUserRequest;
import co.istad.spring_authorization_server.dto.UserResponse;
import co.istad.spring_authorization_server.entity.Role;
import co.istad.spring_authorization_server.entity.User;
import co.istad.spring_authorization_server.mapper.UserMapper;
import co.istad.spring_authorization_server.repository.RoleRepository;
import co.istad.spring_authorization_server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper; // 👈 MapStruct automatically injects here

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + createUserRequest.getUsername());
        }

        if (createUserRequest.getEmail() != null && userRepository.existsByEmail(createUserRequest.getEmail())) {
                throw new RuntimeException("Email already exists: ");
        }


        User user = userMapper.toEntity(createUserRequest);

        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setRoles(resolveRoles(createUserRequest.getRoles()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                roles.add(roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build())));
            }
        } else {
            roles.add(roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build())));
        }
        return roles;
    }
}

