package org.gso.backend.controller;

import org.gso.backend.entity.User;
import org.gso.backend.enums.Role;
import org.gso.backend.model.EmailDetails;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.request.*;
import org.gso.backend.response.LoginResponse;
import org.gso.backend.response.RefreshResponse;
import org.gso.backend.security.JwtTokenProvider;
import org.gso.backend.services.EmailService;
import org.gso.backend.utils.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.Executors;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        if (!authentication.isAuthenticated()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-Mail or Password incorrect");

        User user = userRepository.findByEmail(loginRequest.getEmail()).get();

        String access_token = jwtTokenProvider.generateToken(user);
        String refresh_token = jwtTokenProvider.generateRefreshToken(user);

        user.setRefresh_token(refresh_token);
        user.resetPasswordResetCode();
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
    public ResponseEntity register(@RequestBody RegistrationRequest registrationRequest){
        if(!registrationRequest.isValid()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration is invalid!");

        Optional<User> optionalUser = userRepository.findByEmail(registrationRequest.getEmail());

        if(optionalUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-Mail already used!");

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

    @PostMapping("/forgotpassword")
    public ResponseEntity resetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest){
        Optional<User> optionalUser = userRepository.findByEmail(forgotPasswordRequest.getEmail());

        if(!optionalUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No User with this E-Mail was found");

        User user = optionalUser.get();
        String resetCode = new RandomString().nextString();

        user.setPassword_reset_code(resetCode);
        userRepository.save(user);

        EmailDetails emailDetails = EmailDetails.builder()
                .subject("Passwort zurücksetzen")
                .recipient(user.getEmail())
                .msgBody("Hallo " + user.getVorname() + " " + user.getNachname() + ",<br><br>" +
                        "hier ist dein Code, um dein Passwort zurückzusetzen: <b>" + resetCode + "</b><br><br><br>" +
                        "Solltest du dein Passwort nicht vergessen haben, kannst du diese E-Mail ignorieren.").build();

        emailService.sendSimpleMail(emailDetails);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/resetpassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        if(!resetPasswordRequest.isValid()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<User> optionalUser = userRepository.findByPassword_reset_code(resetPasswordRequest.getReset_code());

        if(!optionalUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-Mail already used!");

        User user = optionalUser.get();

        user.resetPasswordResetCode();
        user.resetRefreshToken();
        user.setPassword(resetPasswordRequest.getPasswordEncrypted());
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
