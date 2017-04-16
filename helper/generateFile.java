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
		//ByteBuffer.allocate(4).putInt(0xAABBCCDD).array()
		ByteBuffer bbuf = ByteBuffer.allocate(12);
		bbuf.put((byte)ph.type);
		bbuf.put((byte)ph.numOfCells);
		bbuf.put((byte)(ph.startOfContent>>>8));
		bbuf.put((byte)(ph.startOfContent));
		bbuf.putInt(ph.right);
		bbuf.putInt(128);
		return bbuf.array();
		
	}
	//b+ tree leaf page
	public void leafContent(){
		
	}
	//b+ tree interior page
	public void interiorContent(){
		
	}
}
