package logger

import net.md_5.bungee.api.plugin.Plugin
import java.io.IOException

class main : Plugin() {
    override fun onEnable() {
        Enable().onEnable(this)
    }

    override fun onDisable() {
        try {
            Disable().onDisable(this)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}