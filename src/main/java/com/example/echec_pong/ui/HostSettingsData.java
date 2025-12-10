package com.example.echec_pong.ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    public HostSettingsData(TextField widthField, TextField pionHealth, TextField cavalierHealth,
                           TextField fouHealth, TextField tourHealth, TextField dameHealth,
                           TextField roiHealth, ComboBox<String> firstServeCombo, Label statusLabel) {
        this.widthField = widthField;
        this.pionHealth = pionHealth;
        this.cavalierHealth = cavalierHealth;
        this.fouHealth = fouHealth;
        this.tourHealth = tourHealth;
        this.dameHealth = dameHealth;
        this.roiHealth = roiHealth;
        this.firstServeCombo = firstServeCombo;
        this.statusLabel = statusLabel;
    }
}