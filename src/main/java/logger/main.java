package logger;

import net.md_5.bungee.api.plugin.Plugin;

public class main extends Plugin {

    @Override
    public void onEnable() {
        new enable().onEnable(this);
    }

    @Override
    public void onDisable() {
        new disable().onDisable(this);
    }
}
