package me.santio.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
        id = "mhweb-velocity",
        name = "MHWeb",
        version = "1.0",
        description = "MHWeb",
        authors = {"Santio71"}
)
public class MHWebVelocity {
    
    @Inject private Logger logger;
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // TODO document why this method is empty
    }
}
