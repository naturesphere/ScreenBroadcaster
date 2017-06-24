package com.IT18Zhang.www.ScreenBroadcast;

import java.util.Arrays;

public class BroadCastUtils 
{
	public final static int LongTypeSize = 8;
	/*
	 * for test
	 */
	public static void main(String[] args) 
	{
		long timeStamp = System.currentTimeMillis();
		System.out.println(" "+timeStamp);
		long n = ByteArray2Long(Long2ByteArray(timeStamp));
		System.out.println(n);
		/***********************************/
		byte[] srcBuf = {0,1,2,3,4,5,6,7,8,9};
		byte[] desBuf = Arrays.copyOfRange(srcBuf, 1, 5);
		for(byte b:desBuf)
			System.out.print(b+" ");
	}
	
	static byte[] Long2ByteArray(long n)
	{
		
		byte[] buf = new byte [LongTypeSize];
		for(int i=0;i<LongTypeSize;i++)
			buf[i] = (byte) (n>>8*i);
		return buf;
	}
	
	 static long ByteArray2Long(byte[] buf)
	{
		long n=0L;
		
		if(buf.length<LongTypeSize)
			return -1;
		
		for(int i=0;i<LongTypeSize;i++)
			n |= (((long)(0xff&buf[i]))<<8*i);
		
		return n;
	}
	 
	static void printIMGFrame(IMGFrame frame)
	{
		if(frame==null)
			return;
		System.out.println("----------------------------------");
		System.out.println("Time Stamp:"+frame.getTimeStamp());
		System.out.println("Packet Index:"+frame.getPacketIndex());
		System.out.println("Packets Amount:"+frame.getPacketsAmount());
		System.out.println("Image Data Length:"+frame.getImgData().length);
	}
}
