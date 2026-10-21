package co.istad.spring_authorization_server.service;

import co.istad.spring_authorization_server.dto.CreateUserRequest;
import co.istad.spring_authorization_server.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);
}
