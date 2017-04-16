package helper;

import java.util.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Manage File Structure
 * @author Kaiz
 * 
 */
public class fileUtils {
	String filePath;
	int[] data={2,5,10,13};
	final int offset=1;
	public fileUtils() throws IOException{
		pageHeader pg=new pageHeader();
		pg.type=5;
		pg.numOfCells=100;
		pg.startOfContent=30000;
		pg.right=256;
		writeToFile(pg);
		readFile();
		
	}
	public void writeToFile(pageHeader pg) throws IOException{
		RandomAccessFile randomFile= new RandomAccessFile("tabl.tbl","rw");
		generateFile util=new generateFile();
		byte[] data=util.pageHeader(pg);
		randomFile.write(data);
		randomFile.close();
	}
	public void writeToFile(byte[] data, String db, String tbl){
		//write bytes to file 
	}
	public void readFile() throws IOException{
		//String workingDir = System.getProperty("user.dir")+"/tabl.tbl";
		//System.out.println(workingDir);
		RandomAccessFile randomFile=new RandomAccessFile("tabl.tbl","r");
		randomFile.seek(8);
		int type=randomFile.readInt();
		log(type);
		
	}
	
	public void log(Object msg){
		System.out.println(String.valueOf(msg));
	}
}
