package ddl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

import helper.fileUtils;

/**
 * create table table_name{
 * 			col1 int 		primary key,
 * 			col2 datatype 	[not null],
 * 			col3 datatype	[not null]
 * }
 * 
 * tables
 *  	rowid			int		4 bytes
 *  	dbName			text	
 *  	tableName		text
 *  	record_count 	int		4 bytes
 *  	avg_length 		smallint	2 byts
 */
public class createTable {
	fileUtils ft;
	// check if exist, create table under database
	public void create(ArrayList<String> createTableTokens, String currDB, long pageSize, String createTableString) throws IOException {
		
		System.out.println(createTableTokens);
		
		/* Define table file name */
		String tableFileName = createTableTokens.get(2);
		
		//in case no space between {}
		if(tableFileName.contains("{")) {
			log("check syntex!");
			return;
		}
		
		if(addToTables(tableFileName, currDB)){
			addColumns(tableFileName, currDB,createTableString);
		}
		else{
			log("failed insert into tables table");
		}
	}

	// add row to tables table
	public boolean addToTables(String tableName, String currDB) throws IOException {
		// Code to insert a row in the tables table with currentDB
		ft=new fileUtils();
		
		//get tables in currDB
		List<String> tbls=ft.getTables(currDB);
		
		// read all table from check if exist
		if(tbls.contains(tableName)) return false;
		
		// Code to insert table in the tables table
		ft.writeToTables(currDB+"."+tableName+";");
		return true;
	}

	public void addColumns(String tableName, String currDB, String createTableString) {
		// Code to insert rows in the columns table for each column in currentDB
		String[] cols=createTableString.split("\\{")[1].split("\\}")[0].split(",");
		for(String s:cols){
			String[] colDetails=s.split(" ");
			//db.table.colname.key.null
			String newcols=currDB+"."+tableName+"."+colDetails[0]+"."+colDetails[1]+".";
		}
	}

	public void createTablePage(String tableName, String currDB,long pageSize) {
		/* Code to create a .tbl file to contain table data */
		try {
			/*
			 * Create RandomAccessFile tableFile in read-write mode. Note that
			 * this doesn't create the table file in the correct directory
			 * structure
			 */
			RandomAccessFile tableFile = new RandomAccessFile("/" + currDB + "/" + tableName+".tbl", "rw");
			tableFile.setLength(pageSize);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void log(Object msg) {
		System.out.println(String.valueOf(msg));
	}
}
