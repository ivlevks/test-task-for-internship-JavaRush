package com.game.entity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name= "player")
public class Player {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, length = 12)
    private String name;

    @Column(nullable = false, length = 30)
    private String title;

    @Column
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column
    @Enumerated(EnumType.STRING)
    private Profession profession;

    @Column(length = 10000000)
    private Integer experience;

    @Column
    private Integer level;

    @Column
    private Integer untilNextLevel;

    @Column
    private Date birthday;

    @Column Boolean banned;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
