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
	int size=main.getCon().getInt("Logging.PageSize");
	ArrayList<String> ret=new ArrayList<String>();
	for(int i=0; i<size; i++) {
		if(al.size()>(pag*size)+i){
			
			ret.add(al.get((pag*size)+i));
		} else break;
	}
	int of=((al.size()/size));
	if((al.size()%size)!=0)
		of++;
	pag=pag+1;
	ret.add(""+pag+" of "+of);
	return ret;
}
public int getPageAmount() {
	return ((al.size()/main.getCon().getInt("Logging.PageSize")));
}
}
