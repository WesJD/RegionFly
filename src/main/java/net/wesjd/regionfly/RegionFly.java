package net.wesjd.regionfly;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RegionFly extends JavaPlugin implements Listener {

    private List<String> regions;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        regions = getConfig().getStringList("regions");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        final Location to = e.getTo();
        final Location from = e.getFrom();
        if(from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            final Player player = e.getPlayer();

            if(player.getGameMode() != GameMode.CREATIVE) {
                final boolean wasFlying = player.isFlying();
                final boolean fly = isInARegion(to);

                // player has walked into/out of a region
                if(player.getAllowFlight() != fly) {
                    player.setAllowFlight(fly);
                    player.setFlying(fly);
                    if(fly && !wasFlying) player.teleport(player.getLocation().add(0, 0.2, 0));
                }
            }
        }
    }

    private boolean isInARegion(Location location) {
        for(final ProtectedRegion region : WGBukkit.getRegionManager(location.getWorld()).getApplicableRegions(location))
            if(regions.stream().anyMatch(name -> region.getId().equals(name))) return true;
        return false;
    }

}
