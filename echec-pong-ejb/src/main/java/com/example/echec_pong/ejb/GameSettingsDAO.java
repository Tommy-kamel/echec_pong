package com.example.echec_pong.ejb;

import com.example.echec_pong.entity.GameSettings;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * EJB Stateless pour gérer les opérations CRUD sur GameSettings
 * C'est ce composant qui se connecte à la base de données MySQL
 */
@Stateless
public class GameSettingsDAO {

    @PersistenceContext(unitName = "EchecPongPU")
    private EntityManager em;

    /**
     * Créer ou mettre à jour des paramètres de partie
     */
    public GameSettings save(GameSettings settings) {
        if (settings.getId() == null) {
            em.persist(settings);
            return settings;
        } else {
            return em.merge(settings);
        }
    }

    /**
     * Trouver des paramètres par ID
     */
    public GameSettings findById(Long id) {
        return em.find(GameSettings.class, id);
    }

    /**
     * Trouver des paramètres par nom
     */
    public GameSettings findByName(String name) {
        TypedQuery<GameSettings> query = em.createNamedQuery("GameSettings.findByName", GameSettings.class);
        query.setParameter("name", name);
        List<GameSettings> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Récupérer tous les paramètres de partie
     */
    public List<GameSettings> findAll() {
        return em.createNamedQuery("GameSettings.findAll", GameSettings.class).getResultList();
    }

    /**
     * Récupérer les derniers paramètres créés
     */
    public GameSettings findLatest() {
        TypedQuery<GameSettings> query = em.createNamedQuery("GameSettings.findLatest", GameSettings.class);
        query.setMaxResults(1);
        List<GameSettings> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Supprimer des paramètres par ID
     */
    public void delete(Long id) {
        GameSettings settings = findById(id);
        if (settings != null) {
            em.remove(settings);
        }
    }

    /**
     * Supprimer des paramètres par nom
     */
    public void deleteByName(String name) {
        GameSettings settings = findByName(name);
        if (settings != null) {
            em.remove(settings);
        }
    }
}
