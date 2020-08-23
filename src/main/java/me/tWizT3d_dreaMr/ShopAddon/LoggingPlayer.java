package me.tWizT3d_dreaMr.ShopAddon;

import java.util.ArrayList;

public class LoggingPlayer {
private ArrayList<String> al;
private String pname;
public LoggingPlayer(ArrayList<String> arrayList, String pname) {
	this.al=arrayList;
	this.pname=pname;
}
public boolean isName(String pn) {
	return pname.equals(pn);
}
public ArrayList<String> message(int pag){
	
	ArrayList<String> ret=new ArrayList<String>();
	for(int i=0; i<5; i++) {
		if(al.size()>(pag*5)+i){
			
			ret.add(al.get((pag*5)+i));
		} else break;
	}
	int of=((al.size()/5));
	if(al.size()%5==0)
		of--;
	pag=pag+1;
	ret.add(""+pag+" of "+of);
	return ret;
}
}
