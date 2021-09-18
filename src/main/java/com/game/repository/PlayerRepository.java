package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PlayerRepository extends PagingAndSortingRepository<Player, Long> {

    Player findById(String id);

    List<Player> findAll(Sort sort);

    Player save(Player player);

    void delete(Player player);
}
