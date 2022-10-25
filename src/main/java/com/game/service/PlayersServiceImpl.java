package com.game.service;

import com.game.exception.BadRequestException;
import com.game.exception.PlayerNotFoundException;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Oleg Khilko
 */

@Service
public class PlayersServiceImpl implements PlayersService {
    private PlayersRepository playersRepository;

    @Autowired
    public void setPlayersRepository(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    @Override
    public Page<Player> getAllPlayers(Specification<Player> specification, Pageable sortedByName) {
        return playersRepository.findAll(specification, sortedByName);
    }

    @Override
    public List<Player> getAllPlayers(Specification<Player> specification) {
        return playersRepository.findAll(specification);
    }

    @Override
    public Player createPlayer(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null)
            throw new BadRequestException("The player cannot be created. One of the parameters is null.");

        checkPlayerParameters(player);

        if (player.getBanned() == null)
            player.setBanned(false);

        Integer currentLevel = calculateCurrentLevel(player);
        player.setLevel(currentLevel);

        Integer experienceUntilNextLevel = calculateExperienceUntilNextLevel(player);
        player.setUntilNextLevel(experienceUntilNextLevel);

        return playersRepository.saveAndFlush(player);
    }

    @Override
    public Player getPlayer(Long id) {
        if (!playersRepository.existsById(id))
            throw new PlayerNotFoundException("The player is not found.");

        return playersRepository.findById(id).get();
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        checkPlayerParameters(player);

        if (!playersRepository.existsById(id))
            throw new PlayerNotFoundException("The player is not found.");

        Player updatedPlayer = playersRepository.findById(id).get();

        if (player.getName() != null)
            updatedPlayer.setName(player.getName());

        if (player.getTitle() != null)
            updatedPlayer.setTitle(player.getTitle());

        if (player.getRace() != null)
            updatedPlayer.setRace(player.getRace());

        if (player.getProfession() != null)
            updatedPlayer.setProfession(player.getProfession());

        if (player.getBirthday() != null)
            updatedPlayer.setBirthday(player.getBirthday());

        if (player.getBanned() != null)
            updatedPlayer.setBanned(player.getBanned());

        if (player.getExperience() != null)
            updatedPlayer.setExperience(player.getExperience());

        Integer currentLevel = calculateCurrentLevel(updatedPlayer);
        updatedPlayer.setLevel(currentLevel);

        Integer experienceUntilNextLevel = calculateExperienceUntilNextLevel(updatedPlayer);
        updatedPlayer.setUntilNextLevel(experienceUntilNextLevel);

        return playersRepository.save(updatedPlayer);
    }

    @Override
    public void deleteById(Long id) {
        if (playersRepository.existsById(id))
            playersRepository.deleteById(id);
        else throw new PlayerNotFoundException("The player cannot be found to be deleted");
    }

    private void checkPlayerParameters(Player player) {
        if (player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12))
            throw new BadRequestException("Incorrect name");

        if (player.getTitle() != null && (player.getTitle().length() < 1 || player.getTitle().length() > 30))
            throw new BadRequestException("Incorrect title");

        if (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10_000_000))
            throw new BadRequestException("Incorrect experience");

        if (player.getBirthday() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(player.getBirthday());
            if (calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000)
                throw new BadRequestException("Incorrect birthdate");
        }
    }

    @Override
    public Long checkAndParseId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException("Некорректный ID");

        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID не является числом", e);
        }
    }

    private Integer calculateCurrentLevel(Player player) {
        return (int) ((Math.sqrt(2500 + 200 * player.getExperience())) - 50) / 100;
    }

    private Integer calculateExperienceUntilNextLevel(Player player) {
        return 50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience();
    }

    @Override
    public Specification<Player> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> filterByTitle(String title) {
        return (root, query, cb) -> title == null ? null : cb.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> filterByRace(Race race) {
        return (root, query, cb) -> race == null ? null : cb.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> filterByProfession(Profession profession) {
        return (root, query, cb) -> profession == null ? null : cb.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> filterByBirthday(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date before1 = new Date(before);
                return cb.lessThanOrEqualTo(root.get("birthday"), before1);
            }
            if (before == null) {
                Date after1 = new Date(after);
                return cb.greaterThanOrEqualTo(root.get("birthday"), after1);
            }
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return cb.between(root.get("birthday"), after1, before1);
        };
    }

    @Override
    public Specification<Player> filterByBan(Boolean banned) {
        return (root, query, cb) -> {
            if (banned == null)
                return null;
            if (banned)
                return cb.isTrue(root.get("banned"));
            else return cb.isFalse(root.get("banned"));
        };
    }

    @Override
    public Specification<Player> filterByExperience(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("experience"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("experience"), min);

            return cb.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> filterByLevel(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("level"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("level"), min);

            return cb.between(root.get("level"), min, max);
        };
    }
}
