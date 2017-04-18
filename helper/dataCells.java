package helper;

public class dataCells {
	public int getSize(String dt) {
		int size = -1;
		switch (dt.toLowerCase()) {
		case "tinyint":
			size = 1;
			break;
		case "smallint":
			size = 2;
			break;
		case "int":
			size = 4;
			break;
		case "real":
			size = 4;
			break;
		case "bigint":
			size = 8;
			break;
		case "double":
			size = 8;
			break;
		case "datetime":
			size = 8;
			break;
		case "date":
			size = 8;
			break;
		case "text":
			size = 100;
			break;
		default:
			break;
		}
		return size;
	}
}
