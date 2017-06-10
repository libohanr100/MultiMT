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
                <title>Moses Translation System</title>
				<script type="text/javascript" src="./js/jquery-1.7.2.js"></script>
				<script type="text/javascript" src="./js/myJs.js"></script>
                <link href="./themes/styles/common.css" rel="stylesheet" type="text/css" />
                <link href="./themes/styles/search.css" rel="stylesheet" type="text/css"/>
                <link href="./themes/styles/fanyi.css" rel="stylesheet" type="text/css" />
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body >
<form method="get" action="http://localhost:8081">
	name:<input type="text" name="name" /><br>
	<input type="submit" />
</form>
  </body>
</html>