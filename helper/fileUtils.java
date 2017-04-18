package helper;

import java.util.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Manage File IO
 * 
 * @author Kaiz
 * 
 */
public class fileUtils {
	
	String filePath;
	
	int[] data = { 2, 5, 10, 13 };
	
	final int offset = 1;
	
	public fileUtils() throws IOException {
		pageHeader pg = new pageHeader();
		pg.type = 5;
		pg.numOfCells = 100;
		pg.startOfContent = 30000;
		pg.right = 256;
		//writeToTables("kaidb.table1;");
		// readTables();

	}

	public void writeToFile(pageHeader pg) throws IOException {
		RandomAccessFile randomFile = new RandomAccessFile("tabl.tbl", "rw");
		generateFile util = new generateFile();
		byte[] data = util.pageHeader(pg);
		randomFile.write(data);
		randomFile.close();
	}

	

	public void writeToFile(byte[] data, String db, String tbl) {
		// write bytes to file
	}

	public void readFile() throws IOException {
		// String workingDir = System.getProperty("user.dir")+"/tabl.tbl";
		// System.out.println(workingDir);
		RandomAccessFile randomFile = new RandomAccessFile("tabl.tbl", "r");
		randomFile.seek(8);
		int type = randomFile.readInt();
		// log(type);

	}
	/**
	 * Read tables Names
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<String>> readTables() throws IOException {
		RandomAccessFile raf = new RandomAccessFile("data/catalog/tables.tbl", "r");
		Map<String, List<String>> res=new HashMap();
		if(raf.length()==0) return res;
		byte[] document = new byte[(int) raf.length()];
		raf.readFully(document);
		String raw=new String(document);
		String[] tables=raw.split(";");
		for(String s:tables){
			String[] tmp=s.split("\\.");
			//log(tmp[0]+" "+ tmp[1]);
			if(res.containsKey(tmp[0])){
				List<String> t1=res.get(tmp[0]);
				t1.add(tmp[1]);
				res.put(tmp[0], t1);
			}
			else{
				List<String> t2=new ArrayList();
				t2.add(tmp[1]);
				res.put(tmp[0],t2);
			}
		}
		// log(raf.length());
		// log(raf.readChar());
		// randomFile.seek(8);
		// int type=randomFile.readInt();
		// log(type);
		return res;

	}

	// return list of all documents names in that folder
	public List<String> getDBNames(String dir) {
		List<String> results = new ArrayList<String>();
		File[] files = new File(dir).listFiles();
		// If this pathname does not denote a directory, then listFiles()
		// returns null.
		for (File file : files) {
			if (file.isDirectory()) {
				results.add(file.getName());
			}
		}
		// log(results);
		return results;
	}

	// return list of all documents names in that folder
	public List<String> getTables(String currDB) throws IOException {
		List<String> results = new ArrayList<String>();
		Map<String, List<String>> res=readTables();
		// If this pathname does not denote a directory, then listFiles()
		// returns null.
		results=res.get(currDB);
		// log(results);
		return results;
	}
	
	public void writeToTables(String s) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("data/catalog/tables.tbl", "rw");
		raf.seek(raf.length());
		raf.writeBytes(s);
		raf.close();
		//readTables();
	}
	
	//
	public void log(Object msg) {
		System.out.println(String.valueOf(msg));
	}
}
