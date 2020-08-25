package de.marcely.pcel;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {
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
}
