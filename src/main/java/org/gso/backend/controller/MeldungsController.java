package org.gso.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.gso.backend.entity.GeräteTyp;
import org.gso.backend.entity.Meldung;
import org.gso.backend.entity.Room;
import org.gso.backend.entity.User;
import org.gso.backend.enums.Meldungstyp;
import org.gso.backend.enums.Status;
import org.gso.backend.exceptions.*;
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

    @GetMapping("/get/all")
    public ResponseEntity<List<Meldung>> getAllMeldungen(){
        return ResponseEntity.ok(meldungsRepository.findAll());
    }

    @GetMapping("/get/room/{room_id}")
    public ResponseEntity<List<Meldung>> getAllMeldungenByRoom(@PathVariable String room_id){
        return ResponseEntity.ok(meldungsRepository.findAllByRoomId(room_id));
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
        } catch (UserNotFoundException | RoomNotFoundException | GeräteTypNotFoundException | MeldungstypNotFoundException | StatusNotFoundException e) {
            log.warn(e.getMessage());
            //e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    private Meldung buildMeldungFromRequest(MeldungsRequest meldungsRequest) throws UserNotFoundException, RoomNotFoundException, GeräteTypNotFoundException, MeldungstypNotFoundException, StatusNotFoundException {
        if(!EnumUtils.isValidEnum(Meldungstyp.class, meldungsRequest.getMeldungs_typ().toUpperCase())) throw new MeldungstypNotFoundException("Meldungstyp " + meldungsRequest.getMeldungs_typ().toUpperCase() + " is unknown!");
        if(!EnumUtils.isValidEnum(Status.class, meldungsRequest.getStatus().toUpperCase())) throw new StatusNotFoundException("Status " + meldungsRequest.getStatus().toUpperCase() + " is unknown!");

        Optional<User> user = userRepository.getById(meldungsRequest.getCreated_by_id());
        Optional<Room> room = raumRepository.getById(meldungsRequest.getRaum_id());
        Optional<GeräteTyp> geraete_typ = geraeteTypRepository.getById(meldungsRequest.getGeraete_typ_id());

        if(!user.isPresent()) throw new UserNotFoundException("No User with ID " + meldungsRequest.getCreated_by_id() + " was found!");
        if(!room.isPresent()) throw new RoomNotFoundException("No Room with ID " + meldungsRequest.getRaum_id() + " was found!");
        if(!geraete_typ.isPresent()) throw new GeräteTypNotFoundException("No Geräte-Typ with ID " + meldungsRequest.getGeraete_typ_id() + " was found!");


        Meldung meldung = new Meldung();
        meldung.setDescription(meldungsRequest.getDescription());
        meldung.setGeraete_id(meldungsRequest.getGeraete_id());

        meldung.setMeldungstyp(Meldungstyp.valueOf(meldungsRequest.getMeldungs_typ().toUpperCase()));
        meldung.setStatus(Status.valueOf(meldungsRequest.getStatus()));

        meldung.setGeraete_typ(geraete_typ.get());
        meldung.setCreated_by(user.get());
        meldung.setRoom(room.get());

        return meldung;
    }

}
