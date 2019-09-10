package me.aberdeener.vaultgames;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import me.aberdeener.vaultgames.commands.GameCommand;
import me.aberdeener.vaultgames.games.SpeedPvP;
import me.aberdeener.vaultgames.games.TNTRun;

public class API {

	static String string = VaultGames.vcc.getString("string");
	static String variable1 = VaultGames.vcc.getString("variable-1");
	static String variable2 = VaultGames.vcc.getString("variable-2");

	public static void GameStart(String gameClass, String gameMap) {
		
		Collection<Player> hashMap = null;
		if (gameClass.equals("SpeedPvP")) {
			SpeedPvP.playing = true;
			hashMap = GameCommand.SpeedPvPPlaying.values();
		}

		if (gameClass.equals("TNTRun")) {
			TNTRun.playing = true;
			hashMap = GameCommand.tntRunTotal.values();
		}
		
		for (Player players : hashMap) {
			players.sendMessage(
					ChatColor.translateAlternateColorCodes('&', variable1 + gameClass + string
							+ " is starting in " + variable1 + "10" + string + " seconds!"));
			VaultGames.getInstance().getServer().getScheduler()
					.scheduleSyncDelayedTask(VaultGames.getInstance(), new Runnable() {
						public void run() {
							players.teleport(Bukkit.getWorld(gameMap).getSpawnLocation());
							players.sendTitle(ChatColor.translateAlternateColorCodes('&', string + "Welcome to"),
									(ChatColor.translateAlternateColorCodes('&', variable1 + gameClass)), 10, 70, 10);
							return;
						}
					}, 20 * 10);
		}		
	}
	
	public static void GameEnding(String gameClass, Player players, Player winner, String gameMap) {
		
		players.sendMessage(ChatColor.translateAlternateColorCodes('&',
				variable1 + winner.getName() + string + " has won the game!"));
		players.sendMessage(ChatColor.translateAlternateColorCodes('&',
				string + "You will be teleported to the Lobby in 5 seconds..."));

		// make scheduler to teleport players to lobby after 5 seconds + clear hashmaps
		VaultGames.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(VaultGames.getInstance(),
				new Runnable() {
					public void run() {
						players.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());

						if (gameClass.equals("SpeedPvP")) {
							GameCommand.SpeedPvPPlaying.clear();
						}

						if (gameClass.equals("TNTRun")) {
							GameCommand.tntRunTotal.clear();
							GameCommand.tntRunRemaining.clear();
						}

						Bukkit.unloadWorld(gameMap, false);

						VaultGames.getInstance().getServer().getScheduler()
								.scheduleSyncDelayedTask(VaultGames.getInstance(), new Runnable() {
									public void run() {

										Bukkit.getServer().createWorld(new WorldCreator(gameMap));

										if (gameClass.equals("SpeedPvP")) {
											SpeedPvP.playing = false;
										}

										if (gameClass.equals("TNTRun")) {
											TNTRun.playing = false;
										}

										return;
									}
								}, 20 * 5);

					}
				}, 20 * 5); // 20 (one second in ticks) * 5 (seconds to wait)
		return;
	}
}