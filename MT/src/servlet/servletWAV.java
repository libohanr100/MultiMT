package servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import preprocess.Preprocess;

import edu.stanford.nlp.ie.crf.CRFClassifier;

public class servletWAV extends HttpServlet implements Runnable{
	  public static  CRFClassifier segmenter ;
	  
	  
	  //String pocketPath = "/home/libohan/Workspaces/MT/WebRoot/";
	  //String savePath = "/home/libohan/wavfile";////////////////////////统一是一个file？？？？
	  String pocketPath = "/usr/local/tomcat/webapps/MT/";
	  String savePath = "/usr/local/tomcat/webapps/MT/wavfile";
	  public static String share = "";
	  public static volatile boolean flag = true;
	public servletWAV()
	{}
	void Send2Client(String res, String str) throws UnknownHostException, IOException
	{
		res = res + "$%$" + str;
		share = res;
		flag = false;
		
		/*
		Socket socket_ = new Socket(ip, port);
		//fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
		
		
		DataOutputStream dataout = new DataOutputStream(socket_.getOutputStream());
		dataout.writeUTF(res);
		dataout.flush();
		dataout.close();
		socket_.close();
		*/
	}
	
	String genWAVRes() throws IOException, InterruptedException
	{
		File file = new File(pocketPath+"pocketsphinx-5prealpha/res");

		String []commands = new String[]{ "/bin/sh", "-c", "bash " +pocketPath + "pocketsphinx-5prealpha/pocket  -infile " +savePath+  " -hmm "
		//String []commands = new String[]{ "/bin/sh", "-c", "bash " +pocketPath + "pocketsphinx-5prealpha/pocket  -infile "+pocketPath+"pocketsphinx-5prealpha/tf002_0022_t3000.wav  -hmm "
				+ pocketPath +"pocketsphinx-5prealpha/model_ora/20160413_tibetan_1000_notone/tibetan.cd_cont_1000 -lm "+pocketPath+"pocketsphinx-5prealpha/model_ora/20160413_tibetan_1000_notone/tibetan.lm.bin "
				+" -dict "+pocketPath+"pocketsphinx-5prealpha/model_ora/20160413_tibetan_1000_notone/tibetan.dic 1>"+pocketPath+"pocketsphinx-5prealpha/res 2>info" };
		
		Process process = Runtime.getRuntime().exec (commands);
		process.waitFor();
         
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String str = "", tmp = ""; 
		while ((tmp = reader.readLine ()) != null){
		     str += tmp; 
		}
		return str;
	}
	
	String genTransRes(String str) throws IOException, InterruptedException
	{
		Preprocess pr = new Preprocess();
		
	    String toDeal =pr. segTibetan(str);
	    //String toDeal = str;
		String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ toDeal+ "'|nc 192.168.9.63 8998"};
	    Process process = Runtime.getRuntime().exec (commands);
		process.waitFor();   
		
		// for showing the info on screen
		InputStreamReader ir=new
				InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader (ir);
		           
		String outText = "", line; 
		    
		while ((line = input.readLine ()) != null){
		     outText += line; 
		}
		input.close();
		ir.close();
		 
		outText = outText.replaceAll(" ", "");
		outText = outText.replaceAll("　", "");
		return outText;
	}
	public void run()
	{
		Socket socket = null;
		System.out.println("初始化完成");
		try {
			
			ServerSocket serverSocket = new ServerSocket(8082);
			
			while (true) {
				// 选择进行传输的文件
				socket = serverSocket.accept();
				
				System.out.println("等待客户端连接，连接端口：" + 8082);
				int bufferSize = 8192;
				byte[] buf = new byte[bufferSize];
				BufferedReader reader = null;
				System.out.println("建立socket链接");
				
				
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
				//dis.readByte();
				
				int passedlen = 0;
				long len = 0;
				//String savePath = "/home/libohan/res";
				DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(savePath))));
				
				while (true) {
					
					int read = 0;
					if (dis != null) {
						read = dis.read(buf);
					}
					//passedlen += read;
					if (read == -1) {
						break;
					}
					
					fileOut.write(buf, 0, read);
				}
				
				fileOut.close();
				dis.close();
				
				String str = genWAVRes();
				
				Preprocess pr = new Preprocess();
				str = str.trim();
				if(str.length() == 0)
				{
					Send2Client("", "");
					socket.close();
					continue;
				}
				str = pr.convert2UTF8(str);
				
				str = str.replaceAll(" ", "");
				str = str.replaceAll("　", "");
				
				String outText = genTransRes(str);
				
				// 注意关闭socket链接
				// 直到socket超时，导致数据不完整。
				
				Send2Client(outText, str);
				
					
				socket.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
