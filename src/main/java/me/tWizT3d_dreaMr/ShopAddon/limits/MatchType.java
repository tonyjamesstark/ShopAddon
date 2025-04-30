package me.tWizT3d_dreaMr.ShopAddon.limits;

import me.tWizT3d_dreaMr.ShopAddon.util.Filter;

public class MatchType {
	private String type;
	private Filter filter;

	public MatchType(String type, Filter filter) {
		this.type = type;
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public String getType() {
		return type;
	}
}
