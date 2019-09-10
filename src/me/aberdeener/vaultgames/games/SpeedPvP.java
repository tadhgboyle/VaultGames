package me.aberdeener.vaultgames.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import me.aberdeener.vaultgames.API;
import me.aberdeener.vaultgames.VaultGames;
import me.aberdeener.vaultgames.commands.GameCommand;

public class SpeedPvP implements Listener {

	public static boolean playing = false;

	String string = VaultGames.vcc.getString("string");
	String variable1 = VaultGames.vcc.getString("variable-1");
	String variable2 = VaultGames.vcc.getString("variable-2");

	@EventHandler
	public void onPLayerTP(PlayerTeleportEvent event) {

		Player player = event.getPlayer();

		World worldTo = event.getTo().getWorld();

		if (worldTo.getName().equalsIgnoreCase("pvp")) {
			if (GameCommand.SpeedPvPPlaying.containsKey(player.getUniqueId())) {
				if (playing == false) {
					player.sendTitle(ChatColor.translateAlternateColorCodes('&', string + "Welcome to"),
							(ChatColor.translateAlternateColorCodes('&', variable1 + "Speed PvP")), 10, 70, 10);
					player.getInventory().addItem(new ItemStack(Material.MUSHROOM_STEW, 8));
					player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
					player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 4));
					return;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {

		Player dead = event.getEntity();

		if (GameCommand.SpeedPvPPlaying.containsKey(dead.getUniqueId())) {

			if (dead.getWorld().getName().equalsIgnoreCase("pvp")) {

				GameCommand.SpeedPvPPlaying.remove(dead.getUniqueId());

				dead.sendMessage(ChatColor.RED + "You died!");

				// fix this asap: get only player in hashmap to be used as winner
				Player winner = dead.getKiller();

				for (Player players : Bukkit.getWorld("pvp").getPlayers()) {

					API.GameEnding("SpeedPvP", players, winner, "pvp");
				}
			}
		}
	}

	public static void SpeedPvPQueue() {

		String string = VaultGames.vcc.getString("string");
		String variable1 = VaultGames.vcc.getString("variable-1");
		String variable2 = VaultGames.vcc.getString("variable-2");

		// make scheduler to teleport players to lobby after 5 seconds + clear hashmaps
		VaultGames.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(VaultGames.getInstance(),
				new Runnable() {
					public void run() {

						int queued = GameCommand.SpeedPvPPlaying.size();
						int needed = 2 - queued;

						if (playing == false) {

							if (queued == 0) {
								return;
							}

							if (queued < 2) {
								for (Player players : Bukkit.getOnlinePlayers()) {
									players.sendMessage(ChatColor.translateAlternateColorCodes('&',
											string + "Only " + variable2 + needed + string + " players needed for "
													+ variable1 + "SpeedPvP" + string + "!"));
									return;
								}
							}

							if (queued == 2) {
								API.GameStart("SpeedPvP", "pvp");
							}
						}
					}
				}, 20 * 10); // 20 (one second in ticks) * 5 (seconds to wait)
	}
}