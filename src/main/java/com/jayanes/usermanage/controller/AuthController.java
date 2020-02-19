package com.jayanes.usermanage.controller;


import com.jayanes.usermanage.exception.AppException;
import com.jayanes.usermanage.model.Role;
import com.jayanes.usermanage.model.RoleName;
import com.jayanes.usermanage.model.User;
import com.jayanes.usermanage.payload.ApiResponse;
import com.jayanes.usermanage.payload.JwtAuthenticationResponse;
import com.jayanes.usermanage.payload.LoginRequest;
import com.jayanes.usermanage.payload.SignUpRequest;
import com.jayanes.usermanage.repository.RoleRepository;
import com.jayanes.usermanage.repository.UserRepository;
import com.jayanes.usermanage.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Value("${login.attempt}")
    private int loginAttemptExceed;

    @Value("${login.pending.status}")
    private int loginPendingStatus;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        List<User> isUserExist =userRepository.findOneByUsername(loginRequest.getUsername());
        if (!isUserExist.isEmpty()){

            if(isUserExist.get(0).getStatus()==loginPendingStatus){
                return ResponseEntity.ok(new ApiResponse(false,"","Sorry! , Your account has not been activated yet.Please contact your admin"));            }else {
                int loginAttempt =isUserExist.get(0).getLoginAttemptCount();
                if(loginAttempt ==loginAttemptExceed){
                    return ResponseEntity.ok(new ApiResponse(false,"","Sorry! , Your account has been locked.Please contact your admin"));
                }else {
                    boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), isUserExist.get(0).getPassword());
                    if(isPasswordMatch){
                        Optional<User> user=userRepository.findById(isUserExist.get(0).getId());
                        user.get().setLoginAttemptCount(0);
                        userRepository.save(user.get());
                        Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        loginRequest.getUsername (),
                                        loginRequest.getPassword()
                                )
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        String jwt = tokenProvider.generateToken(authentication);
                        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
                    }else {
                        loginAttempt = loginAttempt +1;
                        Optional<User> user=userRepository.findById(isUserExist.get(0).getId());
                        user.get().setLoginAttemptCount(loginAttempt);
                        userRepository.save(user.get());

                        return ResponseEntity.ok(new ApiResponse(false,"","You have entered an invalid user name or password ."));
                    }
                }
            }



        }else {
            return ResponseEntity.ok(new ApiResponse(false,"","Sorry! Your account is not found."));

        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false,"", "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "","Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));


if (signUpRequest.getUserRole() !=null){
    if(signUpRequest.getUserRole().equals("ROLE_ADMIN")){
        Role userRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User Role not set."));
        user.setRoles(Collections.singleton(userRole));
        // status 1= deleted ,2=pending ,3=active
        user.setStatus(3);
        user.setLoginAttemptCount(0);
    } else if (signUpRequest.getUserRole().equals("ROLE_USER_MANAGE")){
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER_MANAGE)
                .orElseThrow(() -> new AppException("User Role not set."));
        user.setRoles(Collections.singleton(userRole));
        // status 1= deleted ,2=pending ,3=active
        user.setStatus(2);
        user.setLoginAttemptCount(0);

    }
}
        else {
            Role userRole = roleRepository.findByName(RoleName.ROLE_VIEW)
                    .orElseThrow(() -> new AppException("User Role not set."));
            user.setRoles(Collections.singleton(userRole));
            // status 1= deleted ,2=pending ,3=active
            user.setStatus(2);
            user.setLoginAttemptCount(0);
        }


        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true,"", "User registered successfully"));
    }
}
