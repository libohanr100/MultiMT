package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Preprocess {
	public static String segTibetan(String input)
	{
		if(input.length() == 0)
			return "";
		String line = "";
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

			File writename = new File("./input.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
			writename.createNewFile(); // 创建新文件
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write(input); // \r\n即为换行
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 最后记得关闭文件
			
			//String path = "/home/libohan/apache-tomcat-7.0.63/bin/";//ServletActionContext.getServletContext().getRealPath(File.separator); 
			String path = "/usr/local/tomcat/bin/";
			
			//PythonInterpreter interpreter = new PythonInterpreter();
	       // interpreter.execfile("/home/libohan/git/CRF++-0.58/example/seg/get_segmented_tibetan_text.py");
	       // PyFunction func = (PyFunction) interpreter.get("conv_file_to_segmented",
	        //        PyFunction.class);

	        String inputPath = path + "input.txt", outputPath = path +  "input.out.txt";
	        //PyObject pyobj = func.__call__(new PyString(inputPath), new PyString(outputPath));
	       //String []commands = new String[]{ "/bin/sh", "-c", "python /home/libohan/git/CRF++-0.58/example/seg/get_segmented_tibetan_text.py "
	        String []commands = new String[]{ "/bin/sh", "-c", "python /usr/local/tomcat/webapps/MultiMT/CRF++-0.58/example/seg/get_segmented_tibetan_text.py "
	        				+ inputPath + " " + outputPath};
	     
			Process process = Runtime.getRuntime().exec (commands);
	        process.waitFor();
	        /* 读入TXT文件 */
			String pathname = "./input.out.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
			File filename = new File(pathname); // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			
			String tmp= br.readLine();
			tmp = tmp.trim();
			line += tmp + "\n";
			
			while ( (tmp = br.readLine()) != null) {
				tmp = tmp.trim();
				line += tmp + "\n"; // 一次读入一行数据
			}
			
			/* 写入Txt文件 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	
	public String getEncoding(String str) {
		  if (str == null || str.trim().length() < 1)
		    return "";
		  // 常用字符编码数组
		  String[] encodes = new String[] { "GBK", "ISO-8859-1", "GB2312",
		      "GB18030", "UTF-8" };
		  for (String encode : encodes) {
		    try {
		      // 匹配字符编码
		      if (str.equals(new String(str.getBytes(), encode))) {
		        // 返回编码名称
		        return encode;
		      } else {
		        continue;
		      }
		    } catch (Exception er) {
		    }
		  }
		  return "";
		}
	public String convert2UTF8(String str) throws UnsupportedEncodingException
	{
		String result = "";
		String oldCharset = getEncoding(str);
		if (str != null) {
			   //用旧的字符编码解码字符串。解码可能会出现异常。
			   byte[] bs = str.getBytes(oldCharset);
			   //用新的字符编码生成字符串
			   result = new String(bs, "UTF-8");
			  }
			return result;
	  }
}
