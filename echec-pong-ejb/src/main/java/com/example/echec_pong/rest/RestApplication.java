package com.example.echec_pong.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration JAX-RS pour exposer les endpoints REST
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
