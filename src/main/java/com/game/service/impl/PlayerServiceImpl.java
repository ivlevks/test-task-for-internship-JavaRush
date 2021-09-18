package com.game.service.impl;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Override
    public Player create(Player player) {
        Player createdPlayer = player;
        if (createdPlayer.getBanned() == null) {
            createdPlayer.setBanned(false);
        }

        List<Player> playerList = (List<Player>) playerRepository.findAll();
        long newId = playerList.size() + 1;
        createdPlayer.setId(newId);

        int level = setLevel(createdPlayer.getExperience());
        createdPlayer.setLevel(level);

        int untilNextLevel = setUntilNextLevel(level, createdPlayer.getExperience());
        createdPlayer.setUntilNextLevel(untilNextLevel);

        return playerRepository.save(createdPlayer);
    }

    @Override
    public List<Player> readAll(String name, String title, Race race, Profession profession, Long after, Long before,
                                Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer pageSize) {

        EntityManager manager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        manager.getTransaction().begin();
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = query.from(Player.class);
        Predicate finalPredicate = getFinalPredicate(name, title, race, profession, after,
                                                    before, banned, minExperience, maxExperience,
                                                    minLevel, maxLevel, criteriaBuilder, playerRoot);

        query.select(playerRoot);
        if (order != null) {
            query.orderBy(criteriaBuilder.asc(playerRoot.get(order.getFieldName())));
        } else {
            query.orderBy(criteriaBuilder.asc(playerRoot.get(PlayerOrder.ID.getFieldName())));
        }
        query.where(finalPredicate);

        if (pageSize != null) {
            TypedQuery<Player> typedQuery = manager.createQuery(query);
            if (pageNumber != null) {
                typedQuery.setFirstResult(pageNumber * pageSize);
            } else {
                typedQuery.setFirstResult(0);
            }
            typedQuery.setMaxResults(pageSize);
            List<Player> playerList = typedQuery.getResultList();
            return playerList;
        } else {
            TypedQuery<Player> typedQuery = manager.createQuery(query);
            if (pageNumber != null) {
                typedQuery.setFirstResult(pageNumber * 3);
            } else {
                typedQuery.setFirstResult(0);
            }
            typedQuery.setMaxResults(3);
            List<Player> playerList = typedQuery.getResultList();
            return playerList;
        }
    }

    @Override
    public Player read(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if (player.isPresent()) {
            return player.get();
        }
        return null;
    }

    @Override
    public Player update(Player player, long id) {
        Player updatedPlayer = read(id);
        if (player.getName() != null) {
            updatedPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            updatedPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            updatedPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            updatedPlayer.setProfession(player.getProfession());
        }

        if (player.getBirthday() != null) {
            Long newDate = player.getBirthday().getTime();
            updatedPlayer.setBirthday(new Date(newDate));
        }
        if (player.getBanned() != null) {
            updatedPlayer.setBanned(player.getBanned());
        }
        if (player.getExperience() != null) {
            updatedPlayer.setExperience(player.getExperience());
            int level = setLevel(updatedPlayer.getExperience());
            updatedPlayer.setLevel(level);

            int untilNextLevel = setUntilNextLevel(level, updatedPlayer.getExperience());
            updatedPlayer.setUntilNextLevel(untilNextLevel);
        }

        Player finalPlayer = playerRepository.save(updatedPlayer);
        return finalPlayer;
    }

    @Override
    public boolean delete(long id) {
        Player deletedPlayer = read(id);
        if (deletedPlayer != null) {
            playerRepository.delete(deletedPlayer);
            return true;
        }
        return false;
    }

    @Override
    public int count(String name, String title, Race race, Profession profession, Long after, Long before,
                     Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {

        int result = 0;
        EntityManager manager = entityManagerFactory.getNativeEntityManagerFactory().createEntityManager();
        manager.getTransaction().begin();
        CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
        CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
        Root<Player> playerRoot = query.from(Player.class);
        Predicate finalPredicate = getFinalPredicate(name, title, race, profession, after,
                                                    before, banned, minExperience, maxExperience,
                                                    minLevel, maxLevel, criteriaBuilder, playerRoot);
        query.select(playerRoot);
        query.where(finalPredicate);
        result = manager.createQuery(query).getResultList().size();
        return result;
    }

    @Override
    public int count() {
        List<Player> playerList = (List<Player>) playerRepository.findAll();
        return playerList.size();
    }

    public int setLevel(int exp) {
        int level = (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
        return level;
    }

    public int setUntilNextLevel(int level, int exp) {
        int untilNextLevel = 50 * (level + 1) * (level + 2) - exp;
        return untilNextLevel;
    }

    public Predicate getFinalPredicate (String name, String title, Race race, Profession profession, Long after,
                                             Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                             Integer minLevel, Integer maxLevel, CriteriaBuilder criteriaBuilder,
                                             Root<Player> playerRoot) {
        List<Predicate> predicateList = new ArrayList<>();
        Predicate[] predicates = new Predicate[1];

        if (name != null) {
            predicates[0] = criteriaBuilder.like(playerRoot.get("name"), "%" + name + "%");
            predicateList.add(predicates[0]);
        }

        if (title != null) {
            predicates[0] = criteriaBuilder.like(playerRoot.get("title"), "%" + title + "%");
            predicateList.add(predicates[0]);
        }

        if (minLevel != null) {
            predicates[0] = criteriaBuilder.ge(playerRoot.get("level"), minLevel);
            predicateList.add(predicates[0]);
        }

        if (minExperience != null) {
            predicates[0] = criteriaBuilder.ge(playerRoot.get("experience"), minExperience);
            predicateList.add(predicates[0]);
        }

        if (maxLevel != null) {
            predicates[0] = criteriaBuilder.le(playerRoot.get("level"), maxLevel);
            predicateList.add(predicates[0]);
        }

        if (maxExperience != null) {
            predicates[0] = criteriaBuilder.le(playerRoot.get("experience"), maxExperience);
            predicateList.add(predicates[0]);
        }

        if (race != null) {
            predicates[0] = criteriaBuilder.equal(playerRoot.get("race"), race);
            predicateList.add(predicates[0]);
        }

        if (profession != null) {
            predicates[0] = criteriaBuilder.equal(playerRoot.get("profession"), profession);
            predicateList.add(predicates[0]);
        }

        if (before != null) {
            Date date = new Date(before);
            predicates[0] = criteriaBuilder.lessThanOrEqualTo(playerRoot.get("birthday"), date);
            predicateList.add(predicates[0]);
        }

        if (after != null) {
            Date date = new Date(after);
            predicates[0] = criteriaBuilder.greaterThanOrEqualTo(playerRoot.get("birthday"), date);
            predicateList.add(predicates[0]);
        }

        if (banned != null) {
            predicates[0] = criteriaBuilder.equal(playerRoot.get("banned"), banned);
            predicateList.add(predicates[0]);
        }

        Predicate finalPredicate = criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        return finalPredicate;
    }
}
