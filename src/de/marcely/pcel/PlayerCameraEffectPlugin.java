package de.marcely.pcel;

import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayOutCamera;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class PlayerCameraEffectPlugin extends JavaPlugin implements Listener, CommandExecutor {
	
	private static final Set<Player> MAKING_PLAYERS = new HashSet<>();
	
	@Override
	public void onEnable(){		
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("pce").setExecutor(this);
	}
	
	@Override
	public void onDisable() {
		MAKING_PLAYERS.clear();
	}
	
	public void playEffect(final Player p, CameraEffect cam){
		if(!MAKING_PLAYERS.contains(p)) {
			MAKING_PLAYERS.add(p);

			final Location pLoc = p.getLocation().clone();
			Entity entityToSpectate = p.getWorld().spawnEntity(pLoc, cam.getEntityType());

			p.setGameMode(GameMode.SPECTATOR);
			sendCameraPacket(p, entityToSpectate);

			entityToSpectate.remove();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args){
		if(sender instanceof Player || args.length < 1) {
			return false;
		}

		Player p = Bukkit.getPlayer(args[1]);

		if(p != null && !p.isDead()) {
			if(args[0].equalsIgnoreCase("remove")) {

				Location loc = p.getLocation();
				float xp = p.getExp();

				playEffect(p, CameraEffect.NORMAL);

				p.setGameMode(GameMode.SURVIVAL);
				p.damage(100);

				forceRespawn(p);
				p.teleport(loc);
				p.setExp(xp);

				MAKING_PLAYERS.remove(p);
				return true;
			}

			CameraEffect cam = CameraEffect.getByName(args[0]);

			if(cam != null) {
				playEffect(p, cam);
				return true;
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(InventoryOpenEvent e) {
		if(MAKING_PLAYERS.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerMoveEvent e) {
		if(MAKING_PLAYERS.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerQuitEvent e) {
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		MAKING_PLAYERS.remove(e.getPlayer());
	}

	private void sendCameraPacket(Player p, Entity e){
		PacketPlayOutCamera packet = new PacketPlayOutCamera(((CraftEntity) e).getHandle());
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	private void forceRespawn(Player p) {
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer) p).getHandle().playerConnection.a(packet);
	}
}
