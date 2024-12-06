package com.example.demo.services;

import com.example.demo.components.JwtTokenUtil;
import com.example.demo.dto.UpdateUserDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
        @Override
        public User createUser(UserDTO userDTO)  {
            String userName=userDTO.getUserName();//ktr xem userr đã tồn tại chưa
            if (userRepository.existsByUserName(userName)) {
                throw new DataIntegrityViolationException("user name already exists");
            }
            User newUser=User.builder()
                    .fullName(userDTO.getFullName())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .address(userDTO.getAddress())
                    .dateOfBirth(userDTO.getDateOfBirth())
                    .googleAccountId(userDTO.getGoogleAccountId())
                    .facebookAccountId(userDTO.getFacebookAccountId())
                    .userName(userDTO.getUserName())
                    .password(userDTO.getPassword())
                    .build();
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            newUser.setRole(role);
            //kiem tra neu la fb or googleid, thi kh yeu cau password
            if (userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0) {
                String password=userDTO.getPassword();
                String encodedPassword=passwordEncoder.encode(password);
                newUser.setPassword(encodedPassword);
            }
            return userRepository.save(newUser);
        }

    @Override
    public String login(String userName, String password) throws Exception {
            Optional<User>  optionalUser=userRepository.findByUserName(userName);
            if (optionalUser.isEmpty()) {
                throw new DataNotFoundException("can not find user");
            }
            User existUser=optionalUser.get();
            // check password
            if (existUser.getFacebookAccountId()==0 && existUser.getGoogleAccountId()==0)  {
                if (!passwordEncoder.matches(password, existUser.getPassword())) {
                    throw new BadCredentialsException("wrong password or username");
                }
            }
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                    userName,password,existUser.getAuthorities()
            );
            //authentication vs spring security
            authenticationManager.authenticate(authenticationToken);
            return jwtTokenUtil.generateToken(existUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String userName = jwtTokenUtil.extractUserName(token);
        if (userName == null || userName.isEmpty()) {
            throw new Exception("Invalid token: username not found");
        }
        Optional<User> user = userRepository.findByUserName(userName);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Kiểm tra xem username mới có giống username cũ không
        String newUserName = updatedUserDTO.getUserName();
        if (!existingUser.getUsername().equals(newUserName) &&
                userRepository.existsByUserName(newUserName)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // Update user tu dto
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (newUserName != null) {
            existingUser.setUserName(newUserName);
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if (updatedUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }
        if (updatedUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUserDTO.getPhoneNumber());
        }

        // Update the password
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        return userRepository.save(existingUser);
    }

}
