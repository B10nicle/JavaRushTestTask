package com.game.repositories;

import com.game.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Oleg Khilko
 */

@Repository
public interface PlayersRepository extends JpaRepository<Player, Long>, JpaSpecificationExecutor<Player> {

}
