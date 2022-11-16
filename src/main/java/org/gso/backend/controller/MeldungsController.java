package org.gso.backend.controller;

import org.gso.backend.entity.Meldung;
import org.gso.backend.entity.User;
import org.gso.backend.repository.MeldungRepository;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/meldungen")
public class MeldungsController {
    private final MeldungRepository meldungRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MeldungsController(MeldungRepository meldungRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.meldungRepository = meldungRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/get/own")
    public ResponseEntity<List<Meldung>> getOwnMeldungen(@RequestHeader(HttpHeaders.AUTHORIZATION) String access_token){
        User user = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromAccessToken(access_token)).get();

        System.out.println(user.getEmail());
        System.out.println(user.getId());


        return ResponseEntity.ok(meldungRepository.findAllByCreated_by(user));
    }
}
