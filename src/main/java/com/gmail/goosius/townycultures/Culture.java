package com.gmail.goosius.townycultures;


import com.gmail.goosius.townycultures.enums.CultureType;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyObject;

import java.util.List;

public class Culture extends TownyObject {
	private CultureType type;
	private String founder;
	private double joinCost;
	private List<String> lore;
	private TownBlock hub;
	private boolean inviteOnly;

	public Culture(String name) {
		super(name);
	}

	public Resident getFounder() {
		return TownyUniverse.getInstance().getResident(founder);
	}

	public void setFounder(String founder) {
		this.founder = founder;
	}

	public List<String> getLore() {
		return lore;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public double getJoinCost() {
		return joinCost;
	}

	public void setJoinCost(double joinCost) {
		this.joinCost = joinCost;
	}

	public CultureType getType() {
		return type;
	}

	public void setType(CultureType type) {
		this.type = type;
	}

	public TownBlock getHeadquarters() {
		return hub;
	}

	public void setHeadquarters(TownBlock headquarters) {
		this.hub = headquarters;
	}

	public TownBlock getHub() {
		return hub;
	}

	public void setHub(TownBlock hub) {
		this.hub = hub;
	}

	public boolean isInviteOnly() {
		return inviteOnly;
	}

	public void setInviteOnly(boolean inviteOnly) {
		this.inviteOnly = inviteOnly;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public void save() {
		TownyCultures.getManager().saveCulture(this);
	}
}
