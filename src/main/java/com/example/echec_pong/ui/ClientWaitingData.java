package com.example.echec_pong.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class ClientWaitingData {
    public final TextField serverIpField;
    public final Button connectButton;
    public final Label statusLabel;

    public ClientWaitingData(TextField serverIpField, Button connectButton, Label statusLabel) {
        this.serverIpField = serverIpField;
        this.connectButton = connectButton;
        this.statusLabel = statusLabel;
    }
}
