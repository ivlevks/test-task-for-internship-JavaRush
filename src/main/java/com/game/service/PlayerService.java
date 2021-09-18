package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface PlayerService {

    Player create(Player player);

    List<Player> readAll(String name, String title, Race race, Profession profession, Long after, Long before,
                         Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                         Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer pageSize);

    Player read(long id);

    Player update(Player player, long id);

    boolean delete(long id);

    int count(String name, String title, Race race, Profession profession, Long after, Long before,
              Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);

    int count();
}
