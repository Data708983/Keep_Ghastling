package org.data7.keep_Ghastling;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Keep_Ghastling extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getLogger().info("[Keep_Ghastling] Plugin Enabled");
        getServer().getPluginManager().registerEvents(this, this);
        GhastNoGrowUpListener();
    }

    @Override
    public void onDisable() {
    }

    public void GhastNoGrowUpListener() {
        NamespacedKey key = new NamespacedKey(this,"keepGhastling");
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntitiesByClass(HappyGhast.class)) {
                        HappyGhast ghast = (HappyGhast) entity;
                        if (ghast.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false)) {
                            if (ghast.isAdult()) {
                                ghast.setBaby();
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L); // 每刻运行
    }

    @EventHandler
    public void onPlayerUseIceBlock(PlayerInteractEntityEvent event){
        if (!(event.getRightClicked() instanceof HappyGhast entity)) {
            return;
        }
        if (!entity.isAdult()) {
            NamespacedKey key = new NamespacedKey(this,"keepGhastling");
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            Boolean value = pdc.getOrDefault(key, PersistentDataType.BOOLEAN, false);
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItem(event.getHand());
            if (!value) {
                if (itemInHand != null && itemInHand.getType() == Material.ICE) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    World world = entity.getWorld();
                    Location location = entity.getLocation();
                    world.spawnParticle(Particle.HAPPY_VILLAGER, location.add(0, 1, 0),30, 1.5, 1.5, 1.5, 0.1);
                    world.spawnParticle(Particle.SNOWFLAKE, location.clone().add(0, 1.5, 0), 10, 1, 0.5, 1, 0.05);
                    world.playSound(location, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.0f);
                    pdc.set(key, PersistentDataType.BOOLEAN, true);
                }
            }
            else  {
                if (itemInHand != null && itemInHand.getType() == Material.MAGMA_CREAM) {
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                    World world = entity.getWorld();
                    Location location = entity.getLocation();
                    world.spawnParticle(Particle.LAVA, location.add(0, 1, 0), 20, 1.5, 1.5, 1.5, 0.1);
                    world.spawnParticle(Particle.SMOKE, location.clone().add(0, 1.5, 0), 10, 1, 0.5, 1, 0.05);
                    world.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 0.8f, 1.0f);
                    pdc.set(key, PersistentDataType.BOOLEAN, false);
                }
            }
        }
    }
}
