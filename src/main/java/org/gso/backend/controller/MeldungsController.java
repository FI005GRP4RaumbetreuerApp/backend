package org.gso.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.gso.backend.entity.GeräteTyp;
import org.gso.backend.entity.Meldung;
import org.gso.backend.entity.Room;
import org.gso.backend.entity.User;
import org.gso.backend.exceptions.GeräteTypNotFoundException;
import org.gso.backend.exceptions.RoomNotFoundException;
import org.gso.backend.exceptions.UserNotFoundException;
import org.gso.backend.repository.GeräteTypRepository;
import org.gso.backend.repository.MeldungsRepository;
import org.gso.backend.repository.RaumRepository;
import org.gso.backend.repository.UserRepository;
import org.gso.backend.request.MeldungsRequest;
import org.gso.backend.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/meldungen")
@Slf4j
public class MeldungsController {
    private final MeldungsRepository meldungsRepository;
    private final UserRepository userRepository;
    private final RaumRepository raumRepository;
    private final GeräteTypRepository geraeteTypRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MeldungsController(MeldungsRepository meldungsRepository, UserRepository userRepository, RaumRepository raumRepository, GeräteTypRepository geraeteTypRepository, JwtTokenProvider jwtTokenProvider) {
        this.meldungsRepository = meldungsRepository;
        this.userRepository = userRepository;
        this.raumRepository = raumRepository;
        this.geraeteTypRepository = geraeteTypRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/get/own")
    public ResponseEntity<List<Meldung>> getOwnMeldungen(@RequestHeader(HttpHeaders.AUTHORIZATION) String access_token){
        User user = userRepository.findByEmail(jwtTokenProvider.getUserEmailFromAccessToken(access_token)).get();

        System.out.println(user.getEmail());
        System.out.println(user.getId());


        return ResponseEntity.ok(meldungsRepository.findAllByCreated_by(user));
    }

    @PostMapping("/add")
    public ResponseEntity getOwnMeldungen(@RequestBody MeldungsRequest meldungsRequest){
        try {
            Meldung meldung = buildMeldungFromRequest(meldungsRequest);
            meldungsRepository.save(meldung);
        } catch (UserNotFoundException | RoomNotFoundException | GeräteTypNotFoundException e) {
            log.warn(e.getMessage());
            //e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    private Meldung buildMeldungFromRequest(MeldungsRequest meldungsRequest) throws UserNotFoundException, RoomNotFoundException, GeräteTypNotFoundException {
        Optional<User> user = userRepository.getById(meldungsRequest.getCreated_by_id());
        Optional<Room> room = raumRepository.getById(meldungsRequest.getRaum_id());
        Optional<GeräteTyp> geraete_typ = geraeteTypRepository.getById(meldungsRequest.getGeraete_typ_id());

        if(!user.isPresent()) throw new UserNotFoundException("Es konnte kein User mit der ID " + meldungsRequest.getCreated_by_id() + " gefunden werden!");
        if(!room.isPresent()) throw new RoomNotFoundException("Es konnte kein Raum mit der ID " + meldungsRequest.getRaum_id() + " gefunden werden!");
        if(!geraete_typ.isPresent()) throw new GeräteTypNotFoundException("Es konnte kein Geräte-Typ mit der ID " + meldungsRequest.getGeraete_typ_id() + " gefunden werden!");

        Meldung meldung = new Meldung();
        meldung.setMeldungstyp(meldungsRequest.getMeldungs_typ());
        meldung.setDescription(meldungsRequest.getDescription());
        meldung.setStatus(meldungsRequest.getStatus());
        meldung.setGeraete_id(meldungsRequest.getGeraete_id());
        meldung.setGeraete_typ(geraete_typ.get());
        meldung.setCreated_by(user.get());
        meldung.setRoom(room.get());

        return meldung;
    }

}
