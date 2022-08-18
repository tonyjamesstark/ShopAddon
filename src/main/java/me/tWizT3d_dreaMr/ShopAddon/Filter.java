package me.tWizT3d_dreaMr.ShopAddon;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.snowgears.shop.shop.ShopType;

public class Filter {
	private String shoptype, name, lore, listType, title, friendlyMinA, friendlyMaxA, friendlyMinP, friendlyMaxP;
	private Material material;
	private Double minRat, maxRat;
	private boolean priceMin, priceMax, isInvalid;
public Filter(String shoptype, String name, String lore, String listType,String title,
		String mat, String minString, String maxString) {
	this.shoptype=shoptype.toUpperCase();
	this.name=name.toUpperCase();
	this.lore=lore.toUpperCase();
	this.listType=listType.toUpperCase();
	this.title=title;
	this.material=Material.getMaterial(mat);
	priceMin= !minString.isBlank();
	priceMax= !maxString.isBlank();
	minRat=priceMin? getRatio(minString ,false): 0.0;
	minRat=priceMax? getRatio(minString ,false): 0.0;
	isInvalid= this.material==null;
	isInvalid= isInvalid ? true : (priceMin && priceMax);
	if(isInvalid) Bukkit.getLogger().log(Level.INFO, this.title+" is Invalid");
}
private Double getRatio(String s, boolean max) {
	String[] nums=s.split(" ");
	boolean valid =nums.length!=2;
	if(valid) valid=(Integer.valueOf(nums[0])==null || Integer.valueOf(nums[1])== null);
	if(valid) {
		if(max) 
			priceMax=false;
		else 
			priceMin=false;
		return 0.0;
	}
	Double price=0.0+Integer.valueOf(nums[0]);
	Double amount=0.0+Integer.valueOf(nums[1]);

	if(max) {
		friendlyMaxA=""+amount;
		friendlyMaxP=""+price;
	}
	else {
		friendlyMinA=""+amount;
		friendlyMinP=""+price;
		}
	return amount/price;
}
public boolean valid() {
	return isInvalid;
}
public String getFriendlyMinAmount() {
	return friendlyMinA;
}
public String getFriendlyMinPrice() {
	return friendlyMinP;
}
public String getFriendlyMaxAmount() {
	return friendlyMaxA;
}
public String getFriendlyMaxPrice() {
	return friendlyMaxP;
}
public boolean valid(ShopType st) {
	return shoptype.equals("ALL") || ShopType.valueOf(shoptype)==st;
}
public boolean MaterialSame(Material mat) {
	return mat==material;
}
public boolean NameContains(String s) {
	return name.isBlank() || s.toUpperCase().contains(name);
}
public boolean LoreContains(String s) {
	return lore.isBlank() || s.toUpperCase().contains(lore);
}
public String Title() {
	return title;
}
public String ListType() {
	return listType;
}
public boolean minCheck(Double d) {
	return priceMin ? d>=minRat:true;
}
public boolean maxCheck(Double d) {
	return priceMax ? d<=maxRat:true;
}

}
