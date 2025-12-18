package com.example.echec_pong.rest;

import com.example.echec_pong.ejb.GameSettingsDAO;
import com.example.echec_pong.entity.GameSettings;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Endpoint REST pour accéder aux paramètres de partie via microservice
 * Le jeu JavaFX appelle ces endpoints pour récupérer/sauvegarder les paramètres
 */
@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameSettingsResource {

    @EJB
    private GameSettingsDAO gameSettingsDAO;

    /**
     * GET /api/settings - Récupérer tous les paramètres
     */
    @GET
    public Response getAllSettings() {
        List<GameSettings> settings = gameSettingsDAO.findAll();
        return Response.ok(settings).build();
    }

    /**
     * GET /api/settings/latest - Récupérer les derniers paramètres
     */
    @GET
    @Path("/latest")
    public Response getLatestSettings() {
        GameSettings settings = gameSettingsDAO.findLatest();
        if (settings == null) {
            // Retourner des paramètres par défaut si aucun n'existe
            settings = createDefaultSettings();
        }
        return Response.ok(settings).build();
    }

    /**
     * GET /api/settings/{id} - Récupérer des paramètres par ID
     */
    @GET
    @Path("/{id}")
    public Response getSettingsById(@PathParam("id") Long id) {
        GameSettings settings = gameSettingsDAO.findById(id);
        if (settings == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Settings not found\"}")
                    .build();
        }
        return Response.ok(settings).build();
    }

    /**
     * GET /api/settings/name/{name} - Récupérer des paramètres par nom
     */
    @GET
    @Path("/name/{name}")
    public Response getSettingsByName(@PathParam("name") String name) {
        GameSettings settings = gameSettingsDAO.findByName(name);
        if (settings == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Settings not found\"}")
                    .build();
        }
        return Response.ok(settings).build();
    }

    /**
     * POST /api/settings - Créer de nouveaux paramètres
     */
    @POST
    public Response createSettings(GameSettings settings) {
        if (settings.getName() == null || settings.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Name is required\"}")
                    .build();
        }
        
        // Vérifier si le nom existe déjà
        GameSettings existing = gameSettingsDAO.findByName(settings.getName());
        if (existing != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"Settings with this name already exists\"}")
                    .build();
        }
        
        GameSettings saved = gameSettingsDAO.save(settings);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    /**
     * PUT /api/settings/{id} - Mettre à jour des paramètres
     */
    @PUT
    @Path("/{id}")
    public Response updateSettings(@PathParam("id") Long id, GameSettings settings) {
        GameSettings existing = gameSettingsDAO.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Settings not found\"}")
                    .build();
        }
        
        // Mettre à jour les valeurs
        existing.setBoardWidth(settings.getBoardWidth());
        existing.setPionHealth(settings.getPionHealth());
        existing.setCavalierHealth(settings.getCavalierHealth());
        existing.setFouHealth(settings.getFouHealth());
        existing.setTourHealth(settings.getTourHealth());
        existing.setDameHealth(settings.getDameHealth());
        existing.setRoiHealth(settings.getRoiHealth());
        existing.setFirstServe(settings.getFirstServe());
        existing.setProgressBarCapacity(settings.getProgressBarCapacity());
        existing.setSpecialDamage(settings.getSpecialDamage());
        
        GameSettings updated = gameSettingsDAO.save(existing);
        return Response.ok(updated).build();
    }

    /**
     * DELETE /api/settings/{id} - Supprimer des paramètres
     */
    @DELETE
    @Path("/{id}")
    public Response deleteSettings(@PathParam("id") Long id) {
        GameSettings existing = gameSettingsDAO.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Settings not found\"}")
                    .build();
        }
        
        gameSettingsDAO.delete(id);
        return Response.noContent().build();
    }

    /**
     * GET /api/settings/default - Récupérer les paramètres par défaut
     */
    @GET
    @Path("/default")
    public Response getDefaultSettings() {
        GameSettings defaultSettings = createDefaultSettings();
        return Response.ok(defaultSettings).build();
    }

    /**
     * Créer des paramètres par défaut
     */
    private GameSettings createDefaultSettings() {
        GameSettings settings = new GameSettings("default");
        settings.setBoardWidth(8);
        settings.setPionHealth(3);
        settings.setCavalierHealth(5);
        settings.setFouHealth(5);
        settings.setTourHealth(5);
        settings.setDameHealth(8);
        settings.setRoiHealth(10);
        settings.setFirstServe("black");
        settings.setProgressBarCapacity(5);
        settings.setSpecialDamage(3);
        return settings;
    }
}
