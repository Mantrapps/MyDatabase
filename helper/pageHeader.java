package helper;

public class pageHeader {
	// b-tree page type 
	// 2 - an interior index b-tree page
	// 5 - an interior table b-tree page
	// 10- an leaf index b-tree page
	// 13- an leaf table b-tree page
	// 1 byte
	int type;
	// 1 byte
	int numOfCells;
	//2 bytes
	int startOfContent;
	//4 bytes right child or right sibling
	int right;
	//page offset location of each cell
	int[] offset;
}
