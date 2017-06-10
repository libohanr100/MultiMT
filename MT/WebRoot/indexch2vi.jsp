<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import=" com.opensymphony.xwork2.ActionContext" %>
<%

String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
                <title>中国科学院软件研究所汉藏机器翻译系统</title>
				<script type="text/javascript" src="./js/jquery-1.7.2.js"></script>
				<script type="text/javascript" src="./js/myJs.js"></script>
                <link href="./themes/styles/common.css" rel="stylesheet" type="text/css" />
                <link href="./themes/styles/search.css" rel="stylesheet" type="text/css"/>
                <link href="./themes/styles/fanyi.css" rel="stylesheet" type="text/css" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body onload="loadCh2TiOra()">
  <%
    if(ActionContext.getContext().get("outputText") != null) 
   {
   		String result = ActionContext.getContext().get("outputText").toString();
   		if(result.length() == 0)
   		{
  			ActionContext.getContext().put("outputText", "藏文释义");
  		}
  	}
   if(ActionContext.getContext().get("inputText") != null) 
   {
   		String result = ActionContext.getContext().get("inputText").toString();
   		if(result.length() == 0)
   		{
  			ActionContext.getContext().put("inputText", "汉文释义");
  		}
  	}
   %>
    <!--  div class="topWrap" >
	                <div class="top">
		                <div class="logo"><a href="/" title="English Chinese Translation Based on Moses">Home</a></div>
			                
		            </div>
		                <!-- top end -->
                </div>
                <br-->
              <div class="ConBox">
	            <div class="hd">
	             <table border="0" cellpadding="0"  align="center" cellspacing="0" height="30px">
  					

   <tr>
   <td style="position:relative;align:left" width="65px" ><a href="index.jsp"><img name="topmenu_r1"  src="image/topmenu_r1_c1.gif"  width="65px" height="29px" border="0" id="topmenu_r1_c1" alt="" /></a></td>
   <td style="position:relative;align:left;width:65px"><a href="ti2ch.jsp"><img name="topmenu_r2" src="image/topmenu_r1_c2.gif"  width="65px" height="29px" border="0" id="topmenu_r1_c1" alt="" /></a></td>

  <td style="position:relative;width:65px" ><a href="indexora.jsp"><img name="topmenu_r2"  src="image/topmenu_r1_c3.gif"  width="65px" height="29px" border="0" id="topmenu_r1_c2" alt="" /></a></td>
   <td style="position:relative;width:65px" ><a href="ti2chOra.jsp"><img name="topmenu_r2" src="image/topmenu_r1_c4.gif"  width="65px" height="29px" border="0" id="topmenu_r1_c2" alt="" /></a></td>
  	<td style="position:relative;width:80px" ><a href="indexch2vi.jsp"><img name="topmenu_r2" style="position:absolute;z-index:100px" src="image/curmenu_r1_c5.gif"  width="80px" height="32px" border="0" id="topmenu_r1_c2" alt="" /></a></td>
   <td style="position:relative;" ><a href="vi2ch.jsp"><img name="topmenu_r2" src="image/topmenu_r1_c6.gif"  width="65px" height="29px" border="0" id="topmenu_r1_c2" alt="" /></a></td>
  
  				</tr>
  
				</table>
				<div class="input">
                        <div id="inputMod" class="column fl">
                                <div class="wrapper">
                               
