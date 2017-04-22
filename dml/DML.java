package dml;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import helper.Utils;

/**
 * DML VDL
 * SELECT
 * INSERT
 * UPDATE
 * DELETE
 * Kai
 */
public class DML {
    /**
     * Execute select queries
     *
     * @param queryString is a String of the user input
     */
    public static void select(String queryString) {
        String column = "";
        String tableName = "";
        String condition = "";
        Pattern p = Pattern.compile("select (.+) from (.+)");
        Matcher m = p.matcher(queryString);
        if (!m.matches()) {
            Utils.print("Cannot parse query: " + queryString);
            return;
        }
        column = m.group(1).trim();
        tableName = m.group(2).trim();
        if (tableName.contains("where")) {
            condition = tableName.substring(tableName.lastIndexOf("where")).replaceFirst("where", "").trim();
            tableName = tableName.substring(0, tableName.lastIndexOf("where")).trim();
        }

        Table table = new Table(tableName);
        table.readFromFile();
        List<Record> result = table.records;
        if (!condition.equals("")) result = findWithCondition(table, condition);

        if (result == null || result.size() == 0) return;

        if (column.equals("*")) {
            for (Column c : table.columns) {
                System.out.print(c.columnName + "\t\t");
            }
           
            Utils.print("");
            Utils.print(Utils.line("-", 80));
            for (Record r : result) {
                System.out.print(r.rowid + "\t\t");

                int idx = 0;
                for (int typeCode : r.typeCodes) {
                    switch (typeCode) {
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                            idx++;
                            System.out.print("null" + "\t\t"); break;
                        case 0x04: System.out.print(r.getByte(idx++) + "\t\t"); break;
                        case 0x05: System.out.print(r.getShort(idx++) + "\t\t"); break;
                        case 0x06: System.out.print(r.getInt(idx++) + "\t\t"); break;
                        case 0x07: System.out.print(r.getLong(idx++) + "\t\t"); break;
                        case 0x08: System.out.print(r.getFloat(idx++) + "\t\t"); break;
                        case 0x09: System.out.print(r.getDouble(idx++) + "\t\t"); break;
                        case 0x0A: System.out.print(Utils.timestampToDateTime(r.getLong(idx++)) + "\t\t"); break;
                        case 0x0B: System.out.print(Utils.timestampToDate(r.getLong(idx++)) + "\t\t"); break;
                    }
                    if (typeCode >= 0x0C) {
                        System.out.print(r.data.get(idx++) + "\t\t");
                    }
                }
                Utils.print("");
            }
        } else {
            String[] columns = column.split(",");
            List<Integer> ordinals = new ArrayList<>();
            List<Integer> typeCodes = new ArrayList<>();
            for (String clm : columns) {
                for (Column c : table.columns) {
                    if (c.columnName.equals(clm.trim())) {
                        System.out.print(c.columnName + "\t\t");
                        ordinals.add(c.ordinalPosition);
                        typeCodes.add(Configs.dataTypeCode.get(c.type));
                        break;
                    }
                }
            }
            System.out.println(Utils.line("-", 80));
            if (ordinals.size() != columns.length) {
                Utils.print("One of the columns not found!");
                return;
            }
            Utils.print("");
            for (Record r : result) {
                for (int ordinal : ordinals) {
                    if (ordinal == 0) {
                        System.out.print(r.rowid + "\t\t");
                    } else {
                        ordinal--;  // Except rowid
                        int typeCode = r.typeCodes.get(ordinal);

                        switch (typeCode) {
                            case 0x00:
                            case 0x01:
                            case 0x02:
                            case 0x03:
                                System.out.print("null" + "\t\t"); break;
                            case 0x04: System.out.print(r.getByte(ordinal) + "\t\t"); break;
                            case 0x05: System.out.print(r.getShort(ordinal) + "\t\t"); break;
                            case 0x06: System.out.print(r.getInt(ordinal) + "\t\t"); break;
                            case 0x07: System.out.print(r.getLong(ordinal) + "\t\t"); break;
                            case 0x08: System.out.print(r.getFloat(ordinal) + "\t\t"); break;
                            case 0x09: System.out.print(r.getDouble(ordinal) + "\t\t"); break;
                            case 0x0A: System.out.print(Utils.timestampToDateTime(r.getLong(ordinal)) + "\t\t"); break;
                            case 0x0B: System.out.print(Utils.timestampToDate(r.getLong(ordinal)) + "\t\t"); break;
                        }
                        if (typeCode >= 0x0C) {
                            System.out.print(r.data.get(ordinal) + "\t\t");
                        }
                    }
                }
                Utils.print("");
            }
        }


    }

