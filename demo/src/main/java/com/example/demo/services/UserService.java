package com.example.demo.services;

import com.example.demo.dto.UpdateUserDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.User;

public interface UserService {
    User createUser(UserDTO userDTO);
    String login(String userName,String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
}
