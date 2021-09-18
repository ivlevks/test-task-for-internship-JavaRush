package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.impl.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest")
public class PlayerDataController {

    @Autowired
    private PlayerServiceImpl playerService;

    @GetMapping(value = "/players")
    public ResponseEntity<List<Player>> readAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize) {

        List<Player> playerList = playerService.readAll(name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return new ResponseEntity<>(playerList, HttpStatus.OK);
    }

    @GetMapping(value = "/players/{id}")
    public ResponseEntity<Player> read(@PathVariable(name = "id") long id) {
        if (id > playerService.count()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player player = playerService.read(id);

        if (player != null) {
            return new ResponseEntity<>(player, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/players/count")
    public ResponseEntity<Integer> count(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {
        Integer countEntity = playerService.count(name, title, race, profession, after, before,
                banned, minExperience, maxExperience, minLevel, maxLevel);
        return new ResponseEntity<>(countEntity, HttpStatus.OK);
    }

    @PostMapping(value = "/players")
    public ResponseEntity<?> create(@RequestBody Player player) {
        Player createdPlayer = player;

        if (createdPlayer.getName() == null || createdPlayer.getTitle() == null
            || createdPlayer.getRace() == null || createdPlayer.getProfession() == null
            || createdPlayer.getBirthday() == null || createdPlayer.getExperience() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (createdPlayer.getName().length() > 12 || createdPlayer.getTitle().length() > 30) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (createdPlayer.getName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (createdPlayer.getExperience() < 0 || createdPlayer.getExperience() > 10000000) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (createdPlayer.getBirthday().getTime() < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Calendar year3000 = new GregorianCalendar(3000, 0, 1);
        Date dateAfter = year3000.getTime();
        Calendar year2000 = new GregorianCalendar(2000, 0, 1);
        Date dateBefore = year2000.getTime();
        if (createdPlayer.getBirthday().after(dateAfter) || createdPlayer.getBirthday().before(dateBefore)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player finalPlayer = playerService.create(createdPlayer);
        return new ResponseEntity<>(finalPlayer, HttpStatus.OK);
    }

    @PostMapping(value = "/players/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") long id, @RequestBody Player player) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (id > playerService.count()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (playerService.read(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (player.getName() == null && player.getTitle() == null
                && player.getRace() == null && player.getProfession() == null
                && player.getBirthday() == null && player.getExperience() == null
                && player.getBanned() == null) {
            return new ResponseEntity<>(playerService.read(id), HttpStatus.OK);
        }

        if (player.getExperience() != null) {
            if (player.getExperience() < 0 || player.getExperience() > 10000000) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        if (player.getBirthday() != null) {
            if (player.getBirthday().getTime() < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Calendar year3000 = new GregorianCalendar(3000, 0, 1);
            Date dateAfter = year3000.getTime();
            Calendar year2000 = new GregorianCalendar(2000, 0, 1);
            Date dateBefore = year2000.getTime();
            if (player.getBirthday().after(dateAfter) || player.getBirthday().before(dateBefore)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Player finalPlayer = playerService.update(player, id);
        return new ResponseEntity<>(finalPlayer, HttpStatus.OK);
    }

    @DeleteMapping(value = "/players/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") long id) {
        if (id > playerService.count()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean deleted = playerService.delete(id);

        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }
}
