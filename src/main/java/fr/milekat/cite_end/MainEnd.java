package fr.milekat.cite_end;

import fr.milekat.cite_end.engine.AntiAFKTimer;
import fr.milekat.cite_end.event.EndEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class MainEnd extends JavaPlugin {
    private static MainEnd mainEnd;
    public static HashMap<Player, String> lastPlayerSurvie = new HashMap<>();
    public static HashMap<Player, Location> lastPlayerLocation = new HashMap<>();
    public static HashMap<Player, Integer> timesPlayerAFK = new HashMap<>();
    private BukkitTask timerAfk;

    @Override
    public void onEnable() {
        mainEnd = this;
        //  Event
        getServer().getPluginManager().registerEvents(new EndEvent(),this);
        //  Engines
        timerAfk = new AntiAFKTimer().runTask();
    }

    @Override
    public void onDisable() {
        timerAfk.cancel();
    }

    public static MainEnd getMainEnd() {
        return mainEnd;
    }
}
