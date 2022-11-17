package org.gso.backend.controller;

import org.gso.backend.entity.Meldung;
import org.gso.backend.entity.User;
import org.gso.backend.repository.MeldungsRepository;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final MeldungsRepository meldungsRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(MeldungsRepository meldungsRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.meldungsRepository = meldungsRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/get/own")
    public ResponseEntity<User> getOwnMeldungen(@RequestHeader(HttpHeaders.AUTHORIZATION) String access_token){
        User user = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromAccessToken(access_token)).get();

        return ResponseEntity.ok(user);
    }

}
