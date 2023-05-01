package logger

import net.md_5.bungee.api.plugin.Plugin


class Main : Plugin() {
    override fun onEnable() {
        val chatEventListener = ChatEventListener(this)
        proxy.pluginManager.registerListener(this, chatEventListener)
        proxy.pluginManager.registerListener(this, ServerSwitchListener(this))
        Enable(this).onEnable(this)
    }

    override fun onDisable() {
        Disable(this).onDisable(this)
    }
}

