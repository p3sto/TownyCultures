package com.gmail.goosius.townycultures.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CultureType {
	POLITICAL("polticial"),
	RELIGIOUS("religious"),
	SOCIAL("social"),
	;
	private final String id;


	CultureType(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public static CultureType fromId(String id) {
		switch(id) {
			case "political": return CultureType.POLITICAL;
			case "religious": return CultureType.RELIGIOUS;
			case "social": return CultureType.SOCIAL;
			default: return null;
		}
	}

	public static List<String> tabCompletions() {
		return Arrays.asList(values()).stream().map(CultureType::id).collect(Collectors.toList());
	}
}
