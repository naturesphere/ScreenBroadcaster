package com.IT18Zhang.www.ScreenBroadcast;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.KeyStore.Entry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

import org.hamcrest.core.Is;

public class ScreenBroadCastClient {

	private JFrame frame;
	private JLabel label;
	private  Map<Long,List<IMGFrame>> FramesMap = new HashMap<>();
	private int LIST_CAPACITY = 10;
	private final static int PACK_MAX_LENGTH = 65500;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ScreenBroadCastClient window = new ScreenBroadCastClient();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ScreenBroadCastClient() {
		initialize();
		receiveBroadCast();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(0, 0, 1920, 1080);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		ImageIcon imageIcon = new ImageIcon("C:\\Users\\xiebi\\Pictures\\pf.jpg");
		label.setIcon(imageIcon);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		
	}

	private DatagramPacket Stream2Packet(DatagramSocket socket) throws Exception
	{
		 
		byte [] buf = new byte[PACK_MAX_LENGTH];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);		
		return packet;
	}
	
	public IMGFrame PushPacket(DatagramPacket packet)
	{
		IMGFrame imgFrame = new IMGFrame();
		byte[] buf = packet.getData();
		int off = 0;
		//set time stamp
		imgFrame.setTimeStamp(BroadCastUtils.ByteArray2Long(buf));
		off += BroadCastUtils.LongTypeSize;
		//set packet amount
		imgFrame.setPacketsAmount(buf[off++]);
		//set packet index
		imgFrame.setPacketIndex(buf[off++]);
		//set image data
		imgFrame.setImgData(buf,off,packet.getLength());
		return imgFrame;
		
	}
	
	private void ManageImageFrames(IMGFrame imgFrame) throws Exception
	{
		Long key = imgFrame.getTimeStamp();
		List<IMGFrame> values;
		long oldestKey = -1;
		//insert frame
		if(!FramesMap.keySet().contains(key)) //a new image frame
		{
			if(FramesMap.size()>=LIST_CAPACITY)//no room for a new one	
			{
				oldestKey = Collections.min(FramesMap.keySet());
				FramesMap.remove(oldestKey);//delete the oldest one
			}
			values = new LinkedList<IMGFrame>();
			values.add(imgFrame);
			FramesMap.put(key, values);
		}
		else //same image frame arrived early
		{
			values = FramesMap.get(key);
			values.add(imgFrame);	
		}
	}
	
	private void ShowImage() throws Exception
	{
		if(FramesMap.isEmpty())
			return;
		long oldestKey = Collections.min(FramesMap.keySet());
		List<IMGFrame> values = FramesMap.get(oldestKey);
		if(values.size()==values.get(0).getPacketsAmount())//is image complete
		{
			//show image
			BufferedImage img = DeframeImage(values);
			label.setIcon(new ImageIcon(img));
			frame.repaint();
			FramesMap.remove(oldestKey);
		}
	}
	
	private BufferedImage DeframeImage(List<IMGFrame> imgFrames) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//order list
		Collections.sort(imgFrames, new Comparator<IMGFrame>() {
			@Override
			public int compare(IMGFrame o1, IMGFrame o2) {
				return o1.getPacketIndex()-o2.getPacketIndex();
			}
		});
		//gether image
		for(IMGFrame frame:imgFrames)
			baos.write(frame.getImgData());
		baos.close();
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
		ImageIO.write(img, "JPG", new File("C:\\Users\\xiebi\\Pictures\\receivedPic.jpg"));
		return img;
		
	}
	
	private void receiveBroadCast()
	{
		new Thread()
		{
			public void run() 
			{
				try 
				{
					DatagramSocket socket = new DatagramSocket(9999);
					while(true)
					{
						DatagramPacket packet = Stream2Packet(socket);
						IMGFrame frame = PushPacket(packet);
						BroadCastUtils.printIMGFrame(frame);
						ManageImageFrames(frame);
						ShowImage();
					}
				} catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}
	
}
