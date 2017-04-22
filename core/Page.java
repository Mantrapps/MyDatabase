package core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Page class
 * Kai
 */
class Page {
    int offset = 0;         // Position of this page in file
    int type = 0x0D;       // 0x05 for interior page, 0x0d for leaf page
    int numCells = 0;
    int startPos = 0x1FF;
    int pagePtr = 0xFFFFFFFF;
    List<Integer> recordOffsets = new ArrayList<>();
    List<Record> records = new ArrayList<>();

    Page() {}

    // Construct by reading from file
    Page(RandomAccessFile f, int offset) {
        readFromFile(f, offset);
    }

    void readFromFile(RandomAccessFile f, int offset) {
        this.offset = offset;
        try {
            f.seek(offset);  // Jump to start of file
            type = f.readByte();
            numCells = f.readByte();
            startPos = f.readShort();
            pagePtr = f.readInt();
            for (int i=0; i < numCells; i++) {
                recordOffsets.add((int) f.readShort());
            }
            for (int loc : recordOffsets) {
                Record r = new Record(f, offset + loc);
                records.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeToFile(RandomAccessFile f, int offset) {
        this.offset = offset;

        // Update info
        updatePageHeader();

        try {
            f.seek(offset);
            f.writeByte(type);
            f.writeByte(numCells);
            f.writeShort(startPos);
            f.writeInt(pagePtr);
            for (int i=0; i < numCells; i++) {
                f.writeShort(recordOffsets.get(i));
            }
            for (Record r : records) {
                r.writeToFile(f, offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int getAvailSpace() {
        updatePageHeader();
        return startPos - 8 - 2*(numCells+1);
    }

    // Should update header after changing records
    void updatePageHeader() {
        numCells = records.size();
        if (numCells == 0) {
            startPos = 0x1FF;
        } else {
            int size = 0;
            recordOffsets.clear();
            for (int i = 0; i < numCells; i++) {
                recordOffsets.add(records.get(i).offset);
                size += records.get(i).getLen();
            }
            startPos = 0x1FF - size + 1;
        }
    }

}
