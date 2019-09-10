package me.aberdeener.vaultgames;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.aberdeener.vaultgames.commands.GameCommand;

public class Events implements Listener {

	String string = VaultGames.vcc.getString("string");
	String variable1 = VaultGames.vcc.getString("variable-1");
	String variable2 = VaultGames.vcc.getString("variable-2");

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {

		Player player = (Player) event.getPlayer();

		if (GameCommand.tntRunRemaining.containsKey(player.getUniqueId())
				|| GameCommand.SpeedPvPPlaying.containsKey(player.getUniqueId())) {
			if (event.getMessage().equalsIgnoreCase("/tp") || (event.getMessage().equalsIgnoreCase("/hub") || (event
					.getMessage().equalsIgnoreCase("/lobby")
					|| (event.getMessage().equalsIgnoreCase("/warp") || (event.getMessage().equalsIgnoreCase("/sv")
							|| (event.getMessage().equalsIgnoreCase("/cr")
									|| (event.getMessage().equalsIgnoreCase("/tsv")))))))) {
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', string + "You cannot teleport during the game."));
				player.sendMessage(ChatColor.RED + "To leave the game: " + ChatColor.DARK_GREEN + "/game leave");
				event.setCancelled(true);
				return;
			}
		}
	}
}
