package ddl;

import java.io.IOException;

import helper.fileUtils;
import java.util.*;

public class showDatabase {
	public void showDatabase() throws IOException{
		fileUtils helper=new fileUtils();
		List<String> res=helper.getDBNames(System.getProperty("user.dir")+"/data/");
		int count=1;
		for(String i:res){
			if(!i.equals("catalog")) {
				String row=String.format("%1$-10s %2$-10s",count,i);
				log(row);
				count++;
			}
		}
		
	}
	public boolean checkDB(String s) throws IOException{
		fileUtils helper=new fileUtils();
		List<String> res=helper.getDBNames(System.getProperty("user.dir")+"/data/");
		
		if(!res.contains(s)) return false;
		else return true;
	}
	public void log(Object msg){
		System.out.println(String.valueOf(msg));
	}
}
