package core;

import java.util.Scanner;

/***
 * main function, partial inheritated from Prof.Davis
 * cs6360 proj
 * @author Kaiz
 *
 */

public class mydb {

    //Each time the semicolon (;) delimiter is entered, the userCommand String is re-populated.
    private static boolean isExit = false;

    /**
     * **********************************************************************
     * Main method
     */
    public static void main(String[] args) {
		/* Display the welcome screen */
        splashScreen();

        Scanner scanner = new Scanner(System.in).useDelimiter(";");

		/* Variable to collect user input from the prompt */
        String userCommand = "";

        while (!isExit) {
            System.out.print(Configs.prompt);
            /* toLowerCase() renders command case insensitive */
            userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
            // userCommand = userCommand.replace("\n", "").replace("\r", "");
            parseUserCommand(userCommand);
        }
        System.out.println("Bye");
    }


    private static void parseUserCommand(String userCommand) {
		/*
		*  This switch handles a very small list of hardcoded commands of known syntax.
		*  You will want to rewrite this method to interpret more complex commands.
		*/
        Utils.print(userCommand);
        switch (userCommand.split(" ")[0]) {
            // DDL operations
            case "show":
                if (!userCommand.split(" ")[1].equals("tables")) complain(userCommand);
                else DDL.showTables();
                break;
            case "create":
                if (!userCommand.split(" ")[1].equals("table")) complain(userCommand);
                else {
                    String result = DDL.createTable(userCommand);
                    if (!result.equals("success")) {
                        Utils.print(result);
                    } else {
                        Utils.print("Table created successful!");
                    }
                }
                break;
            case "drop":
                if (!userCommand.split(" ")[1].equals("table")) complain(userCommand);
                DDL.dropTable(userCommand.split(" ")[2]);
                break;

            // DML and VDL operations
            case "select":
                DML.select(userCommand);
                break;
            case "insert":
                DML.insert(userCommand);
                break;
            case "update":
                DML.update(userCommand);
                break;
            case "delete":
                DML.delete(userCommand);
                break;

            // Others
            case "help":
                help();
                break;
            case "version":
                displayVersion();
                break;
            case "quit":
            case "exit":
                isExit = true;
                break;
            default:
                complain(userCommand);
                break;
        }
    }

    /** ***********************************************************************
     *  Method definitions
     */
    private static void complain(String userCommand) {
        Utils.print("I didn't understand the command: \"" + userCommand + "\"");
    }

    /**
     * Display the splash screen
     */
    private static void splashScreen() {
        System.out.println(Utils.line("-", 80));
        System.out.println("Welcome to Mydb"); // Display the string.
        displayVersion();
        System.out.println("\nType \"help;\" to display supported commands.");
        System.out.println("Please be aware there should be space column_name operator value, such as where condition, delete or update");
        System.out.println(Utils.line("-", 80));
    }

    /**
     * Help: Display supported commands
     */
    private static void help() {
        Utils.print(Utils.line("*", 80));
        Utils.print("SUPPORTED COMMANDS");
        Utils.print("All commands below are case insensitive");
        Utils.print("");
        Utils.print("\tCREATE TABLE table_name ([column_name_property_list])        Create table");
        Utils.print("\tDROP TABLE table_name;                                       Remove table data and its schema.");
        Utils.print("\tSELECT * FROM table_name [WHERE rowid = <value>];            Display one or more records in the table.");
        Utils.print("\tINSERT INTO [Column_list] table_name VALUES value_list;      Insert a single record into table.");
        Utils.print("\tUPDATE table_name SET column_name = value [WHERE condition]; Modify one or more records in a table.");
        Utils.print("\tDELETE FROM table_name WHERE rowid = <value>;                Delete a record whose rowid is <value>.");
        Utils.print("\tVERSION;                                                     Show the program version.");
        Utils.print("\tHELP;                                                        Show this help information");
        Utils.print("\tEXIT;                                                        Exit the program");
        Utils.print("");
        Utils.print(Utils.line("*", 80));
    }


    private static void displayVersion() {
    	System.out.println("mydb Version " + Configs.version);
		System.out.println(Configs.copyright);
    }
}