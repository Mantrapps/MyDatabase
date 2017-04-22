package core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Record class
 * Kai
 */
class Record {
    int offset = 0;         // Position of this record in page
    int payloadLen = 0;
    int rowid = 0;
    int numColumns = 0;
    List<String> data = new ArrayList<>();
    List<Integer> typeCodes = new ArrayList<>();

    Record() {}

    // Construct by reading from file
    Record(RandomAccessFile f, int offset) {
        readFromFile(f, offset);
    }

    // Read record data to file
    void readFromFile(RandomAccessFile f, int offset) {
        this.offset = offset;
        try {
            f.seek(offset);
            payloadLen = f.readShort();
            rowid = f.readInt();
            numColumns = f.readByte();
            for (int i=0; i < numColumns; i++) {
                int typeCode = f.readByte();
                typeCodes.add(typeCode);
            }
            for (int typeCode : typeCodes) {
                switch (typeCode) {
                    // Null types, just to move file pointer
                    case 0x00: data.add("null"); f.readByte(); break;
                    case 0x01: data.add("null"); f.readShort(); break;
                    case 0x02: data.add("null"); f.readInt(); break;
                    case 0x03: data.add("null"); f.readLong(); break;
                    // Read data and save into lists
                    case 0x04: data.add(String.valueOf(f.readByte())); break;
                    case 0x05: data.add(String.valueOf(f.readShort())); break;
                    case 0x06: data.add(String.valueOf(f.readInt())); break;
                    case 0x07: data.add(String.valueOf(f.readLong())); break;
                    case 0x08: data.add(String.valueOf(f.readFloat())); break;
                    case 0x09: data.add(String.valueOf(f.readDouble())); break;
                    case 0x0A: data.add(String.valueOf(f.readLong())); break;
                    case 0x0B: data.add(String.valueOf(f.readLong())); break;
                }
                if (typeCode >= 0x0C) {
                    StringBuilder sb = new StringBuilder();
                    for (int i=0x0C; i < typeCode; i++) {
                        sb.append((char)f.readByte());
                    }
                    data.add(sb.toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Write record to given offset of file
    void writeToFile(RandomAccessFile f, int pageOffset) {
        try {
            f.seek(pageOffset + offset);
            f.writeShort(payloadLen);
            f.writeInt(rowid);
            f.writeByte(numColumns);
            assert numColumns == typeCodes.size();
            for (int typeCode : typeCodes) {
                f.writeByte(typeCode);
            }
            int idx = 0;
            for (int typeCode : typeCodes) {
                switch (typeCode) {
                    case 0x00: f.writeByte(0); idx++; break;
                    case 0x01: f.writeShort(0); idx++; break;
                    case 0x02: f.writeInt(0); idx++; break;
                    case 0x03: f.writeLong(0); idx++; break;
                    case 0x04: f.writeByte(Byte.parseByte(data.get(idx++))); break;
                    case 0x05: f.writeShort(Short.parseShort(data.get(idx++))); break;
                    case 0x06: f.writeInt(Integer.parseInt(data.get(idx++))); break;
                    case 0x07: f.writeLong(Long.parseLong(data.get(idx++))); break;
                    case 0x08: f.writeFloat(Float.parseFloat(data.get(idx++))); break;
                    case 0x09: f.writeDouble(Double.parseDouble(data.get(idx++))); break;
                    case 0x0A: f.writeLong(Long.parseLong(data.get(idx++))); break;
                    case 0x0B: f.writeLong(Long.parseLong(data.get(idx++))); break;
                }
                if (typeCode >= 0x0C) {
                    for (char c : data.get(idx++).toCharArray()) {
                        f.writeByte(c);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setPayloadLen() {
        payloadLen = 1 + typeCodes.size();
        for (int typeCode : typeCodes) {
            if (typeCode >= 0x0C) {
                payloadLen += typeCode - 0x0C;
            } else {
                payloadLen += Configs.typeCodeLength.get(typeCode);
            }
        }
    }

    int getLen() { return 6 + payloadLen; }

    byte getByte(int ordinal) {
        return Byte.parseByte(data.get(ordinal));
    }

    short getShort(int ordinal) {
        return Short.parseShort(data.get(ordinal));
    }

    int getInt(int ordinal) {
        return Integer.parseInt(data.get(ordinal));
    }

    long getLong(int ordinal) {
        return Long.parseLong(data.get(ordinal));
    }

    float getFloat(int ordinal) {
        return Float.parseFloat(data.get(ordinal));
    }

    double getDouble(int ordinal) {
        return Double.parseDouble(data.get(ordinal));
    }

    String getString(int ordinal) {
        return data.get(ordinal);
    }
}
