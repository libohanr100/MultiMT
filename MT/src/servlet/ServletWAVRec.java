package servlet;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServletWAVRec implements Runnable{
		public ServletWAVRec()
		{}
		
		public void run()
		{
			Socket socket_ = null;
			System.out.println("初始化完成");
				ServerSocket serverSocket;
				try {
					serverSocket = new ServerSocket(8083);
				
				/*
				
					// 选择进行传输的文件
					 * */
					while (true) {
						
					socket_  = serverSocket.accept();
					DataOutputStream dataout = new DataOutputStream(socket_.getOutputStream());
					while(true)
					{
						if(servletWAV.flag == false)
							break;
					}
					servletWAV.flag = true;
					dataout.writeUTF(servletWAV.share);
					dataout.flush();
					dataout.close();
					

					socket_ .close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		

}
