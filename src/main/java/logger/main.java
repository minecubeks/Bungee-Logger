package logger;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class main extends Plugin {

    @Override
    public void onEnable() {
        new enable().onEnable(this);
    }

    @Override
    public void onDisable() {
        try {
            new disable().onDisable(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
