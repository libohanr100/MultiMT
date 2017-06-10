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
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw

			File writename = new File("./input.txt"); // ���·�������û����Ҫ����һ���µ�output��txt�ļ�
			writename.createNewFile(); // �������ļ�
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			out.write(input); // \r\n��Ϊ����
			out.flush(); // �ѻ���������ѹ���ļ�
			out.close(); // ���ǵùر��ļ�
			
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
	        /* ����TXT�ļ� */
			String pathname = "./input.out.txt"; // ����·�������·�������ԣ������Ǿ���·����д���ļ�ʱ��ʾ���·��
			File filename = new File(pathname); // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			
			String tmp= br.readLine();
			tmp = tmp.trim();
			line += tmp + "\n";
			
			while ( (tmp = br.readLine()) != null) {
				tmp = tmp.trim();
				line += tmp + "\n"; // һ�ζ���һ������
			}
			
			/* д��Txt�ļ� */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	
	public String getEncoding(String str) {
		  if (str == null || str.trim().length() < 1)
		    return "";
		  // �����ַ���������
		  String[] encodes = new String[] { "GBK", "ISO-8859-1", "GB2312",
		      "GB18030", "UTF-8" };
		  for (String encode : encodes) {
		    try {
		      // ƥ���ַ�����
		      if (str.equals(new String(str.getBytes(), encode))) {
		        // ���ر�������
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
			   //�þɵ��ַ���������ַ�����������ܻ�����쳣��
			   byte[] bs = str.getBytes(oldCharset);
			   //���µ��ַ����������ַ���
			   result = new String(bs, "UTF-8");
			  }
			return result;
	  }
}
