package com.example.echec_pong.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Client REST pour communiquer avec le microservice EJB sur WildFly
 * Récupère les paramètres de partie stockés dans MySQL via EJB
 */
public class GameSettingsClient {

    private static final String BASE_URL = "http://localhost:8080/echec-pong-ejb/api/settings";
    private static final int TIMEOUT = 5000; // 5 secondes

    /**
     * Classe pour stocker les paramètres récupérés
     */
    public static class GameSettingsDTO {
        private Long id;
        private String name;
        private int boardWidth;
        private int pionHealth;
        private int cavalierHealth;
        private int fouHealth;
        private int tourHealth;
        private int dameHealth;
        private int roiHealth;
        private String firstServe;

        // Getters et Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getBoardWidth() { return boardWidth; }
        public void setBoardWidth(int boardWidth) { this.boardWidth = boardWidth; }
        
        public int getPionHealth() { return pionHealth; }
        public void setPionHealth(int pionHealth) { this.pionHealth = pionHealth; }
        
        public int getCavalierHealth() { return cavalierHealth; }
        public void setCavalierHealth(int cavalierHealth) { this.cavalierHealth = cavalierHealth; }
        
        public int getFouHealth() { return fouHealth; }
        public void setFouHealth(int fouHealth) { this.fouHealth = fouHealth; }
        
        public int getTourHealth() { return tourHealth; }
        public void setTourHealth(int tourHealth) { this.tourHealth = tourHealth; }
        
        public int getDameHealth() { return dameHealth; }
        public void setDameHealth(int dameHealth) { this.dameHealth = dameHealth; }
        
        public int getRoiHealth() { return roiHealth; }
        public void setRoiHealth(int roiHealth) { this.roiHealth = roiHealth; }
        
        public String getFirstServe() { return firstServe; }
        public void setFirstServe(String firstServe) { this.firstServe = firstServe; }

        @Override
        public String toString() {
            return name + " (Largeur: " + boardWidth + ", Pion: " + pionHealth + " HP)";
        }
    }

    /**
     * Récupérer tous les paramètres disponibles
     */
    public List<GameSettingsDTO> getAllSettings() throws Exception {
        String json = sendGetRequest(BASE_URL);
        return parseJsonArray(json);
    }

    /**
     * Récupérer les derniers paramètres créés
     */
    public GameSettingsDTO getLatestSettings() throws Exception {
        String json = sendGetRequest(BASE_URL + "/latest");
        return parseJsonObject(json);
    }

    /**
     * Récupérer les paramètres par nom
     */
    public GameSettingsDTO getSettingsByName(String name) throws Exception {
        String json = sendGetRequest(BASE_URL + "/name/" + name);
        return parseJsonObject(json);
    }

    /**
     * Récupérer les paramètres par défaut
     */
    public GameSettingsDTO getDefaultSettings() throws Exception {
        String json = sendGetRequest(BASE_URL + "/default");
        return parseJsonObject(json);
    }

    /**
     * Récupérer les paramètres par ID
     */
    public GameSettingsDTO getSettingsById(Long id) throws Exception {
        String json = sendGetRequest(BASE_URL + "/" + id);
        return parseJsonObject(json);
    }

    /**
     * Sauvegarder de nouveaux paramètres
     */
    public GameSettingsDTO saveSettings(GameSettingsDTO settings) throws Exception {
        String json = toJson(settings);
        String response = sendPostRequest(BASE_URL, json);
        return parseJsonObject(response);
    }

    /**
     * Vérifier si le service EJB est disponible
     */
    public boolean isServiceAvailable() {
        try {
            URL url = new URL(BASE_URL + "/default");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    // Méthodes utilitaires HTTP
    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Erreur HTTP: " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        conn.disconnect();

        return response.toString();
    }

    private String sendPostRequest(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            throw new RuntimeException("Erreur HTTP: " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        conn.disconnect();

        return response.toString();
    }

    // Parsing JSON simple (sans bibliothèque externe)
    private GameSettingsDTO parseJsonObject(String json) {
        GameSettingsDTO dto = new GameSettingsDTO();
        
        dto.setId(extractLong(json, "id"));
        dto.setName(extractString(json, "name"));
        dto.setBoardWidth(extractInt(json, "boardWidth"));
        dto.setPionHealth(extractInt(json, "pionHealth"));
        dto.setCavalierHealth(extractInt(json, "cavalierHealth"));
        dto.setFouHealth(extractInt(json, "fouHealth"));
        dto.setTourHealth(extractInt(json, "tourHealth"));
        dto.setDameHealth(extractInt(json, "dameHealth"));
        dto.setRoiHealth(extractInt(json, "roiHealth"));
        dto.setFirstServe(extractString(json, "firstServe"));
        
        return dto;
    }

    private List<GameSettingsDTO> parseJsonArray(String json) {
        List<GameSettingsDTO> list = new ArrayList<>();
        
        // Trouver chaque objet dans le tableau
        int depth = 0;
        int start = -1;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String objJson = json.substring(start, i + 1);
                    list.add(parseJsonObject(objJson));
                    start = -1;
                }
            }
        }
        
        return list;
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) return "";
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return "";
        return json.substring(start, end);
    }

    private int extractInt(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return 0;
        start += pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        if (end == start) return 0;
        return Integer.parseInt(json.substring(start, end));
    }

    private Long extractLong(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) return null;
        start += pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        if (end == start) return null;
        return Long.parseLong(json.substring(start, end));
    }

    private String toJson(GameSettingsDTO dto) {
        return "{" +
                "\"name\":\"" + dto.getName() + "\"," +
                "\"boardWidth\":" + dto.getBoardWidth() + "," +
                "\"pionHealth\":" + dto.getPionHealth() + "," +
                "\"cavalierHealth\":" + dto.getCavalierHealth() + "," +
                "\"fouHealth\":" + dto.getFouHealth() + "," +
                "\"tourHealth\":" + dto.getTourHealth() + "," +
                "\"dameHealth\":" + dto.getDameHealth() + "," +
                "\"roiHealth\":" + dto.getRoiHealth() + "," +
                "\"firstServe\":\"" + dto.getFirstServe() + "\"" +
                "}";
    }
}
