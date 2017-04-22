package core;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * DDL
 * SHOW TABLES
 * CREATE TABLE
 * DROP TABLE
 * Kai
 */
class DDL {
    /**
     * Show all tables
     */
    static void showTables() {
        DML.select("select * from davisbase_tables");
    }


    /**
     * Method for creating new tables
     *
     * @param command is a String of the user input
     */
    static String createTable(String command) {
        String tableName = command.split(" ")[2].trim();
        String[] columnsStrs = command.substring(command.indexOf("(")+1,command.indexOf(")")).trim().split(",");

        // Special check for first column
        if (!columnsStrs[0].contains("primary key") || !columnsStrs[0].contains("int")) {
            return "First row must be primary key and must be INT!";
        }

        // Generate columns
        ArrayList<Column> columns = new ArrayList<>();
        int ordinal = 0;
        for (String c : columnsStrs) {
            String[] fields = c.trim().split(" ");
            String name = fields[0];
            String type = fields[1];
            if(type.equals("float")) type="real";
            if (!Configs.dataTypeCode.containsKey(type)) {
                return "Data type " + fields[1] + " not supported!";
            }
            Column column = new Column(name, type, tableName);
            if (c.contains("not null") || c.contains("primary key")) column.isNullable = false;
            column.ordinalPosition = ordinal++;
            columns.add(column);
        }
        Table table = new Table(tableName);
        table.columns = columns;

        // Write into meta tables table
        Table metaTable = new Table(Configs.META_TABLE);
        metaTable.readFromFile();
        for (Record r : metaTable.records) {
            if (r.data.get(0).equals(tableName)) {
                return "Table already exist!";
            }
        }
        int lastRowid = 0;
        if (metaTable.records.size() > 0) {
            lastRowid = metaTable.records.get(metaTable.records.size()-1).rowid;
        }
        Record r = new Record();
        r.numColumns = 1;
        r.typeCodes.add(0x0C + tableName.length());
        r.data.add(tableName);
        r.payloadLen = 1 + r.typeCodes.size() + tableName.length();
        r.rowid = lastRowid+1;
        r.offset = 0;
        metaTable.records.add(r);
        metaTable.writeToFile();

        // Write into meta columns table
        Table metaColumn = new Table(Configs.META_COLUMN);
        metaColumn.readFromFile();
        lastRowid = 0;
        if (metaColumn.records.size() > 0) {
            lastRowid = metaColumn.records.get(metaColumn.records.size()-1).rowid;
        }
        for (Column c : columns) {
            Record rec = new Record();
            rec.typeCodes.add(0x0C + c.tableName.length());
            rec.typeCodes.add(0x0C + c.columnName.length());
            rec.typeCodes.add(0x0C + c.type.length());
            rec.typeCodes.add(0x04);  // Tiny int
            rec.typeCodes.add(0x0C + String.valueOf(c.isNullable).length());  // Text
            rec.data.add(c.tableName);
            rec.data.add(c.columnName);
            rec.data.add(c.type);
            rec.data.add(String.valueOf(c.ordinalPosition));
            rec.data.add(String.valueOf(c.isNullable));
            rec.numColumns = rec.typeCodes.size();
            rec.setPayloadLen();
            rec.rowid = ++lastRowid;
            rec.offset = 0;
            metaColumn.records.add(rec);
        }
        metaColumn.writeToFile();

        try (RandomAccessFile f = new RandomAccessFile(Configs.USERDATA_DIR + tableName + ".tbl", "rw")){
            f.setLength(512);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success";
    }

    /**
     * Stub method for dropping tables
     *
     * @param tableName is a String of the user input
     */
    static void dropTable(String tableName) {
        // Delete from meta table
        Table metaTable = new Table(Configs.META_TABLE);
        metaTable.readFromFile();
        int deleteIndex = -1;
        for (int i=0; i < metaTable.records.size(); i++) {
            if (metaTable.records.get(i).data.get(0).equals(tableName)) {
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex == -1) {
            Utils.print("Table " + tableName + " not exist!");
        } else {
            metaTable.records.remove(deleteIndex);
            // Modify rowid
            int rowid = 1;
            for (Record r : metaTable.records) {
                r.rowid = rowid++;
            }
        }
        metaTable.writeToFile();

        // Delete from meta column
        Table metaColumn = new Table(Configs.META_COLUMN);
        metaColumn.readFromFile();
        List<Record> deleteList = new ArrayList<>();
        for (int i=0; i < metaColumn.records.size(); i++) {
            Record r = metaColumn.records.get(i);
            if (r.data.get(0).equals(tableName)) {
                deleteList.add(r);
            }
        }
        for (Record r : deleteList) {
            metaColumn.records.remove(r);
        }
        // Modify rowid
        int rowid = 1;
        for (Record r : metaColumn.records) {
            r.rowid = rowid++;
        }
        metaColumn.writeToFile();

        // Delete table file
        File f = new File(Configs.USERDATA_DIR + tableName + ".tbl");
        if (f.delete()) {
            Utils.print("Drop table " + tableName + " successful!");
        } else {
            Utils.print("Deleting table file failed!");
        }
    }
}
