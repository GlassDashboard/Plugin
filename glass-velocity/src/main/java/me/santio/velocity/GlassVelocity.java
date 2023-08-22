package me.santio.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "glass-velocity",
        name = "Glass",
        version = "1.0",
        description = "The official plugin for Glass on Velocity",
        authors = {"Santio71"}
)
public class GlassVelocity {
    
    @Inject private Logger logger;
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // TODO Add support for Velocity
    }
}
