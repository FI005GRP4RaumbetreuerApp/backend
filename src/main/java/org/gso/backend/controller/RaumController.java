package org.gso.backend.controller;

import org.gso.backend.entity.Room;
import org.gso.backend.entity.User;
import org.gso.backend.repository.RaumRepository;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/raum")
public class RaumController {
    private final RaumRepository raumRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RaumController(RaumRepository raumRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.raumRepository = raumRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<Room>> getAllRooms(){
        List<Room> roomList = raumRepository.findAll();

        return ResponseEntity.ok(roomList);
    }

    @GetMapping("/get/own")
    public ResponseEntity<List<Room>> getOwnRooms(@RequestHeader(HttpHeaders.AUTHORIZATION) String access_token){
        User user = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromAccessToken(access_token)).get();

        return ResponseEntity.ok(raumRepository.getById(user));
    }
}
