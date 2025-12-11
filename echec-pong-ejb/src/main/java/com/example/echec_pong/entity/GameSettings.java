package com.example.echec_pong.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité JPA pour stocker les paramètres de partie dans MySQL
 */
@Entity
@Table(name = "game_settings")
@NamedQueries({
    @NamedQuery(name = "GameSettings.findAll", query = "SELECT g FROM GameSettings g ORDER BY g.createdAt DESC"),
    @NamedQuery(name = "GameSettings.findByName", query = "SELECT g FROM GameSettings g WHERE g.name = :name"),
    @NamedQuery(name = "GameSettings.findLatest", query = "SELECT g FROM GameSettings g ORDER BY g.createdAt DESC")
})
public class GameSettings implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "board_width", nullable = false)
    private int boardWidth = 8;

    @Column(name = "pion_health", nullable = false)
    private int pionHealth = 3;

    @Column(name = "cavalier_health", nullable = false)
    private int cavalierHealth = 5;

    @Column(name = "fou_health", nullable = false)
    private int fouHealth = 5;

    @Column(name = "tour_health", nullable = false)
    private int tourHealth = 5;

    @Column(name = "dame_health", nullable = false)
    private int dameHealth = 8;

    @Column(name = "roi_health", nullable = false)
    private int roiHealth = 10;

    @Column(name = "first_serve", nullable = false)
    private String firstServe = "black"; // "black" ou "white"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructeurs
    public GameSettings() {}

    public GameSettings(String name) {
        this.name = name;
    }

    // Getters et Setters
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

    public int getBoardWidth() {
        return boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getPionHealth() {
        return pionHealth;
    }

    public void setPionHealth(int pionHealth) {
        this.pionHealth = pionHealth;
    }

    public int getCavalierHealth() {
        return cavalierHealth;
    }

    public void setCavalierHealth(int cavalierHealth) {
        this.cavalierHealth = cavalierHealth;
    }

    public int getFouHealth() {
        return fouHealth;
    }

    public void setFouHealth(int fouHealth) {
        this.fouHealth = fouHealth;
    }

    public int getTourHealth() {
        return tourHealth;
    }

    public void setTourHealth(int tourHealth) {
        this.tourHealth = tourHealth;
    }

    public int getDameHealth() {
        return dameHealth;
    }

    public void setDameHealth(int dameHealth) {
        this.dameHealth = dameHealth;
    }

    public int getRoiHealth() {
        return roiHealth;
    }

    public void setRoiHealth(int roiHealth) {
        this.roiHealth = roiHealth;
    }

    public String getFirstServe() {
        return firstServe;
    }

    public void setFirstServe(String firstServe) {
        this.firstServe = firstServe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "GameSettings{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", boardWidth=" + boardWidth +
                ", pionHealth=" + pionHealth +
                ", cavalierHealth=" + cavalierHealth +
                ", fouHealth=" + fouHealth +
                ", tourHealth=" + tourHealth +
                ", dameHealth=" + dameHealth +
                ", roiHealth=" + roiHealth +
                ", firstServe='" + firstServe + '\'' +
                '}';
    }
}
