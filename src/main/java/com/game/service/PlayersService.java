package com.game.service;

import com.game.model.Player;
import com.game.model.Profession;
import com.game.model.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author Oleg Khilko
 */

public interface PlayersService {
    Page<Player> getAllPlayers(Specification<Player> specification, Pageable sortedByName);

    List<Player> getAllPlayers(Specification<Player> specification);

    Player createPlayer(Player requestPlayer);

    Player getPlayer(Long id);

    Player updatePlayer(Long id, Player player);

    void deleteById(Long id);

    Long checkAndParseId(String id);

    Specification<Player> filterByName(String name);

    Specification<Player> filterByTitle(String title);

    Specification<Player> filterByRace(Race race);

    Specification<Player> filterByProfession(Profession profession);

    Specification<Player> filterByBirthday(Long after, Long before);

    Specification<Player> filterByBan(Boolean isBanned);

    Specification<Player> filterByExperience(Integer min, Integer max);

    Specification<Player> filterByLevel(Integer min, Integer max);
}