    /**
     * Execute select queries
     *
     * @param queryString is a String of the user input
     */
    static void insert(String queryString) {
        // Parse query
        Pattern p = Pattern.compile("insert into (.+) values (.+)");
        Matcher m = p.matcher(queryString);
        String column = "";
        String tableName = "";
        String value = "";
        if (!m.matches()) {
            Utils.print("Cannot parse query: " + queryString);
            return;
        }
        tableName = m.group(1).trim();
        if (tableName.contains("(")) {
            column = tableName.substring(tableName.indexOf("(")+1, tableName.indexOf(")")).trim();
            tableName = tableName.substring(0, tableName.indexOf("(")).trim();
        }
        value = m.group(2).replace('(', ' ').replace(')', ' ').trim();

        Table table = new Table(tableName);
        table.readFromFile();

        String[] values = value.split(",");
        if (column.equals("")) {
            if (values.length != table.columns.size()) {
                Utils.print(table.columns.size() + " values required!");
                return;
            }

            // Check input
            try {
                Integer.parseInt(values[0]);
                for (int i=1; i < table.columns.size(); i++) {
                    Column c = table.columns.get(i);
                    String v = values[i].trim();
                    int typeCode = Configs.dataTypeCode.get(c.type);
                    if (!v.equals("null")) {
                        switch (typeCode) {
                            case 0x04: Byte.parseByte(v); break;
                            case 0x05: Short.parseShort(v); break;
                            case 0x06: Integer.parseInt(v); break;
                            case 0x07: Long.parseLong(v); break;
                            case 0x08: Float.parseFloat(v); break;
                            case 0x09: Double.parseDouble(v); break;
                            case 0x0A:
                                Utils.dateTimeToTimestamp(v); break;
                            case 0x0B:
                                Utils.dateToTimeStamp(v); break;
                        }
                    }
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                Utils.print(e.getLocalizedMessage());
                Utils.print("Invalid value!");
                return;
            }

            Record r = new Record();
            r.rowid = Integer.parseInt(values[0]);
            for (Record rec : table.records) {
                if (r.rowid == rec.rowid) {
                    Utils.print("Rowid duplicated!");
                    return;
                }
            }
            r.numColumns = table.columns.size()-1;
            for (int i=1; i < table.columns.size(); i++) {
                Column c = table.columns.get(i);
                String v = values[i].trim();
                int typeCode = Configs.dataTypeCode.get(c.type);
                if (v.equals("null") && typeCode != 0x0C) {
                    r.data.add("null");
                    if (typeCode == 0x04) {
                        r.typeCodes.add(0x00);
                    } else if (typeCode == 0x05) {
                        r.typeCodes.add(0x01);
                    } else if (typeCode == 0x06 || typeCode == 0x08) {
                        r.typeCodes.add(0x02);
                    } else {
                        r.typeCodes.add(0x03);
                    }
                } else {
                    if ((typeCode >= 0x04 && typeCode <= 0x07)) {
                        r.data.add(v);
                        r.typeCodes.add(typeCode);
                    } else if (typeCode == 0x0C) {
                        r.data.add(v);
                        r.typeCodes.add(typeCode + v.length());
                    } else if (typeCode == 0x0A) {
                        r.data.add(String.valueOf(Utils.dateTimeToTimestamp(v)));
                        r.typeCodes.add(typeCode);
                    } else if (typeCode == 0x0B) {
                        r.data.add(String.valueOf(Utils.dateToTimeStamp(v)));
                        r.typeCodes.add(typeCode);
                    } else {
                        r.data.add(v);
                        r.typeCodes.add(typeCode);
                    }
                }
            }
            r.setPayloadLen();
            table.records.add(r);
        } else {
            String[] columns = column.split(",");
            List<Integer> ordinals = new ArrayList<>();
            List<Integer> typeCodes = new ArrayList<>();
            List<Boolean> nullables = new ArrayList<>();

            // Check input
            if (columns.length != values.length) {
                Utils.print("Number of given values not equal to number of columns!");
                return;
            }

            for (Column c : table.columns) {
                boolean found = false;
                for (String clm : columns) {
                    if (clm.trim().equals(c.columnName)) {
                        found = true;
                        break;
                    }
                }
                if (!found && !c.isNullable && c.ordinalPosition != 0) {
                    Utils.print("Column " + c.columnName + " is not nullable, you must provide this value!");
                    return;
                }
            }
            for (String clm : columns) {
                for (Column c : table.columns) {
                    if (c.columnName.equals(clm.trim())) {
                        ordinals.add(c.ordinalPosition);
                        typeCodes.add(Configs.dataTypeCode.get(c.type));
                        nullables.add(c.isNullable);
                        break;
                    }
                }
            }
            if (ordinals.size() != columns.length) {
                Utils.print("One or more columns not found!");
                return;
            }
            try {
                for (int i=0; i < ordinals.size(); i++) {
                    int ordinal = ordinals.get(i);
                    String v = values[i].trim();
                    if (ordinal == 0) {
                        Integer.parseInt(v);
                        continue;
                    }
                    int typeCode = typeCodes.get(i);
                    boolean nullable = nullables.get(i);
                    if (!nullable && v.equals("null")) {
                        Utils.print("Value for column " + columns[i] + " cannot be null!");
                        return;
                    }
                    if (!nullable && typeCode == 0x0C && v.equals("")) {
                        Utils.print("Value for column " + columns[i] + " cannot be null!");
                        return;
                    }
                    switch (typeCode) {
                        case 0x04: Byte.parseByte(v); break;
                        case 0x05: Short.parseShort(v); break;
                        case 0x06: Integer.parseInt(v); break;
                        case 0x07: Long.parseLong(v); break;
                        case 0x08: Float.parseFloat(v); break;
                        case 0x09: Double.parseDouble(v); break;
                        case 0x0A:
                            Utils.dateTimeToTimestamp(v); break;
                        case 0x0B:
                            Utils.dateToTimeStamp(v); break;
                    }
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                Utils.print(e.getLocalizedMessage());
                Utils.print("Invalid value!");
                return;
            }

            Record r = new Record();
            if (ordinals.contains(0)) {
                int index = ordinals.indexOf(0);
                r.rowid = Integer.parseInt(values[index].trim());
                for (Record rec : table.records) {
                    if (r.rowid == rec.rowid) {
                        Utils.print("Rowid duplicated!");
                        return;
                    }
                }
            } else {
                int lastRowId = table.records.get(table.records.size()-1).rowid;
                r.rowid = lastRowId + 1;
            }
            for (int i=1; i < table.columns.size(); i++) {
                int ordinal = table.columns.get(i).ordinalPosition;
                if (ordinals.contains(ordinal)) {
                    int index = ordinals.indexOf(ordinal);
                    int typeCode = typeCodes.get(index);
                    String v = values[index].trim();
                    if (v.equals("null") && typeCode != 0x0C) {
                        r.data.add("null");
                        if (typeCode == 0x04) {
                            r.typeCodes.add(0x00);
                        } else if (typeCode == 0x05) {
                            r.typeCodes.add(0x01);
                        } else if (typeCode == 0x06 || typeCode == 0x08) {
                            r.typeCodes.add(0x02);
                        } else {
                            r.typeCodes.add(0x03);
                        }
                    } else {
                        if ((typeCode >= 0x04 && typeCode <= 0x07)) {
                            r.data.add(v);
                            r.typeCodes.add(typeCode);
                        } else if (typeCode == 0x0C) {
                            r.data.add(v);
                            r.typeCodes.add(typeCode + v.length());
                        } else if (typeCode == 0x0A) {
                            r.data.add(String.valueOf(Utils.dateTimeToTimestamp(v)));
                            r.typeCodes.add(typeCode);
                        } else if (typeCode == 0x0B) {
                            r.data.add(String.valueOf(Utils.dateToTimeStamp(v)));
                            r.typeCodes.add(typeCode);
                        } else {
                            r.data.add(v);
                            r.typeCodes.add(typeCode);
                        }
                    }
                } else {
                    int typeCode = Configs.dataTypeCode.get(table.columns.get(i).type);
                    r.data.add("null");
                    if (typeCode == 0x04 || typeCode == 0x0C) {
                        r.typeCodes.add(0x00);
                    } else if (typeCode == 0x05) {
                        r.typeCodes.add(0x01);
                    } else if (typeCode == 0x06 || typeCode == 0x08) {
                        r.typeCodes.add(0x02);
                    } else {
                        r.typeCodes.add(0x03);
                    }
                }
            }
            r.numColumns = table.columns.size()-1;
            r.setPayloadLen();
            table.records.add(r);
        }
        table.writeToFile();

        Utils.print("Insert successful!");
    }

    /**
     * Execute select queries
     *
     * @param queryString is a String of the user input
     */
    static void update(String queryString) {
        // Parse query
        Pattern p = Pattern.compile("update (.+) set (.+)");
        Matcher m = p.matcher(queryString);
        String tableName = "";
        String column = "";
        String value = "";
        String condition = "";
        if (m.matches()) {
            tableName = m.group(1).trim();
            String secondGroup= m.group(2).trim();
            if (secondGroup.contains("where")) {
                condition = secondGroup.substring(secondGroup.lastIndexOf("where")).replaceFirst("where", "").trim();
                secondGroup = secondGroup.substring(0, secondGroup.lastIndexOf("where")).trim();
            }
            column = secondGroup.split("=")[0].trim();
            value = secondGroup.split("=")[1].trim();
        }
        Table table = new Table(tableName);
        table.readFromFile();
        List<Record> updateList = table.records;
        if (!condition.equals("")) updateList = findWithCondition(table, condition);
        if (updateList == null || updateList.size() == 0) return;

        // Find ordinal of column to update
        int ordinal = -1;
        int typeCode = -1;
        boolean nulllable = true;
        for (Column c : table.columns) {
            if (c.columnName.equals(column)) {
                ordinal = c.ordinalPosition;
                typeCode = Configs.dataTypeCode.get(c.type);
                nulllable = c.isNullable;
                break;
            }
        }
        if (ordinal == -1) {
            Utils.print("Column " + column + " not found!");
            return;
        }

        // Check updated value type
        if (!value.equals("null")) {
            try {
                switch (typeCode) {
                    case 0x04:
                        Byte.parseByte(value);
                        break;
                    case 0x05:
                        Short.parseShort(value);
                        break;
                    case 0x06:
                        Integer.parseInt(value);
                        break;
                    case 0x07:
                        Long.parseLong(value);
                        break;
                    case 0x08:
                        Float.parseFloat(value);
                        break;
                    case 0x09:
                        Double.parseDouble(value);
                        break;
                    case 0x0A:
                        Utils.dateTimeToTimestamp(value);
                        break;
                    case 0x0B:
                        Utils.dateToTimeStamp(value);
                        break;
                }
            } catch (NumberFormatException | DateTimeParseException e) {
                Utils.print(e.getLocalizedMessage());
                Utils.print("Invalid value!");
                return;
            }
        } else {
            if (!nulllable) {
                Utils.print("Value for column " + column + " cannot be null!");
                return;
            }
        }

        for (Record r : updateList) {
            if (ordinal == 0) {
                r.rowid = Integer.parseInt(value);
            } else {
                ordinal--;  // Except rowid

                if (value.equals("null") && typeCode != 0x0C) {
                    r.data.set(ordinal, "null");
                    if (typeCode == 0x04) {
                        r.typeCodes.set(ordinal, 0x00);
                    } else if (typeCode == 0x05) {
                        r.typeCodes.set(ordinal, 0x01);
                    } else if (typeCode == 0x06 || typeCode == 0x08) {
                        r.typeCodes.set(ordinal, 0x02);
                    } else {
                        r.typeCodes.set(ordinal, 0x03);
                    }
                } else {
                    if ((typeCode >= 0x04 && typeCode <= 0x07)) {
                        r.data.set(ordinal, value);
                        r.typeCodes.set(ordinal, typeCode);
                    } else if (typeCode == 0x0C) {
                        r.data.set(ordinal, value);
                        r.typeCodes.set(ordinal, typeCode + value.length());
                    } else if (typeCode == 0x0A) {
                        r.data.set(ordinal, String.valueOf(Utils.dateTimeToTimestamp(value)));
                        r.typeCodes.set(ordinal, typeCode);
                    } else if (typeCode == 0x0B) {
                        r.data.set(ordinal, String.valueOf(Utils.dateToTimeStamp(value)));
                        r.typeCodes.set(ordinal, typeCode);
                    } else {
                        r.data.set(ordinal, value);
                        r.typeCodes.set(ordinal, typeCode);
                    }
                }
                ordinal++;
            }
            r.setPayloadLen();
        }
        table.writeToFile();
        Utils.print(updateList.size() + " rows updated!");
    }

    /**
     * Execute select queries
     *
     * @param queryString is a String of the user input
     */
    static void delete(String queryString) {
        // Parse query
        Pattern p = Pattern.compile("delete from (.+) where (.+)");
        Matcher m = p.matcher(queryString);
        String tableName = "";
        String condition = "";
        if (m.matches()) {
            tableName = m.group(1).trim();
            condition = m.group(2).trim();
        }
        Table table = new Table(tableName);
        table.readFromFile();
        List<Record> deleteList = findWithCondition(table, condition);
        if (deleteList == null || deleteList.size() == 0) return;
        for (Record r : deleteList) {
            table.records.remove(r);
        }
        table.writeToFile();
        Utils.print(deleteList.size() + " rows deleted!");
    }

    static List<Record> findWithCondition(Table table, String condition) {
        List<Record> result = new ArrayList<>();
        try {
            String[] conditionTokens = condition.split(" ");
            String columnName = conditionTokens[0].trim();
            String operator = conditionTokens[1].trim();
            String value = conditionTokens[2].trim();
            int ordinal = -1;
            int typeCode = 0;
            for (Column c : table.columns) {
                if (c.columnName.equals(columnName)) {
                    ordinal = c.ordinalPosition;
                    typeCode = Configs.dataTypeCode.get(c.type);
                    break;
                }
            }
            if (ordinal == -1) {
                Utils.print("Cannot apply where clause, column not found");
                return null;
            }
            if (ordinal == 0) {
                for (Record r : table.records) {
                    int v = Integer.parseInt(value);
                    switch (operator) {
                        case "=":
                            if (r.rowid == v)   result.add(r);
                            break;
                        case ">":
                            if (r.rowid > v)   result.add(r);
                            break;
                        case ">=":
                            if (r.rowid >= v)   result.add(r);
                            break;
                        case "<":
                            if (r.rowid < v)   result.add(r);
                            break;
                        case "<=":
                            if (r.rowid <= v)   result.add(r);
                            break;
                        case "!=":
                            if (r.rowid != v)   result.add(r);
                            break;
                        default:
                            Utils.print("Operator " + operator + " not supported");
                            return null;
                        }
                }
            } else {
                ordinal--;
                for (Record r : table.records) {
                    if (r.typeCodes.get(ordinal) < 0x04) {
                        continue;
                    }
                    if (typeCode >= 0x0C) {
                        String recordValue = r.getString(ordinal);
                        switch (operator) {
                            case "=":
                                if (recordValue.equals(value))   result.add(r);
                                break;
                            case "!=":
                                if (!recordValue.equals(value))   result.add(r);
                                break;
                            default:
                                Utils.print("Cannot apply operator " + operator + " to text data");
                                return null;
                        }
                    } else if ((typeCode >= 0x04 && typeCode <= 0x07) || typeCode == 0x0A || typeCode == 0x0B) {
                        long recordValue = r.getLong(ordinal);
                        long compareValue;
                        if (typeCode == 0x0A) {
                            compareValue = Utils.dateTimeToTimestamp(value);
                        } else if (typeCode == 0x0B) {
                            compareValue = Utils.dateToTimeStamp(value);
                        } else {
                            compareValue = Long.parseLong(value);
                        }
                        switch (operator) {
                            case "=":
                                if (recordValue == compareValue) result.add(r);
                                break;
                            case ">":
                                if (recordValue > compareValue) result.add(r);
                                break;
                            case ">=":
                                if (recordValue >= compareValue) result.add(r);
                                break;
                            case "<":
                                if (recordValue < compareValue) result.add(r);
                                break;
                            case "<=":
                                if (recordValue <= compareValue) result.add(r);
                                break;
                            case "!=":
                                if (recordValue != compareValue) result.add(r);
                                break;
                            default:
                                Utils.print("Operator " + operator + " not supported");
                                return null;
                        }
                    } else {
                        double recordValue = r.getDouble(ordinal);
                        double compareValue = Double.parseDouble(value);
                        switch (operator) {
                            case "=":
                                if (recordValue == compareValue)   result.add(r);
                                break;
                            case ">":
                                if (recordValue > compareValue)   result.add(r);
                                break;
                            case ">=":
                                if (recordValue >= compareValue)   result.add(r);
                                break;
                            case "<":
                                if (recordValue < compareValue)   result.add(r);
                                break;
                            case "<=":
                                if (recordValue <= compareValue)   result.add(r);
                                break;
                            case "!=":
                                if (recordValue != compareValue)   result.add(r);
                                break;
                            default:
                                Utils.print("Operator " + operator + " not supported");
                                return null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (result.size() == 0) {
            Utils.print("No record found!");
        }
        return result;
    }

}
