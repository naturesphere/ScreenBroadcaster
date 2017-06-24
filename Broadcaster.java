package com.IT18Zhang.www.ScreenBroadcast;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import javax.imageio.ImageIO;

public class Broadcaster 
{
	private final static int PACK_DATA_MAX_LENGTH = 65450;
	
	public static void main(String[] args) {
		try {
			DatagramSocket socket = new DatagramSocket(8888);
			InetSocketAddress desAddr = new InetSocketAddress("localhost", 9999);
			System.out.println("broadcasting ...");
			while(true)
			{
				BroadcastImage(desAddr,socket,printScreen());
				Thread.sleep(10);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static DatagramPacket popPacket( 
												IMGFrame imgF,
												InetSocketAddress desAddr
												) throws Exception 
	{
		ByteArrayOutputStream pdaos = new ByteArrayOutputStream();
		//write time stamp array
		pdaos.write(BroadCastUtils.Long2ByteArray(imgF.getTimeStamp()));
		//write packet amount
		pdaos.write(imgF.getPacketsAmount());
		//write packet index
		pdaos.write(imgF.getPacketIndex());
		//write data			
		pdaos.write(imgF.getImgData());
		pdaos.close();
		byte[] buf = pdaos.toByteArray();
		//set packet
		DatagramPacket packet = new DatagramPacket(buf,buf.length);
		packet.setSocketAddress(desAddr);
		return packet;
	}
	
	private static IMGFrame[] EnframeImage(BufferedImage img) throws Exception 
	{
		//sequence image
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "jpg", baos);
		baos.close();
		//generate info
		byte[] imageData = baos.toByteArray();
		long timeStamp = System.currentTimeMillis();
		long imgLength = imageData.length;
		byte packetsAmount = (byte) ((imgLength+PACK_DATA_MAX_LENGTH-1) / PACK_DATA_MAX_LENGTH);
		//set IMGFrames
		IMGFrame[] imgFrames = new IMGFrame [packetsAmount];
		int off = 0,to = 0;
		for(byte i=0;i<packetsAmount;i++)
		{
			IMGFrame frame = new IMGFrame();
			//set time stamp
			frame.setTimeStamp(timeStamp);
			//set packet amount
			frame.setPacketsAmount(packetsAmount);
			// set packet index
			frame.setPacketIndex(i);
			//set image data
			off = i*PACK_DATA_MAX_LENGTH;
			to = (int) ((i!=packetsAmount-1) ? (i+1)*PACK_DATA_MAX_LENGTH:imgLength);
			frame.setImgData(imageData, off, to);	
			imgFrames[i] = frame;
		}
		return imgFrames;
	}
	
	public static void BroadcastImage(InetSocketAddress desAddr,
										DatagramSocket socket,
										BufferedImage img) throws Exception
	{
		IMGFrame[] imgFrames = EnframeImage(img);
		for(IMGFrame frame:imgFrames)
		{
			BroadCastUtils.printIMGFrame(frame);
			socket.send(popPacket(frame,desAddr));
		}
	}

	public static BufferedImage printScreen() throws Exception
	{
		Robot robot = new Robot();
//		Rectangle rect = new Rectangle(0, 0, 1440, 800);
//		Rectangle rect = new Rectangle(0, 0, 400, 300);
		Rectangle rect = new Rectangle(0, 0, 1920, 1080);
		BufferedImage img = robot.createScreenCapture(rect);
		ImageIO.write(img, "JPG", new File("C:\\Users\\xiebi\\Pictures\\scrnPic.jpg"));
//		System.out.println("over");
		return img;
	}

}
