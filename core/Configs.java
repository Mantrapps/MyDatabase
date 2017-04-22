package core;

import java.util.HashMap;
import java.util.Map;

/**
 * Configs relative
 * Kai
 */
class Configs {
    // Map for data types and code
	
    static final Map<String, Integer> dataTypeCode = new HashMap<>();
    static {
        dataTypeCode.put("tinyint", 0x04);
        dataTypeCode.put("smallint", 0x05);
        dataTypeCode.put("int", 0x06);
        dataTypeCode.put("bigint", 0x07);
        dataTypeCode.put("real", 0x08);
        //dataTypeCode.put("float", 0x08);
        dataTypeCode.put("double", 0x09);
        dataTypeCode.put("datetime", 0x0A);
        dataTypeCode.put("date", 0x0B);
        dataTypeCode.put("text", 0x0C);
    }
    static final Map<String, Integer> dataTypeLength = new HashMap<>();
    static {
        dataTypeLength.put("tinyInt", 1);
        dataTypeLength.put("smallint", 2);
        dataTypeLength.put("int", 4);
        dataTypeLength.put("bigint", 8);
        dataTypeLength.put("real", 4);
        //dataTypeLength.put("float", 4);
        dataTypeLength.put("double", 8);
        dataTypeLength.put("datetime", 8);
        dataTypeLength.put("date", 8);
        dataTypeLength.put("text", -1);
    }
    static final Map<Integer, Integer> typeCodeLength = new HashMap<>();
    static {
        typeCodeLength.put(0x00, 1);
        typeCodeLength.put(0x01, 2);
        typeCodeLength.put(0x02, 4);
        typeCodeLength.put(0x03, 8);
        typeCodeLength.put(0x04, 1);
        typeCodeLength.put(0x05, 2);
        typeCodeLength.put(0x06, 4);
        typeCodeLength.put(0x07, 8);
        typeCodeLength.put(0x08, 8);
        typeCodeLength.put(0x09, 8);
        typeCodeLength.put(0x0A, 8);
        typeCodeLength.put(0x0B, 8);
        typeCodeLength.put(0x0C, -1);
    }

    /* This can be changed to whatever you like */
    static String prompt = "mydb> ";
	static String version = "v1.0a";
	static String copyright = "©2017 Kai Zhu - kxz160030";
	static String currDB="kaidb";
    /*
     * Page size for all files is 512 bytes by default.
     * You may choose to make it user modifiable
     */
    static final long pageSize = 512;

    // Path for table directory
    static final String META_TABLE = "davisbase_tables";
    static final String META_COLUMN = "davisbase_columns";
    static final String META_TABLE_FILE = "./data/catalog/davisbase_tables.tbl";
    static final String META_COLUMN_FILE = "./data/catalog/davisbase_columns.tbl";
    static final String USERDATA_DIR = "./data/user_data/";
}
