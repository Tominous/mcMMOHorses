package com.blueskullgames.horserpg.configs;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.blueskullgames.horserpg.HorseRPG;
import com.blueskullgames.horserpg.RPGHorse;

@SuppressWarnings("deprecation")
public class HorseConfigHandler {

	private final File file;
	private final FileConfiguration config;

	public HorseConfigHandler(File file) {
		this.file = file;
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.config = YamlConfiguration.loadConfiguration(file);
	}

	public void setGlobalVar(String path, Object var) {
		config.set("Global" + path, var);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean containsGlobalVariable(String path) {
		return config.contains("Global" + path);
	}

	public Object getGlobalVariable(String path) {
		return config.get("Global" + path);
	}

	public void setVariable(RPGHorse horse, String path, Object var) {
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + path, var);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object getVariable(RPGHorse horse, String path) {
		return config.get("Horses." + horse.owner + "." + horse.rpgUUID.toString() + path);
	}

	public boolean anyOwners() {
		return config.contains("Horses");
	}

	public Set<String> getOwners() {
		return config.getConfigurationSection("Horses").getKeys(false);
	}

	public Set<String> getHorses(String owner) {
		return config.getConfigurationSection("Horses." + owner).getKeys(false);
	}

	public void saveHorse(RPGHorse horse,boolean save) {
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.name, horse.name);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.wrath, horse.wrath.xp);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.agility, horse.agility.xp);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.swiftness, horse.swiftness.xp);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.vitality, horse.vitality.xp);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.color, horse.color.name());
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.isdead, horse.isDead);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.godmode, horse.godmode);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.powerlevel, horse.powerLevel);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.style, horse.style.name());
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.variant, horse.variant.name());
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.hassaddle, horse.hasSaddle);
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.sex, horse.isMale);
		if(horse.hasChest)
		config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.hasChest, horse.hasChest);
		if (horse.horse != null && !horse.isDead && !horse.isBanished) {
			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.entityslastUUID,
					horse.horse.getUniqueId().toString());
			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.entityslastWorld,
					horse.horse.getWorld().getName());

			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.sprintPow, horse.generic_speed);
			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.jumpPow, horse.generic_jump);
			try {
				config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.inventory,
						((HorseInventory) ((AbstractHorse) horse.horse).getInventory()).getContents());
			} catch (Exception | Error e) {
				try {
					config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.inventory,
							((HorseInventory) ((Horse) horse.horse).getInventory()).getContents());
				} catch (Exception | Error e2) {
					config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.inventory,
							((Inventory) ((AbstractHorse) horse.horse).getInventory()).getContents());
				}
			}

		} else {
			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.entityslastUUID, null);
			config.set("Horses." + horse.owner + "." + horse.rpgUUID.toString() + Keys.entityslastWorld, null);
		}
		if(save)
			save();
	}
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeHorse(RPGHorse horse) {
		setVariable(horse, "", null);
	}

	public RPGHorse getHorse(final String owner, String horseUUID) {
		for (final String rpguuids : config.getConfigurationSection("Horses." + owner).getKeys(false)) {
			if (rpguuids.equalsIgnoreCase(horseUUID)) {
				Color c = Color.DARK_BROWN;

				if (config.contains("Horses." + owner + "." + rpguuids + Keys.color)) {
					c = Color.valueOf((config.getString("Horses." + owner + "." + rpguuids + Keys.color)));
				}
				Style s = Style.NONE;

				if (config.contains("Horses." + owner + "." + rpguuids + Keys.style)) {
					s = Style.valueOf((config.getString("Horses." + owner + "." + rpguuids + Keys.style)));
				}
				Variant v = Variant.HORSE;
				if (config.contains("Horses." + owner + "." + rpguuids + Keys.variant)) {
					v = Variant.valueOf(config.getString("Horses." + owner + "." + rpguuids + Keys.variant));
				}
				boolean gm = config.getBoolean("Horses." + owner + "." + rpguuids + Keys.godmode);
				int sswift = config.getInt("Horses." + owner + "." + rpguuids + Keys.swiftness);
				int sagil = config.getInt("Horses." + owner + "." + rpguuids + Keys.agility);
				int svit = config.getInt("Horses." + owner + "." + rpguuids + Keys.vitality);
				int swrath = config.getInt("Horses." + owner + "." + rpguuids + Keys.wrath);
				String horsename = config.getString("Horses." + owner + "." + rpguuids + Keys.name);
				UUID uuid = UUID.fromString(rpguuids);

				double jumpPow = config.contains("Horses." + owner + "." + rpguuids + Keys.jumpPow)
						? config.getInt("Horses." + owner + "." + rpguuids + Keys.jumpPow)
						: 2.25;
				double sprintPow = config.contains("Horses." + owner + "." + rpguuids + Keys.sprintPow)
						? config.getInt("Horses." + owner + "." + rpguuids + Keys.sprintPow)
						: 2.25;
						
						boolean sex =config.contains("Horses." + owner + "." + rpguuids + Keys.sex)
								? config.getBoolean("Horses." + owner + "." + rpguuids + Keys.sex)
										: Math.random()<0.5;

				final RPGHorse rpgHorse = new RPGHorse(horsename, owner, c, s, v, gm, sswift, sagil, svit, swrath, uuid,
						jumpPow, sprintPow,sex);
				if (config.contains("Horses." + owner + "." + rpguuids + "."+Keys.hasChest)) {
					rpgHorse.setHasChest(config.getBoolean("Horses." + owner + "." + rpguuids +"."+ Keys.hasChest));
				}

				if (config.contains("Horses." + owner + "." + rpguuids + Keys.inventory.toString())) {
					@SuppressWarnings("unchecked")
					Object[] dumptemp = ((List<ItemStack>) config
							.get("Horses." + owner + "." + rpguuids + Keys.inventory.toString())).toArray();
					ItemStack[] dymbtemp2 = new ItemStack[dumptemp.length];
					for (int i = 0; i < dumptemp.length; i++) {
						dymbtemp2[i] = (ItemStack) dumptemp[i];
					}
					rpgHorse.inventory = dymbtemp2;
				}
				if (config.contains("Horses." + owner + "." + rpguuids + Keys.entityslastUUID.toString())) {
					final UUID uuid2 = UUID
							.fromString(config.getString("Horses." + owner + "." + rpguuids + Keys.entityslastUUID));
					rpgHorse.isBanished = false;
					new BukkitRunnable() {

						@Override
						public void run() {
							World world = Bukkit.getWorld(
									config.getString("Horses." + owner + "." + rpguuids + Keys.entityslastWorld));
							for (Entity e : world.getEntities()) {
								if (e.getUniqueId().equals(uuid2)) {
									rpgHorse.horse = e;
									break;
								}
							}
							HorseRPG.hSpawnedHorses.put(rpgHorse.horse, rpgHorse);
							if (Bukkit.getPlayer(owner) != null)
								HorseRPG.pCurrentHorse.put(Bukkit.getPlayer(owner), rpgHorse);
						}
					}.runTaskLater(HorseRPG.instance, 2);
				}
				rpgHorse.hasSaddle = config.getBoolean("Horses." + owner + "." + rpguuids + Keys.hassaddle);
				System.out.println("Loading " + rpgHorse.name);
				return rpgHorse;
			}
		}
		System.out.println("Could not load horse");
		return null;
	}

	public enum Keys {
		G_dbtransfermode(".savetype"), G_DisableBreeding(".disable_basegame_breeding"), G_banishOnDisable(
				".banish_on_disable"), G_ClameOnTame(".Force_claim_on_tame"), G_banishonquit(".banish_on_player_quit"), G_nobanish(
						".disable_banishment"),G_horseCost(".priceForHorse"), entityslastUUID(".lastUUIDinstance"), entityslastWorld(
								".lastWorldname"), name(".name"), godmode(".godmode"), inventory(
										".inventory"), hassaddle(".hassaddle"), wrath(".wrath"), agility(
												".agility"), swiftness(".swiftness"), vitality(".vitality"), color(
														".color"), isdead(".isdead"), powerlevel(".powerlevel"), style(
																".style"), variant(".variant"), jumpPow(
																		".jumpPow"), sprintPow(".sprintPow"),sex(".sex"),hasChest("hasChest");
		private String n;

		private Keys(String name) {
			n = name;
		}

		@Override
		public String toString() {
			return n;
		}
	}
}
