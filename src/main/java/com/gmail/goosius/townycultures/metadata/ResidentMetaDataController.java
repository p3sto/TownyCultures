package com.gmail.goosius.townycultures.metadata;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;

/**
 * 
 * @author Goosius
 *
 */
public class ResidentMetaDataController {

	private static StringDataField residentCulture = new StringDataField("townycultures_culture", "");

	public static String getResidentCulture(Resident resident) {
		if (MetaDataUtil.hasMeta(resident, ResidentMetaDataController.residentCulture))
			return MetaDataUtil.getString(town, ResidentMetaDataController.residentCulture);
		else
			return Translatable.of("status_unknown").defaultLocale();
	}

	public static void setTownCulture(Town town, String culture) {
		// Remove old meta and replace with nothing.
		if (MetaDataUtil.hasMeta(town, residentCulture) && culture.isEmpty())
			town.removeMetaData("townycultures_culture", true);
		// Nothing left to do, we either just removed the culture or they had none to begin with.
		if (culture.isEmpty())
			return;
		// Set the new meta.
		MetaDataUtil.addNewStringMeta(town, "townycultures_culture", culture, true);
	}

	public static boolean hasCulture(Resident resident) {
		return MetaDataUtil.hasMeta(resident, residentCulture);
	}


}
