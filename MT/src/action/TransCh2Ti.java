package action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
		String inputText= transinfo.getInputText();
		//String selectText = transinfo.getSelectText();

		Preprocess pr = new Preprocess();
		String []lines = inputText.split("\n|\r");
		String res = "";
		for(int k = 0; k < lines.length; k ++)
		{
		inputText = lines[k].trim();
		if(inputText.length() == 0)
			continue;
		
		inputText = pr.convert2UTF8(inputText);
		
		String inputTextTmp = inputText;
		
		//inputText = "中华";
		inputText = inputText.replaceAll(" ", "");
		inputText = inputText.replaceAll("　", "");
		List<String> segmented = servlet.servlet.segmenter.segmentString(inputText);
	   
	    String toDeal = "";
	    for(String i: segmented)
	    {
	    		toDeal = toDeal + " " + i;
	    }
	  //System.out.println(toDeal);
		 String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ toDeal+ "'|nc 192.168.0.111 8997"};
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
		
		 outText = outText.replaceAll(" ", "");
		
		 res += outText + "\n";
		//ActionContext.getContext().put("outputText", outText);
		//ActionContext.getContext().put("inputText", inputTextTmp);
		}
		
		ActionContext.getContext().put("outputText", res);
		ActionContext.getContext().put("inputText", inputText);
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
		String inputText= transinfo.getInputText();
		Preprocess pr = new Preprocess();
		inputText = pr.convert2UTF8(inputText);
		inputText = inputText.replaceAll(" ", "");
		inputText = inputText.replaceAll("　", "");

		 inputText = pr.segTibetan(inputText);

		 //System.out.println(inputText + "DD");
		 String []lines = inputText.split("\n|\r");
			String res = "";
			for(int k = 0; k < lines.length; k ++)
		{
				inputText = lines[k].trim();
				if(inputText.length() == 0)
					continue;
				
				inputText = pr.convert2UTF8(inputText);
				
				String inputTextTmp = inputText;

				String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ inputTextTmp+ "'|nc 192.168.0.111 8998"};
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
				/*
				String unk = findUnk(outText, inputText, true);
				
				commands = new String[]{ "/bin/sh", "-c", "echo '"+ unk+ "'|nc 192.168.11.36 8998"};
				process = Runtime.getRuntime().exec (commands);
				process.waitFor();   
				// for showing the info on screen
				           
				ir=new InputStreamReader(process.getInputStream());
				input = new BufferedReader (ir);
				outText = ""; 
				while ((line = input.readLine ()) != null){
				     outText += line; 
				}
				*/
				/*
				unk = findUnk(outText, unk, true);
				//System.out.println(outText);
				*/
				outText = outText.replaceAll(" ", "");
				 res += outText + "\n";
				// System.out.println(outText);
		}
		//ActionContext.getContext().put("outputText", outText);
		//ActionContext.getContext().put("inputText", inputTextTmp);
			ActionContext.getContext().put("outputText", res);
			ActionContext.getContext().put("inputText", inputText);
		 return "ti2ch";
	}
	
	public String ch2tiOra() throws Exception {
		String inputText= transinfo.getInputText();
		//String selectText = transinfo.getSelectText();

		Preprocess pr = new Preprocess();
		String []lines = inputText.split("\n|\r");
		String res = "";
		for(int k = 0; k < lines.length; k ++)
		{
		inputText = lines[k].trim();
		if(inputText.length() == 0)
			continue;
		
		inputText = pr.convert2UTF8(inputText);
		
		String inputTextTmp = inputText;
		
		//inputText = "中华";
		inputText = inputText.replaceAll(" ", "");
		inputText = inputText.replaceAll("　", "");
		List<String> segmented = servlet.servlet.segmenter.segmentString(inputText);
	   
	    String toDeal = "";
	    for(String i: segmented)
	    {
	    		toDeal = toDeal + " " + i;
	    }
	  //System.out.println(toDeal);
		 String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ toDeal+ "'|nc 192.168.0.111 8999"};
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
		
		 outText = outText.replaceAll(" ", "");
		
		 res += outText + "\n";
		//ActionContext.getContext().put("outputText", outText);
		//ActionContext.getContext().put("inputText", inputTextTmp);
		}
		
		ActionContext.getContext().put("outputText", res);
		ActionContext.getContext().put("inputText", inputText);
		 return "ch2tiOra";
	}
	
	public String ti2chOra() throws Exception {
		String inputText= transinfo.getInputText();
		Preprocess pr = new Preprocess();
		inputText = pr.convert2UTF8(inputText);
		inputText = inputText.replaceAll(" ", "");
		inputText = inputText.replaceAll("　", "");

		 inputText = pr.segTibetan(inputText);

		 //System.out.println(inputText + "DD");
		 String []lines = inputText.split("\n|\r");
			String res = "";
			for(int k = 0; k < lines.length; k ++)
		{
				inputText = lines[k].trim();
				if(inputText.length() == 0)
					continue;
				
				inputText = pr.convert2UTF8(inputText);
				
				String inputTextTmp = inputText;

				String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ inputTextTmp+ "'|nc 192.168.0.111 9000"};
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
				
				outText = outText.replaceAll(" ", "");
				 res += outText + "\n";
				// System.out.println(outText);
		}
		//ActionContext.getContext().put("outputText", outText);
		//ActionContext.getContext().put("inputText", inputTextTmp);
			ActionContext.getContext().put("outputText", res);
			ActionContext.getContext().put("inputText", inputText);
		 return "ti2chOra";
	}
	public String ch2vi() throws Exception {
		String inputText= transinfo.getInputText();
		//String selectText = transinfo.getSelectText();

		Preprocess pr = new Preprocess();
		String []lines = inputText.split("\n|\r");
		String res = "";
		for(int k = 0; k < lines.length; k ++)
		{
		inputText = lines[k].trim();
		if(inputText.length() == 0)
			continue;
		
		inputText = pr.convert2UTF8(inputText);
		
		String outText = "", line; 
		ChineseToVietnameseTranslator.setTargetLanguage("en");

        //测试翻译单句
        ArrayList<String[]> list = ChineseToVietnameseTranslator.translate(inputText);

        //输出结果s
        for (String[] s : list) {
            System.out.println(s[0]+" \n译文: "+s[1]+'\n');
        }
		 outText = outText.replaceAll(" ", "");
		 res += outText + "\n";
		}
		
		ActionContext.getContext().put("outputText", res);
		ActionContext.getContext().put("inputText", inputText);
		 return "ch2vi";
	}
	
	public String vi2ch() throws Exception {
		String inputText= transinfo.getInputText();
		Preprocess pr = new Preprocess();
		inputText = pr.convert2UTF8(inputText);
		inputText = inputText.replaceAll(" ", "");
		inputText = inputText.replaceAll("　", "");

		 inputText = pr.segTibetan(inputText);

		 //System.out.println(inputText + "DD");
		 String []lines = inputText.split("\n|\r");
			String res = "";
			for(int k = 0; k < lines.length; k ++)
		{
				inputText = lines[k].trim();
				if(inputText.length() == 0)
					continue;
				
				inputText = pr.convert2UTF8(inputText);
				
				String inputTextTmp = inputText;

				String []commands = new String[]{ "/bin/sh", "-c", "echo '"+ inputTextTmp+ "'|nc 192.168.0.111 9000"};
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
				
				outText = outText.replaceAll(" ", "");
				 res += outText + "\n";
				// System.out.println(outText);
		}
		//ActionContext.getContext().put("outputText", outText);
		//ActionContext.getContext().put("inputText", inputTextTmp);
			ActionContext.getContext().put("outputText", res);
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