<!-- 
                                        <form action="trans_result.php" method="post" id="transForm" name="transForm">-->
					<form action="transch2vi!ch2vi" method="post" id="transch2ti" name="transForm">
                                                <div class="row desc"> <span style="font-family:宋体">源语言：</span>
											<input type="button" value="清除" onclick="clearCh()"/>
                                                </div>
                                                <div class="row border content">
                                                        <textarea id="inputText" class="text" dir="ltr" style="height:587px" tabindex="1" wrap="SOFT" name="inputText"  >${inputText}	</textarea>
                                                       
                                               </div>
                                               
                                               <div class="row">         
							<!--  select id="selectText" name="selectText" onchange="changeSelect()">
								<option value ="cn-ti">Chinese >> Tibetan </option>
								<option value ="ti-cn">Tibetan >> Chinese </option>
							</select-->
							<input type="button"  onclick="submitCh2ti()" value="点击翻译"/>
                              </div>
                                    </form>    
                                 </div>
            <!-- end of wrapper -->
                        </div>
      <!-- end of div inputMod -->
   <div id="inputMod" class="column fl">
            <div class="wrapper">
             
                <!--  form action="transch2ti!ti2ch" method="post" id="transti2ch" name="transForm"-->
                <div class="row desc"><span style="font-family:宋体">目标语言：</span>
                <input type="button" value="清除" onclick="clearTi()"/>
                </div>                                      
                                <div class="row border content">	
                                	<textarea id="outputText"  style="font-size:14px" class="text" dir="ltr" tabindex="1"  wrap="SOFT" name="outputText">${outputText}		</textarea>					
                                </div>							
                            
					  <!-- /form-->
                 
                <!-- end of entryList -->               
                <!-- end translated -->
            </div>
              
            <!-- end of wrapper -->        
              
              
             <div class="row cf" id="addons">
                <a id="feedback_link" target="_blank" href="#" class="fr">Feedback</a>
                <span id="suggestYou">
                    选择<a data-pos="web.o.leftbottom" class="clog-js" data-clog="FUFEI_CLICK" href="http://nlp2ct.sftw.umac.mo/" target="_blank">人工翻译服务</a>，获得更专业的翻译结果。
                </span>
            </div>
        </div>
        </div>
      <div id="errorHolder"><span class="error_text"></span></div>
    </div>
    <div style="clear:both"></div>
    
	<script data-main="fanyi" type="text/javascript" src="./themes/fanyi/v2.1.3.1/scripts/fanyi.js"></script>
		<div id="transBtnTip">
    <div id="transBtnTipInner">
            点击翻译按钮继续，查看网页翻译结果。
            <p class="ar">   
                <a href="#" id="transBtnTipOK">I have known</a> 
            </p>   
            <b id="transBtnTipArrow"></b>
        </div>
    </div>
	
    <!-- div class="footer" style="clear:both">
	<p><a href="http://nlp2ct.sftw.umac.mo/" target="_blank">Conect with us</a> <span>|</span>
       <a href="http://nlp2ct.sftw.umac.mo/" target="_blank">Mosese Translated system</a> <span>|</span>
         Copyright© &nbsp;&nbsp;2012-2012 NLP2CT All Right to Moses Group
    </p>
	<p>More</p>
    </div--> 
    <div class="demo">

	<!-- p style="left:350px;position:relative">
    <a href="mailto:huidan@iscas.ac.cn" target="_blank" >版权所有：中国科学院软件研究所</a><br>
    </p>
    <p style="left:270px;position:relative">
    <a href="mailto:huidan@iscas.ac.cn" target="_blank" >合作单位：中国社会科学院民族学与人类学研究所民族语言应用研究室</a><br>
    </p-->
    
    <p style="left:250px;position:relative">
    <a href="mailto:huidan@iscas.ac.cn" target="_blank">联系方式：中国科学院软件研究所中文信息处理研究小组多语言研究室，huidan@iscas.ac.cn</a><br>
    </p>
    <p style="left:300px;position:relative">
    <a href="/management/index/" target="_blank">技术支持：</a><a href="http://iea.cass.cn/index.htm" target="_blank">中国科学院软件研究所多语言处理研究室</a><br>
	</p>
	<p style="left:280px;position:relative"> 
	 联系地址：北京市中关村南四街4号5号楼，邮政编码：100190<br>
	 </p>
	<p style="left:270px;position:relative">
	 推荐使用Chrome、Firefox浏览器，使用1024*768以上的分辨率
	  </p>
	</div>
</div>

  </body>
</html>