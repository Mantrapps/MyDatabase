package helper;

/**
 * Column class
 * Kai
 */
class Column {
    String tableName;
    String columnName;
    String type;
    int ordinalPosition;
    boolean isNullable = true;

    Column(String columnName, String type, String tableName) {
        this.columnName = columnName;
        this.type = type;
        this.tableName = tableName;
    }
}
