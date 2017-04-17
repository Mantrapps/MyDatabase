package base;

import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedMap;

import ddl.showDatabase;
import helper.fileUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Kaiz code adopted from Prof.Davis
 */
public class mydb {
	static String prompt = "mydb> ";
	static String version = "v1.0a";
	static String copyright = "©2017 Kai Zhu - kxz160030";
	static String currDB="kaiDB";
	static boolean isExit = false;
	/*
	 * Page size for alll files is 512 bytes by default. You may choose to make
	 * it user modifiable
	 */
	static long pageSize = 512;

	/*
	 * The Scanner class is used to collect user commands from the prompt There
	 * are many ways to do this. This is just one.
	 *
	 * Each time the semicolon (;) delimiter is entered, the userCommand String
	 * is re-populated.
	 */
	static Scanner scanner = new Scanner(System.in).useDelimiter(";");

	/**
	 * ***********************************************************************
	 * Main method
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		/* Display the welcome screen */
		splashScreen();
		
		
		//test purpose
		//fileUtils test=new fileUtils();
		
		
		/* Variable to collect user input from the prompt */
		String userCommand = "";
		while (!isExit) {
			System.out.print(prompt);
			/* toLowerCase() renders command case insensitive */
			userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
			System.out.println(userCommand);
			// userCommand = userCommand.replace("\n", "").replace("\r", "");
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");

	}

	/**
	 * ***********************************************************************
	 * Method definitions
	 */

	/**
	 * Display the splash screen
	 */
	public static void splashScreen() {
		System.out.println(line("-", 80));
		System.out.println("Welcome to mydb!"); // Display the string.
		System.out.println("mydb Version " + getVersion());
		System.out.println(getCopyright());
		System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println(line("-", 80));
	}

	/**
	 * @param s
	 *            The String to be repeated
	 * @param num
	 *            The number of time to repeat String s.
	 * @return String A String object, which is the String s appended to itself
	 *         num times.
	 */
	public static String line(String s, int num) {
		String a = "";
		for (int i = 0; i < num; i++) {
			a += s;
		}
		return a;
	}

	/**
	 * Help: Display supported commands
	 */
	public static void help() {
		System.out.println(line("*", 80));
		System.out.println("SUPPORTED COMMANDS");
		System.out.println("All commands below are case insensitive");
		System.out.println();
		System.out.println("\tSELECT * FROM table_name;                        Display all records in the table.");
		System.out.println("\tSELECT * FROM table_name WHERE rowid = <value>;  Display records whose rowid is <id>.");
		System.out.println("\tDROP TABLE table_name;                           Remove table data and its schema.");
		System.out.println("\tVERSION;                                         Show the program version.");
		System.out.println("\tHELP;                                            Show this help information");
		System.out.println("\tEXIT;                                            Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*", 80));
	}

	/** return the DavisBase version */
	public static String getVersion() {
		return version;
	}

	public static String getCopyright() {
		return copyright;
	}

	public static void displayVersion() {
		System.out.println("mydb Version " + getVersion());
		System.out.println(getCopyright());
	}

	public static void parseUserCommand(String userCommand) throws IOException {

		/*
		 * commandTokens is an array of Strings that contains one token per
		 * array element The first token can be used to determine the type of
		 * command The other tokens can be used to pass relevant parameters to
		 * each command-specific method inside each case statement
		 */
		// String[] commandTokens = userCommand.split(" ");
		ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));

		/*
		 * This switch handles a very small list of hardcoded commands of known
		 * syntax. You will want to rewrite this method to interpret more
		 * complex commands.
		 */
		switch (commandTokens.get(0)) {
		case "use":
			useDB(commandTokens);
			break;
		case "show":
			show(commandTokens);
			break;
		case "select":
			parseQueryString(userCommand);
			break;
		case "drop":
			System.out.println("STUB: Calling your method to drop items");
			dropTable(userCommand);
			break;
		case "create":
			parseCreateString(userCommand);
			break;
		case "help":
			help();
			break;
		case "version":
			displayVersion();
			break;
		case "exit":
			isExit = true;
			break;
		case "quit":
			isExit = true;
		default:
			System.out.println("I didn't understand the command: \"" + userCommand + "\"");
			break;
		}
	}

	/**
	 * Stub method for dropping tables
	 * 
	 * @param dropTableString
	 *            is a String of the user input
	 */
	public static void dropTable(String dropTableString) {
		System.out.println("STUB: Calling parseQueryString(String s) to process queries");
		System.out.println("Parsing the string:\"" + dropTableString + "\"");
	}

	/**
	 * Stub method for executing queries
	 * 
	 * @param queryString
	 *            is a String of the user input
	 */
	public static void parseQueryString(String queryString) {
		System.out.println("STUB: Calling parseQueryString(String s) to process queries");
		System.out.println("Parsing the string:\"" + queryString + "\"");
	}
	public static void useDB(ArrayList<String> s) throws IOException{
		showDatabase sD=new showDatabase();
		if(s.size()<2) log("not correct command");
		else{
			if(sD.checkDB(s.get(1))) {
				log("current database: "+s.get(1)+";");
				currDB=s.get(1);
			}
			else log("database: "+s.get(1)+" not exist!");
		}
		
	}
	public static void show(ArrayList<String> s) throws IOException{
		if(s.size()<2) log("not correct command");
		else{
			if(s.get(1).equals("database")){
				showDatabase sD=new showDatabase();
				sD.showDatabase();
			}
			else if(s.get(1).equals("tables")){
				
			}
			else if(s.get(1).equals("current")){
				log("current database:");
				log("--------------------");
				log(currDB);
				log("--------------------");
			}
			else {
				log("command: "+ s.get(1)+", not supported");
			}
		}	
	}
	/**
	 * Stub method for creating new tables
	 * 
	 * @param queryString
	 *            is a String of the user input
	 */
	public static void parseCreateString(String createTableString) {

		System.out.println("STUB: Calling your method to create a table");
		System.out.println("Parsing the string:\"" + createTableString + "\"");
		ArrayList<String> createTableTokens = new ArrayList<String>(Arrays.asList(createTableString.split(" ")));

		/* Define table file name */
		String tableFileName = createTableTokens.get(2) + ".tbl";

		/* YOUR CODE GOES HERE */

		/* Code to create a .tbl file to contain table data */
		try {
			/*
			 * Create RandomAccessFile tableFile in read-write mode. Note that
			 * this doesn't create the table file in the correct directory
			 * structure
			 */
			RandomAccessFile tableFile = new RandomAccessFile(tableFileName, "rw");
			tableFile.setLength(pageSize);
		} catch (Exception e) {
			System.out.println(e);
		}

		/*
		 * Code to insert a row in the davisbase_tables table i.e. database
		 * catalog meta-data
		 */

		/*
		 * Code to insert rows in the davisbase_columns table for each column in
		 * the new table i.e. database catalog meta-data
		 */
	}
	public static void log(Object msg){
		System.out.println(String.valueOf(msg));
	}

}
