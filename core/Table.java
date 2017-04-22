package core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Table relative
 * Kai
 */
class Table {
    String name;
    String path;
    List<Column> columns = new ArrayList<>();
    List<Record> records = new LinkedList<>();

    Table(String name) {
        this.name = name;
        switch (name) {
            case Configs.META_TABLE:
                this.path = Configs.META_TABLE_FILE;
                break;
            case Configs.META_COLUMN:
                this.path = Configs.META_COLUMN_FILE;
                break;
            default:
                this.path = Configs.USERDATA_DIR + name + ".tbl";
                break;
        }
    }

    void readFromFile() {
        try(RandomAccessFile f = new RandomAccessFile(path, "rw")) {
            int pageNum = (int) (f.length() / Configs.pageSize);
            for (int i=0; i < pageNum; i++) {
                Page p = new Page(f, (int) (i*Configs.pageSize));
                records.addAll(p.records);
            }

            if (!name.equals(Configs.META_COLUMN)) {
                Table metaColumn = new Table(Configs.META_COLUMN);
                metaColumn.readFromFile();
                for (Record r : metaColumn.records) {
                    if (r.data.get(0).equals(name)) {
                        // Record is for this table
                        String columnName = r.data.get(1);
                        String type = r.data.get(2);
                        int ordinal = Integer.parseInt(r.data.get(3));
                        String nullable = r.data.get(4);
                        Column c = new Column(columnName, type, name);
                        c.ordinalPosition = ordinal;
                        c.isNullable = Boolean.parseBoolean(nullable);
                        columns.add(c);
                    }
                }
            } else {
                for (Record r : records) {
                    if (r.data.get(0).equals(name)) {
                        // Record is for this table
                        String columnName = r.data.get(1);
                        String type = r.data.get(2);
                        int ordinal = Integer.parseInt(r.data.get(3));
                        String nullable = r.data.get(4);
                        Column c = new Column(columnName, type, name);
                        c.ordinalPosition = ordinal;
                        c.isNullable = Boolean.parseBoolean(nullable);
                        columns.add(c);
                    }
                }

            }

            // Sort columns according to ordinal position
            Collections.sort(columns, new Comparator<Column>() {
                @Override
                public int compare(Column o1, Column o2) {
                    return o1.ordinalPosition - o2.ordinalPosition;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeToFile() {
        // Write to file
        try(RandomAccessFile f = new RandomAccessFile(path, "rw")) {
            f.setLength(512);
            Page p = new Page();
            int pageNo = 0;
            for (Record r : records) {
                if (r.getLen() > p.getAvailSpace()) {
                    f.setLength(512 + f.length());
                    p.updatePageHeader();
                    p.writeToFile(f, (int) Configs.pageSize * (pageNo++));
                    p = new Page();
                }
                if (p.startPos == 0x1FF) {
                    r.offset = p.startPos - r.getLen()+1;
                } else {
                    r.offset = p.startPos - r.getLen();
                }

                p.records.add(r);
            }
            if (p.records.size() != 0) {
                p.updatePageHeader();
                p.writeToFile(f, (int) Configs.pageSize * pageNo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
