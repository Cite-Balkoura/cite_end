package fr.milekat.cite_end.event;

import fr.milekat.cite_core.MainCore;
import fr.milekat.cite_core.core.bungee.ServersManagerSendPlayer;
import fr.milekat.cite_end.MainEnd;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EndEvent implements Listener {
    @EventHandler
    public void onVoidFell(PlayerMoveEvent event) {
        if (event.getTo()!=null && event.getTo().getY()<=0) {
            Location location = event.getFrom().clone();
            location.setY(250);
            new ServersManagerSendPlayer().sendPlayerToServer(event.getPlayer(),
                    MainEnd.lastPlayerSurvie.getOrDefault(event.getPlayer(),"Prague"),"world",location);
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
        player.teleport(MainCore.locLabels.getOrDefault("end_spawn",
                new Location(Bukkit.getWorld("world_the_end"),0,150,0)));
        new ServersManagerSendPlayer().sendPlayerToServer(player,
                MainEnd.lastPlayerSurvie.getOrDefault(player,"Prague"),"respawn");
    }
}
