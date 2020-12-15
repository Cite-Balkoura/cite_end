package fr.milekat.cite_end.engine;

import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_libs.utils_tools.Jedis.JedisPub;
import fr.milekat.cite_end.MainEnd;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AntiAFKTimer {
    public BukkitTask runTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player: Bukkit.getServer().getOnlinePlayers()) {
                    if (!MainEnd.lastPlayerLocation.containsKey(player)) continue;
                    if (MainEnd.lastPlayerLocation.get(player).distance(player.getLocation())<12) {
                        MainEnd.timesPlayerAFK.put(player,MainEnd.timesPlayerAFK.getOrDefault(player,0) + 1);
                        if (MainEnd.timesPlayerAFK.getOrDefault(player,0)==20) {
                            player.sendMessage(MainCore.prefixCmd + "§6Tu vas finir par t'engourdir à bouger si peu.");
                        } else if (MainEnd.timesPlayerAFK.getOrDefault(player,0)==28) {
                            player.sendMessage(MainCore.prefixCmd + "§6Il est temps de bouger, §ctu sembles afk§6.");
                        } else if (MainEnd.timesPlayerAFK.getOrDefault(player,0)==36) {
                            player.sendMessage(MainCore.prefixCmd +
                                    "§cTu sera éjecté dans 1 minute pour afk si tu ne bouges pas plus.");
                        } else if (MainEnd.timesPlayerAFK.getOrDefault(player,0)>=40) {
                            Bukkit.getScheduler().runTask(MainEnd.getMainEnd(),()->
                                    player.kickPlayer(MainCore.prefixCmd + System.lineSeparator() +
                                            "§cVous avez été kick pour la raison suivante:" + System.lineSeparator() +
                                            "§eKick pour AFK."));
                            JedisPub.sendRedis("log_sanction#:#kick#:#" +
                                    MainCore.profilHashMap.get(player.getUniqueId()).getDiscordid() +
                                    "#:#console#:#null#:#null#:#Kick pour AFK#:#/kick " + player.getName() + " Kick pour AFK");
                        }
                    } else MainEnd.timesPlayerAFK.remove(player);
                    MainEnd.lastPlayerLocation.put(player,player.getLocation());
                }
            }
        }.runTaskTimerAsynchronously(MainEnd.getMainEnd(), 0L, 300L);
    }
}
