package com.example.echec_pong.ui;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class HostSettingsData {
    public final TextField widthField;
    public final TextField pionHealth;
    public final TextField cavalierHealth;
    public final TextField fouHealth;
    public final TextField tourHealth;
    public final TextField dameHealth;
    public final TextField roiHealth;
    public final ComboBox<String> firstServeCombo;
    public final Label statusLabel;
    
    // Nouveaux champs pour EJB/base de données
    public final CheckBox useDbSettings;
    public final ComboBox<String> dbSettingsCombo;
    public final Button refreshDbButton;
    public final Label dbStatusLabel;
    public final HBox dbSettingsBox;

    public HostSettingsData(TextField widthField, TextField pionHealth, TextField cavalierHealth,
                           TextField fouHealth, TextField tourHealth, TextField dameHealth,
                           TextField roiHealth, ComboBox<String> firstServeCombo, Label statusLabel,
                           CheckBox useDbSettings, ComboBox<String> dbSettingsCombo, 
                           Button refreshDbButton, Label dbStatusLabel, HBox dbSettingsBox) {
        this.widthField = widthField;
        this.pionHealth = pionHealth;
        this.cavalierHealth = cavalierHealth;
        this.fouHealth = fouHealth;
        this.tourHealth = tourHealth;
        this.dameHealth = dameHealth;
        this.roiHealth = roiHealth;
        this.firstServeCombo = firstServeCombo;
        this.statusLabel = statusLabel;
        this.useDbSettings = useDbSettings;
        this.dbSettingsCombo = dbSettingsCombo;
        this.refreshDbButton = refreshDbButton;
        this.dbStatusLabel = dbStatusLabel;
        this.dbSettingsBox = dbSettingsBox;
    }
}