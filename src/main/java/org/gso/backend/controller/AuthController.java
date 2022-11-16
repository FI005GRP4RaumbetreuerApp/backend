package org.gso.backend.controller;

import org.gso.backend.entity.User;
import org.gso.backend.enums.Role;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.request.LoginRequest;
import org.gso.backend.request.RefreshRequest;
import org.gso.backend.request.RegistrationRequest;
import org.gso.backend.response.LoginResponse;
import org.gso.backend.response.RefreshResponse;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        if (!authentication.isAuthenticated()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByEmail(loginRequest.getEmail()).get();

        String access_token = jwtTokenProvider.generateToken(user);
        String refresh_token = jwtTokenProvider.generateRefreshToken(user);

        user.setRefresh_token(refresh_token);
        userRepository.save(user);

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .access_token(access_token)
                        .refresh_token(refresh_token)
                        .email(user.getEmail())
                        .roles(user.getAuthorities())
                        .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        if (!jwtTokenProvider.validate_refresh_token(refreshRequest.getRefresh_token()) || !jwtTokenProvider.is_access_token_expired_but_valid(refreshRequest.getAccess_token())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<User> optionalUser = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromRefreshToken(refreshRequest.getRefresh_token()));

        if (!optionalUser.isPresent()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = optionalUser.get();

        if (!user.getRefresh_token().equals(refreshRequest.getRefresh_token()))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        String access_token = jwtTokenProvider.generateToken(user);

        return ResponseEntity.ok(RefreshResponse.builder().access_token(access_token).build());
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegistrationRequest registrationRequest){
        if(!registrationRequest.isValid()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setVorname(registrationRequest.getVornamen());
        user.setNachname(registrationRequest.getNachnamen());
        user.setKuerzel(registrationRequest.getKürzel());
        user.setPassword(registrationRequest.getPasswordEncrypted());
        user.setRole(Role.USER);
        user.set_active(true);

        String access_token = jwtTokenProvider.generateToken(user);
        String refresh_token = jwtTokenProvider.generateRefreshToken(user);

        user.setRefresh_token(refresh_token);
        userRepository.save(user);

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .access_token(access_token)
                        .refresh_token(refresh_token)
                        .email(user.getEmail())
                        .roles(user.getAuthorities())
                        .build());
    }

}
