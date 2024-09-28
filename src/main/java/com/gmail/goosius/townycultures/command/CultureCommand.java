package com.gmail.goosius.townycultures.command;

import com.gmail.goosius.townycultures.TownyCultures;
import com.gmail.goosius.townycultures.enums.CultureType;
import com.gmail.goosius.townycultures.enums.TownyCulturesPermissionNodes;
import com.gmail.goosius.townycultures.events.PreCultureSetEvent;
import com.gmail.goosius.townycultures.metadata.ResidentMetaDataController;
import com.gmail.goosius.townycultures.utils.CultureUtil;
import com.gmail.goosius.townycultures.utils.Messaging;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.BukkitTools;
import com.palmergames.bukkit.util.ChatTools;
import com.palmergames.util.StringMgmt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CultureCommand extends BaseCommand implements TabExecutor {

	private static final List<String> residentCulturesTabComplete = Arrays.asList("create", "invite", "join", "leave");

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args[0].toLowerCase()) {
			case "create":
				if (args.length == 3)
					return NameUtil.filterByStart(CultureType.tabCompletions(), args[2]);
			case "invite":
				if (args.length == 2)
					return getTownyStartingWith(args[1], "r");
			case "join":
				if (args.length == 2)
					return NameUtil.filterByStart(TownyCultures.getManager().getCultureList(), args[1]);
			case "leave":
			default:
				return Collections.emptyList();
		}
	}

	private void showCultureHelp(CommandSender sender) {
		sender.sendMessage(ChatTools.formatTitle("/culture"));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/culture create", "[name] [type]", "Create a new culture."));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/culture invite", "[resident]", "Invite a resident to your culture."));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/culture join", "[name]", "Join an existing culture."));
		sender.sendMessage(ChatTools.formatCommand("Eg", "/culture leave", "", "Leave your culture."));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player player) {
			try {
				parseSetCultureCommand(player, args);
			} catch (TownyException e) {
				Messaging.sendErrorMsg(player, e.getMessage(player));
			}
		} else
			showCultureHelp(sender);

		return true;
	}

	private void parseSetCultureCommand(Player player, String[] args) throws TownyException {
		checkPermOrThrow(player, TownyCulturesPermissionNodes.TOWNYCULTURES_COMMAND_SET_TOWN_CULTURE.getNode());

		Resident resident = getResidentOrThrow(player);
		Town town = getTownFromResidentOrThrow(resident);

		String newCulture = CultureUtil.validateCultureName(StringMgmt.join(args, " "));

		if (newCulture == null)
			throw new TownyException(Translatable.of("msg_err_invalid_string_town_culture_not_set"));

		//Fire cancellable event.
		BukkitTools.ifCancelledThenThrow(new PreCultureSetEvent(newCulture, town));

		//Set town culture
		ResidentMetaDataController.setTownCulture(town, newCulture);
		if (newCulture.isEmpty())
			Messaging.sendPrefixedTownMessage(town, Translatable.of("msg_culture_removed"));
		else
			Messaging.sendPrefixedTownMessage(town, Translatable.of("msg_town_culture_set", StringMgmt.capitalize(newCulture)));
	}
}