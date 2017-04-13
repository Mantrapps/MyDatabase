package helper;

import java.nio.ByteBuffer;

public class generateFile {
	//file format
	// page header
	
	public byte[] pageHeader(pageHeader ph){
		/**
		ph.type=type;
		ph.numOfCells=num;
		ph.startOfContent=start;
		ph.right=page;
		ph.offset=new int[num];
		*/
		
		ByteBuffer bbuf = ByteBuffer.allocate(8+2*ph.numOfCells);
		bbuf.putInt(ph.type);
		bbuf.putInt(ph.numOfCells);
		bbuf.putInt(ph.startOfContent);
		bbuf.putInt(ph.right);
		return bbuf.array();
		
	}
	//b+ tree leaf page
	public void leafContent(){
		
	}
	//b+ tree interior page
	public void interiorContent(){
		
	}
}
