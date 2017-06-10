package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import po.TransInfo;
import preprocess.Preprocess;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class TransCh2Ti extends ActionSupport implements ModelDriven<TransInfo>{
	TransInfo transinfo = new TransInfo();
	@Override
	public  TransInfo getModel()
	{
		return transinfo;
	}
	
	
	static String []suffix = {"གཏོང་","བྱེད་","ཞུ་","བྱས་","བརྒྱབ་","བྱ་"};
	@Override
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	

	static String handleUnkSplit(String  s)
	{
	   String [] tmp = s.split("་");
	   String eletmp = "";
		if(tmp.length <= 2)
		{
			//eletmp = s.replaceFirst("་", "་ ");
			eletmp = s.trim();
			int idx = eletmp.indexOf('་');
			String dot = "་";
			int len = dot.length();
			int secIdx = eletmp.indexOf('་', idx + 1);
			if (secIdx != -1 && eletmp.indexOf('་', idx + 1) == eletmp.length() - len 
			      || secIdx  == -1 && eletmp.indexOf('་') != eletmp.length() - len )
			      {
			             eletmp = s.replaceFirst("་", "་ ");
			      }
		}
		else if(tmp.length == 3)
		{
			eletmp = s.trim();
			for(int i = 0; i < suffix.length; i ++)
			{
				int idx = eletmp.indexOf(suffix[i]);
				if(idx != -1)
				{
					if(idx + suffix[i].length() == eletmp.length())
					{
						eletmp = eletmp.replace(suffix[i], "་");
						break;
					}
				}
			}
		}
		return eletmp;
	}
	static String handleUnkSyn(String  s) throws IOException, InterruptedException
	{
		if(s.contains("<") || s.contains(">")) //有这两个符号解码会出错
			return "";
	   String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ s+ "'|nc 192.168.9.63 8997"};
		Process process = Runtime.getRuntime().exec (commands);
		process.waitFor();   
		InputStreamReader ir=new
				InputStreamReader(process.getInputStream());
		BufferedReader input = new BufferedReader (ir);
		String outText = "", line; 
		while ((line = input.readLine ()) != null){
		     outText += line; 
		}
		return outText;
	}
	String findUnk(String s, String origin, boolean syno) throws IOException, InterruptedException
	{
		//String ret = "";
		String [] elements = s.split(" ");
		for (int j = 0; j < elements.length; j ++)
		{
			String ss = elements[j];
			boolean flag = false;
			int cnt = 0;
			for(int i = 0; i < ss.length(); i ++) //判断中文
			{
				char ch = ss.charAt(i);
				int jud = (int) ch;
				if(jud >= 0x4e00 && jud <= 0x9fa5
					 || jud >= 0x3400 && jud <= 0x4db5
					 || jud >= 0x20000 && jud <= 0x2fa1d)
				{
					cnt ++;
				}
			}	
			if(cnt == ss.length())
				flag = true;
			String res = null;
			if(!flag)
			{
				if(!syno)
					res = handleUnkSplit(ss);
				else
					res = handleUnkSyn(ss);
			}
			if(res !=  null && res.length() > 0)
			{
				origin = origin.replace(ss, res);
			}
			
		}
		
		return origin;
	}
	public String ti2ch() throws Exception {
		
		 return "ti2ch";
	}
	
	public String ch2tiOra() throws Exception {
		
		 return "ch2tiOra";
	}
	
	public String ti2chOra() throws Exception {
		
		 return "ti2chOra";
	}
	public String ch2vi() throws Exception {
		String inputText= transinfo.getInputText();
		inputText = inputText.trim();
		String res = "";
		String outText = "", line; 
		ChineseToVietnameseTranslator.setTargetLanguage("vi");
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate(inputText);
        for (String[] s : list) {
        	if(s[1].trim().length() != 0)
        		outText += s[1].trim() + '\n';
        }
		ActionContext.getContext().put("outputText", outText);
		ActionContext.getContext().put("inputText", inputText);
		return "ch2vi";
	}
	
	public String vi2ch() throws Exception {
		String inputText= transinfo.getInputText();
		inputText = inputText.trim();
		String res = "";
		String outText = "", line; 
		ChineseToVietnameseTranslator.setTargetLanguage("zh-CHS");
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate(inputText);
        for (String[] s : list) {
        	if(s[1].trim().length() != 0)
        		outText += s[1].trim() + '\n';
        }
		ActionContext.getContext().put("outputText", outText);
		ActionContext.getContext().put("inputText", inputText);
		 return "vi2ch";
	}
	public static void main(String[] args) throws Exception {
		System.out.println(handleUnkSplit("བརྟན་སྐྱོར་གཏོང་"));
		/*
		String str = "བོད་རྙིང་པར་བུད་མེད་ཀྱི་གནས་བབ་ཧ་ཅང་དམའ་བ་དང་མཐོང་ཆུང་ཆེན་པོ་བྱེད་ཀྱི་ཡོད། \nབོད་ལ་དམངས་གཙོའི་བ";
		//System.out.println(str);
		String s = "项链空间 ད།";
		String [] elements = s.split(" ");
		for (int j = 0; j < elements.length; j ++)
		{
			String ss = elements[j];
			boolean flag = false;
			int cnt = 0;
		for(int i = 0; i < ss.length(); i ++)
		{
			char ch = ss.charAt(i);
			int jud = (int) ch;
			if(jud >= 0x4e00 && jud <= 0x9fa5
					 || jud >= 0x3400 && jud <= 0x4db5
					 || jud >= 0x20000 && jud <= 0x2fa1d)
			{
				cnt ++;
			}
		}	
		if(cnt == ss.length())
			flag = true;
		if(!flag)
			System.out.println(ss);
		}
		/*
		String oldCharset = getEncoding(str);
		if (str != null) {
			   //用旧的字符编码解码字符串。解码可能会出现异常。
			   byte[] bs = str.getBytes(oldCharset);
			   //用新的字符编码生成字符串
			   result = new String(bs, "UTF-8");
			  }
		System.out.println(result);
		System.out.println(oldCharset);
	  */
	  }
}
