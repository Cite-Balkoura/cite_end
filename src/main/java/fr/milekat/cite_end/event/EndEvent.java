package fr.milekat.cite_end.event;

import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.bungee.ServersManagerSendPlayer;
import fr.milekat.cite_end.MainEnd;
import fr.milekat.cite_libs.MainLibs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EndEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getLocation().getY() <= 0) {
            event.getPlayer().teleport(MainCore.locLabels.getOrDefault("end_spawn",
                    new Location(Bukkit.getWorld("world_the_end"),0,150,0)));
        }
        try {
            Connection connection = MainLibs.getSql();
            PreparedStatement q = connection.prepareStatement(
                    "SELECT COALESCE(`server`, 'survie_prague') as server FROM `balkoura_last_survie` WHERE `uuid` = ?;");
            q.setString(1, event.getPlayer().getUniqueId().toString());
            q.execute();
            if (q.getResultSet().last()) {
                MainEnd.lastPlayerSurvie.put(event.getPlayer(), q.getResultSet().getString("server"));
            } else {
                MainEnd.lastPlayerSurvie.put(event.getPlayer(), "survie_prague");
            }
            q.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public void onVoidFell(PlayerMoveEvent event) {
        if (event.getTo()!=null && event.getTo().getY()<=0) {
            Location location = event.getFrom().clone();
            location.setY(250);
            new ServersManagerSendPlayer().sendPlayerToServer(event.getPlayer(),
                    MainEnd.lastPlayerSurvie.getOrDefault(event.getPlayer(),"Prague"),"world",location);
            event.getPlayer().teleport(MainCore.locLabels.getOrDefault("survie_end_spawn",
                    new Location(Bukkit.getWorld("world_the_end"),0,150,0)));
        }
    }

    @EventHandler
    public void onMoveOverworld(PlayerMoveEvent event) {
        if (event.getTo()!=null && event.getTo().getWorld()!=null &&
                !event.getTo().getWorld().getName().equalsIgnoreCase("world_the_end")) {
            Location location = event.getTo().clone();
            location.setWorld(Bukkit.getWorld("world_the_end"));
            event.getPlayer().teleport(location);
        }
    }

    @EventHandler
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                onEndDeath((Player) event.getEntity());
            }
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).getHealth() - event.getDamage() <= 0) {
                event.setCancelled(true);
                onEndDeath((Player) event.getEntity());
            }
        }
    }

    private void onEndDeath(Player player) {
        player.teleport(MainCore.locLabels.getOrDefault("survie_end_spawn",
                new Location(Bukkit.getWorld("world_the_end"),0,150,0)));
        player.setHealth(20.0);
        player.setFoodLevel(20);
        new ServersManagerSendPlayer().sendPlayerToServer(player,
                MainEnd.lastPlayerSurvie.getOrDefault(player,"survie_prague"),"respawn");
    }
}
