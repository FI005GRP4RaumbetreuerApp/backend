package org.gso.backend.controller;

import org.gso.backend.entity.Room;
import org.gso.backend.repository.RaumRepository;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
