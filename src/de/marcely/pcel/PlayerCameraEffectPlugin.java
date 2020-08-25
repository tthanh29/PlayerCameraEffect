package de.marcely.pcel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.marcely.pcel.versions.Version;

public class PlayerCameraEffectPlugin extends JavaPlugin implements Listener, CommandExecutor {
	
	private static final List<Player> MAKING_PLAYERS = new ArrayList<Player>();
	
	@Override
	public void onEnable(){		
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("pce").setExecutor(this);
		
		Version.onEnable();
	}
	
	@Override
	public void onDisable() {
		MAKING_PLAYERS.clear();
	}
	
	public static void playEffect(final Player player, CameraEffect effect){
		// prepare
		MAKING_PLAYERS.add(player);
		
		// backup inventory
		final GameMode gm = player.getGameMode();
		final ItemStack[] invIs1 = player.getInventory().getContents();
		final ItemStack[] invIs2 = player.getInventory().getArmorContents();
		final double invHealth = player.getHealthScale();
		final int invFood = player.getFoodLevel();
		final GameMode invGM = player.getGameMode();
		final boolean invAllowFlight = player.getAllowFlight();
		final boolean invFlight = player.isFlying();
		final float invExp = player.getExp();
		final Vector invVelocity = player.getVelocity();
		
		// spawn entity
		final Location playerloc = player.getLocation().clone();
		Entity e = player.getWorld().spawnEntity(playerloc, effect.getEntityType());
		
		// send effect
		player.setGameMode(GameMode.SPECTATOR);
		Version.current.getHandler().sendCameraPacket(player, e);
		
		// kill entity
		e.remove();
		
		// clear inventory
		player.getInventory().clear();
		player.setExp(0);
		
		// kill & respawn player
		player.setGameMode(gm);
		player.damage(100);
		if(player.getHealth() > 0)
			player.setHealth(0);
		Version.current.getHandler().forceRespawn(player);
		
		// teleport him back
		Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerCameraEffectPlugin.plugin, new Runnable(){
			@Override
			public void run() {
				player.teleport(playerloc);
				
				// give him his items back
				player.getInventory().setContents(invIs1);
				player.getInventory().setArmorContents(invIs2);
				player.setHealthScale(invHealth);
				player.setFoodLevel(invFood);
				player.setGameMode(invGM);
				player.setAllowFlight(invAllowFlight);
				player.setFlying(invFlight);
				player.setExp(invExp);
				player.setVelocity(invVelocity);
				
				// done
				MAKING_PLAYERS.remove(player);
			}
		}, 5);
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args){
		if(sender instanceof Player || args.length < 1) {
			return false;
		}
		
		CameraEffect cam = CameraEffect.getCameraEffect(args[0]);
		Player player = Bukkit.getPlayer(args[1]);
		
		if(cam != null && player != null) {
			PlayerCameraEffectPlugin.playEffect(player, effect);
			return true;
		}

		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event){
		for(Player player : MAKING_PLAYERS){
			if(PlayerCameraEffectPlugin.equals(player.getLocation(), event.getLocation())){
				event.setCancelled(false);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event){
		for(Player player : MAKING_PLAYERS){
			if(PlayerCameraEffectPlugin.equals(player.getLocation(), event.getTo())){
				event.setCancelled(false);
				return;
			}
		}
	}

	
	private static boolean equals(Location loc1, Location loc2){
		return loc1.getWorld().equals(loc2.getWorld()) &&
			   loc1.getX() == loc2.getX() &&
			   loc1.getY() == loc2.getY() &&
			   loc1.getZ() == loc2.getZ();
	}
}
