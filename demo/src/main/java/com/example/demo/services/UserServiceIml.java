package com.example.demo.services;

import com.example.demo.components.JwtTokenUtil;
import com.example.demo.dto.UserDTO;
import com.example.demo.exceptions.DataNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                    .orElseThrow(() -> new RuntimeException("user not found"));
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
}
