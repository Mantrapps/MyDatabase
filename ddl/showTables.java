package ddl;

import helper.fileUtils;

import java.io.IOException;
import java.util.*;

public class showTables {

	public void show(String currDB) throws IOException {
		// show
		fileUtils ft = new fileUtils();
		List<String> tbls = ft.getTables(currDB);
		int count = 1;
		for (String i : tbls) {
			String row = String.format("%1$-10s %2$-10s %3$-10s", count, currDB, i);
			log(row);
			count++;
		}
	}

	public void log(Object msg) {
		System.out.println(String.valueOf(msg));
	}
}
