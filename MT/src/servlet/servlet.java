package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import preprocess.Preprocess;

import edu.stanford.nlp.ie.crf.CRFClassifier;

public class servlet extends HttpServlet implements Runnable{
	  public static  CRFClassifier segmenter ;
	public servlet()
	{}
	 String dataPath = "/usr/local/tomcat/webapps/MultiMT/data";
	 //String dataPath = "/home/libohan/Workspaces/MT/WebRoot/data";
	public void run()
	{
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(8081);
		
			Socket socket = null;
			Preprocess pr = new Preprocess();
			while (true) {//一直监听，直到受到停止的命令
		
				socket = serverSocket.accept();//如果没有请求，会一直hold在这里等待，有客户端请求的时候才会继续往下执行
				// log
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(socket.getInputStream() , "UTF-8"));//获取输入流(请求)
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				line = bufferedReader.readLine();
				if(line.trim().length() == 0)
				{
					bufferedReader.close();
					socket.close();
					
					continue;
				}
					//while ((line = bufferedReader.readLine()) != null
				//		&& !line.equals("")) {//得到请求的内容，注意这里作两个判断非空和""都要，只判断null会有问题
				stringBuilder.append(line).append("\n");
					//if(line.contains("name=" ) && !line.contains("HTTP/"))
					//{
						// int idx = line.indexOf("=");
						 //String content = line.substring(idx + 1);
				String content = line;
				content = URLDecoder.decode(content, "UTF-8");
				content = content.trim();
					
				if(content.length() == 0)
						continue;
				content =pr. segTibetan(content);
				String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ content+ "'|nc 192.168.9.63 8998"};
				Process process = Runtime.getRuntime().exec (commands);
				process.waitFor();   
				InputStreamReader ir=new
				InputStreamReader(process.getInputStream());
				BufferedReader input = new BufferedReader (ir);
				String res = "", tmp = ""; 
				while ((tmp = input.readLine ()) != null){
				     res += tmp; 
				}
				
				res = res.replaceAll(" ", "");
							
					//}
				Writer printWriter = new OutputStreamWriter(
							socket.getOutputStream());//这里第二个参数表示自动刷新缓存
					//doEcho(printWriter, record);//将日志输出到浏览器
					// release
				printWriter.write(res);
				printWriter.flush();
				printWriter.close();
				ir.close();
				bufferedReader.close();
				input.close();
				socket.close();
			}
			
			
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public void init() throws ServletException {
		
		  new Thread(new servlet()).start();

		  new Thread(new servletWAV()).start();
		 new Thread(new ServletWAVRec()).start();
		 
		 
		    //String basedir = System.getProperty("TransChi2Ti", "/home/libohan/Workspaces/MT/WebRoot/data");
		  String basedir = System.getProperty("TransChi2Ti",  dataPath);

		    Properties props = new Properties();
		    props.setProperty("sighanCorporaDict", basedir);
		    // props.setProperty("NormalizationTable", "data/norm.simp.utf8");
		    // props.setProperty("normTableEncoding", "UTF-8");
		    // below is needed because CTBSegDocumentIteratorFactory accesses it
		    props.setProperty("serDictionary", basedir+ "/dict-chris6.ser.gz");
		    
		    props.setProperty("inputEncoding", "UTF-8");
		    props.setProperty("sighanPostProcessing", "true");

		    segmenter = new CRFClassifier(props);
		    segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		 
	}
}
