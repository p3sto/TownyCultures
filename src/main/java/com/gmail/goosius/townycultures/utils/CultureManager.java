package com.gmail.goosius.townycultures.utils;

import com.gmail.goosius.townycultures.Culture;
import com.gmail.goosius.townycultures.TownyCultures;
import com.gmail.goosius.townycultures.enums.CultureType;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.db.FlatFileSaveTask;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.object.metadata.DataFieldIO;
import com.palmergames.bukkit.towny.object.metadata.MetadataLoader;
import com.palmergames.bukkit.towny.scheduling.ScheduledTask;
import com.palmergames.util.FileMgmt;
import com.palmergames.util.StringMgmt;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class CultureManager {
	private static final TownyUniverse universe = TownyUniverse.getInstance();
	private final Map<String, Culture> culturesMap;
	private final Queue<Runnable> queryQueue;
	private final ScheduledTask task;

	public CultureManager(Towny towny) {
		this.culturesMap = new HashMap<>();
		this.queryQueue = new ConcurrentLinkedQueue<>();
		this.task = towny.getScheduler().runAsyncRepeating(() -> {
			synchronized (queryQueue) {
				while (!this.queryQueue.isEmpty()) {
					Runnable operation = this.queryQueue.poll();
					operation.run();
				}
			}
		}, 5L, 5L);
	}

	/**
	 * Completes all save tasks
	 */
	public void completeTasks() {
		synchronized (this.queryQueue) {
			if (task != null)
				task.cancel();

			while (!this.queryQueue.isEmpty()) {
				Runnable operation = this.queryQueue.poll();
				operation.run();
			}
		}
	}


	public List<String> getCultureList() {
		return new ArrayList<>(culturesMap.keySet());
	}

	/**
	 * Loads the culture map
	 */
	public void loadCultures() {
		File[] files = new File(getCulturesFolder())
				.listFiles(f -> f.getName().toLowerCase(Locale.ROOT).endsWith(".txt"));

		if (files != null) {
			String line;
			Map<String, String> map;
			Culture culture;
			String name;

			for (File file : files) {
				map = FileMgmt.loadFileIntoHashMap(file);
				name = file.getName().replace(".txt", "");
				culture = new Culture(name);

				line = map.get("founder");
				if (line != null)
					culture.setFounder(line);

				line = map.get("culture");
				if (line != null)
					culture.setType(CultureType.fromId(line));

				line = map.get("joinCost");
				if (line != null)
					try {
						culture.setJoinCost(Double.parseDouble(line));
					} catch (Exception ignore) {
						culture.setJoinCost(0);
					}

				line = map.get("hub");
				if (line != null && !line.isEmpty()) {
					String[] tokens = line.split(",");
					if (tokens.length == 3) {
						TownyWorld world = universe.getWorld(tokens[0]);
						if (world == null)
							TownyCultures.info(Translatable.of("msg_err_invalid_townyworld", tokens[0]).defaultLocale());
						else {
							try {
								int x = Integer.parseInt(tokens[1]);
								int z = Integer.parseInt(tokens[2]);
								TownBlock homeBlock = universe.getTownBlock(new WorldCoord(world.getName(), x, z));
								culture.setHeadquarters(homeBlock);
							} catch (NumberFormatException e) {
								TownyMessaging.sendErrorMsg(Translation.of("flatfile_err_homeblock_load_invalid_location", name));
							} catch (NotRegisteredException e) {
								TownyMessaging.sendErrorMsg(Translation.of("flatfile_err_homeblock_load_invalid_townblock", name));
							}
						}
					}
				}

				line = map.get("metadata");
				if (line != null && !line.isEmpty())
					MetadataLoader.getInstance().deserializeMetadata(culture, line.trim());

				line = map.get("lore");
				if (line != null && !line.isEmpty()) {
					List<String> list = Arrays.stream(line.split(","))
							.collect(Collectors.toList());
					culture.setLore(list);
				}
				culturesMap.put(name, culture);
			}
		}
	}

	/**
	 * Saves a single culture to its flat file
	 *
	 * @param culture The culture object
	 */
	public void saveCulture(Culture culture) {
		if (!checkCulturesFolder() || !checkCultureFile(culture)) {
			TownyMessaging.sendErrorMsg("Could not save culture: " + culture.getName());
			return;
		}
		List<String> list = new ArrayList<>();
		list.add("type=" + culture.getType().id());
		list.add("founder=" + culture.getFounder().getName());
		list.add("joinCost=" + culture.getJoinCost());

		String lore = StringMgmt.join(culture.getLore(), ",");
		list.add("hub=" + culture.getHeadquarters().toString());
		list.add("lore=" + lore);
		list.add("metadata=" + DataFieldIO.serializeCDFs(culture.getMetadata()));
		this.queryQueue.add(new FlatFileSaveTask(list, getCultureFile(culture)));
	}

	public void renameCulture(Culture culture, String oldName) {

	}

	private static String getCulturesFolder() {
		return TownyUniverse.getInstance().getRootFolder() + File.separator + "data" + File.separator + "cultures";
	}

	private static String getCultureFile(Culture culture) {
		return getCulturesFolder() + File.separator + culture.getName() + ".txt";
	}

	private static boolean checkCulturesFolder() {
		String path = getCulturesFolder();
		return FileMgmt.checkOrCreateFolders(path);
	}

	private static boolean checkCultureFile(Culture culture) {
		String path = getCultureFile(culture);
		return FileMgmt.checkOrCreateFile(path);
	}
}
