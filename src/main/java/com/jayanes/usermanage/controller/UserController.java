package com.jayanes.usermanage.controller;


import com.jayanes.usermanage.model.User;
import com.jayanes.usermanage.payload.ApiResponse;
import com.jayanes.usermanage.payload.LoginRequest;
import com.jayanes.usermanage.payload.SignUpRequest;
import com.jayanes.usermanage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Value("${login.attempt}")
    private int loginAttemptExceed;

    @Value("${login.pending.status}")
    private int loginPendingStatus;

    @GetMapping("/get-all-locked-account")
    public ResponseEntity<?> getAllLockedAccounts() {

        try {
            List<User> users= userRepository.findByLoginAttempt(loginAttemptExceed);
            return ResponseEntity.ok(new ApiResponse(true,users,"Successfully retrieved all locked accounts"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @GetMapping("/get-all-pending-account")
    public ResponseEntity<?> getAllPendingAccounts() {

        try {
            List<User> users= userRepository.findByPendingStatus(loginPendingStatus);
            return ResponseEntity.ok(new ApiResponse(true,users,"Successfully retrieved all pending accounts"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }

    @PostMapping("/unlock_account_by_id")
    public ResponseEntity<?> unlockAccount(@RequestBody User users){
        try {
            Optional<User> user= userRepository.findById(users.getId());
            user.get().setLoginAttemptCount(0);
            userRepository.save(user.get());
            return ResponseEntity.ok(new ApiResponse(true,"","Successfully account unlocked"));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse(false,"",e.getMessage()));
        }

    }
}
