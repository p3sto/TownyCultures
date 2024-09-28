package com.gmail.goosius.townycultures;

import com.gmail.goosius.townycultures.utils.CultureManager;

public class TownyCulturesAPI {
	private static TownyCulturesAPI api;
	private final CultureManager manager;

	private TownyCulturesAPI() {
		this.manager = TownyCultures.getManager();
	}

	public static TownyCulturesAPI getInstance() {
		if (api == null) {
			api = new TownyCulturesAPI();
		}
		return api;
	}





}
