package me.aberdeener.vaultgames.commands;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.aberdeener.vaultgames.VaultGames;
import me.aberdeener.vaultgames.games.TNTRun;

public class GameCommand implements CommandExecutor {

	public static GameCommand instance;

	// lets us use this instance to load coords, reload config etc
	public static GameCommand getInstance() {
		return instance;
	}

	public static HashMap<UUID, Player> tntRunTotal = new HashMap<>();
	public static HashMap<UUID, Player> tntRunRemaining = new HashMap<>();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		String string = VaultGames.vcc.getString("string");
		String variable1 = VaultGames.vcc.getString("variable-1");
		String variable2 = VaultGames.vcc.getString("variable-2");

		// base command
		if (commandLabel.equalsIgnoreCase("game")) {

			// console sender check
			if (!(sender instanceof Player)) {
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', VaultGames.vcc.getString("console-error")));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				player.sendMessage(
						ChatColor.DARK_GREEN + "Correct Usage: " + ChatColor.RED + "/game <join|leave|setup>");
				return true;
			}

			else if (args.length > 0) {

				if (args[0].equalsIgnoreCase("join")) {

					if (!player.hasPermission("vg.player")) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', VaultGames.vcc.getString("no-permission")));
						return true;
					}

					if (args.length == 2) {

						if (args[1].equalsIgnoreCase("TNTRun")) {
							if (TNTRun.playing == true) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										string + "A game is already in progress, please try again later."));
								return true;
							}

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', string
									+ "You have been added to the " + variable1 + "TNTRun" + string + " queue."));
							tntRunTotal.put(player.getUniqueId(), player);
							tntRunRemaining.put(player.getUniqueId(), player);
							return true;
						}

						// add more here in the future

						else {
							player.sendMessage(ChatColor.RED + "That game does not exist!");
							return true;
						}
					}

					else {
						player.sendMessage(
								ChatColor.DARK_GREEN + "Correct Usage: " + ChatColor.RED + "/game join <game>");
						return true;
					}
				}

				if (args[0].equalsIgnoreCase("leave")) {

					if (!player.hasPermission("vg.player")) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', VaultGames.vcc.getString("no-permission")));
						return true;
					}

					else {

						if (tntRunTotal.containsKey(player.getUniqueId())) {
							player.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());
							player.sendMessage(ChatColor.translateAlternateColorCodes('&',
									string + "Teleporting you to the lobby..."));
							tntRunTotal.remove(player.getUniqueId());
							tntRunRemaining.remove(player.getUniqueId());
							int remaining = tntRunRemaining.size();
							if (remaining > 0) {

								for (Player players : tntRunTotal.values()) {

									players.sendMessage(ChatColor.translateAlternateColorCodes('&',
											variable1 + player.getName() + string + " has " + ChatColor.RED + "left"
													+ string + " the game."));
									players.sendMessage(ChatColor.translateAlternateColorCodes('&',
											string + "Only " + variable2 + remaining + string + " players remaining!"));
									return true;
								}

							}

							else {
								GameCommand.tntRunTotal.clear();
								GameCommand.tntRunRemaining.clear();

								Bukkit.unloadWorld("tnt", false);

								Bukkit.getServer().createWorld(new WorldCreator("tnt"));
								TNTRun.playing = false;
								return true;
							}
							return true;
						}

						else {
							player.sendMessage(ChatColor.RED + "You are not in a game!");
							return true;
						}

					}
				}

				if (args[0].equalsIgnoreCase("setup")) {

					if (!player.hasPermission("vg.admin")) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', VaultGames.vcc.getString("no-permission")));
						return true;
					}

					if (args.length == 2) {

						String game = args[1];

						if (VaultGames.getInstance().gameData.contains(game)) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&',
									string + "Available settings for " + variable1 + game + string + ": " + variable2
											+ "minPlayers" + string + "."));
							return true;
						}

						else {
							player.sendMessage(ChatColor.RED + "That game does not exist!");
							return true;
						}
					}

					if (args.length == 4) {

						String game = args[1];

						String setting = args[2];

						String value = args[3];

						if (VaultGames.getInstance().gameData.contains(game)) {

							if (setting.equals("minPlayers")) {

								try {
									Integer.parseInt(args[3]);
									// is an int
									int valueInt = Integer.parseInt(args[3]);
									VaultGames.getInstance().gameData.set(game + "." + setting, valueInt);
									VaultGames.getInstance().saveGameData();
									player.sendMessage(ChatColor.translateAlternateColorCodes('&',
											string + "Set the " + variable1 + setting + string + " to " + variable1
													+ value + string + " for the game " + variable1 + game + string
													+ "."));
									return true;
								}

								catch (NumberFormatException e) {
									// not an int :(
									player.sendMessage(ChatColor.RED + "Please enter a number!");
									return true;
								}
							}

							else {
								player.sendMessage(ChatColor.RED + "That setting does not need the last argument!");
								return true;
							}
						}

						else {
							player.sendMessage(ChatColor.RED + "That game does not exist!");
							return true;
						}
					}

					else {
						player.sendMessage(ChatColor.DARK_GREEN + "Correct Usage: " + ChatColor.RED
								+ "/game setup <game> <setting> [value]");
						return true;
					}
				}

				else {
					player.sendMessage(
							ChatColor.DARK_GREEN + "Correct Usage: " + ChatColor.RED + "/game <join|leave|setup>");
					return true;
				}
			}

			else {
				player.sendMessage(
						ChatColor.DARK_GREEN + "Correct Usage: " + ChatColor.RED + "/game <join|leave|setup>");
				return true;
			}
		}

		return true;
	}
}