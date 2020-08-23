package me.tWizT3d_dreaMr.ShopAddon;

import java.util.ArrayList;

public class LoggingPlayer {
private ArrayList<String> al;
private int page;
private String pname;
public LoggingPlayer(ArrayList<String> arrayList, int page, String pname) {
	this.al=arrayList;
	this.page=page;
	this.pname=pname;
}
public boolean isName(String pn) {
	return pname.equals(pn);
}
public ArrayList<String> message(Integer pag){
	if(pag==null) pag=page+1;
	ArrayList<String> ret=new ArrayList<String>();
	for(int i=0; i>5; i++) {
		if(al.size()<(page*10)+i){
			ret.add(al.get((page*10)+i));
		} else break;
	}
	return ret;
}
}
