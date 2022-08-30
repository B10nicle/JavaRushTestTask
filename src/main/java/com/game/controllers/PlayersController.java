package com.game.controllers;

import com.game.models.Player;
import com.game.models.Profession;
import com.game.models.Race;
import com.game.services.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Oleg Khilko
 */

@RestController
@RequestMapping("/rest")
public class PlayersController {
    private PlayersService service;

    @Autowired
    public void setService(PlayersService service) {
        this.service = service;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getPlayersList(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "race", required = false) Race race,
                                       @RequestParam(value = "profession", required = false) Profession profession,
                                       @RequestParam(value = "after", required = false) Long after,
                                       @RequestParam(value = "before", required = false) Long before,
                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                       @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                       @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return service.getAllPlayers(
                        Specification.where(
                                service.filterByName(name)
                                .and(service.filterByTitle(title)))
                                .and(service.filterByRace(race))
                                .and(service.filterByProfession(profession))
                                .and(service.filterByBirthday(after, before))
                                .and(service.filterByBan(banned))
                                .and(service.filterByExperience(minExperience, maxExperience))
                                .and(service.filterByLevel(minLevel, maxLevel)), pageable)
                .getContent();
    }

    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getPlayersCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        return service.getAllPlayers(
                        Specification.where(
                                service.filterByName(name)
                                .and(service.filterByTitle(title)))
                                .and(service.filterByRace(race))
                                .and(service.filterByProfession(profession))
                                .and(service.filterByBirthday(after, before))
                                .and(service.filterByBan(banned))
                                .and(service.filterByExperience(minExperience, maxExperience))
                                .and(service.filterByLevel(minLevel, maxLevel)))
                .size();
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player) {

        return service.createPlayer(player);
    }

    @GetMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable(value = "id") String id) {
        Long longId = service.checkAndParseId(id);

        return service.getPlayer(longId);
    }

    @PostMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Player updatePlayer(@PathVariable(value = "id") String id, @RequestBody Player player) {
        Long longId = service.checkAndParseId(id);

        return service.updatePlayer(longId, player);
    }

    @DeleteMapping("/players/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable(value = "id") String id) {
        Long longId = service.checkAndParseId(id);
        service.deleteById(longId);
    }
}
