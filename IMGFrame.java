package com.IT18Zhang.www.ScreenBroadcast;

import java.util.Arrays;

public class IMGFrame 
{
	private long TimeStamp;
	private byte PacketsAmount;
	private byte PacketIndex;
	private byte[] ImgData;
	
	public long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		TimeStamp = timeStamp;
	}
	public byte getPacketsAmount() {
		return PacketsAmount;
	}
	public void setPacketsAmount(byte packetsAmount) {
		PacketsAmount = packetsAmount;
	}
	public byte getPacketIndex() {
		return PacketIndex;
	}
	public void setPacketIndex(byte packetIndex) {
		PacketIndex = packetIndex;
	}
	public byte[] getImgData() {
		return ImgData;
	}
	public void setImgData(byte[] imgData) {
		ImgData = imgData;
	}
	
	public void setImgData(byte[] Data, int from, int to) 
	{
		ImgData = Arrays.copyOfRange(Data, from, to);
	}
	
}
