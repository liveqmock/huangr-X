<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

String path= request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>网站后台管理</title>
<style type="text/css">
body{
	margin:0px;
	font-family:Arial,"宋体",Verdana,sans-serif;
	font-size:12px;
}
html,body {padding: 0;margin: 0; overflow-x:hidden; overflow-y:auto;}
dl,dd{margin:0px}
ul{
	list-style-type:none;
	margin:0px;
	padding:0px;
}
</style>

<script type="text/javascript">
var app_path="<%=path%>";
var mainpage=false;
</script>

    <script type="text/javascript" src="${context}/js/plug-in/jquery/jquery-1.8.3.js"></script>
    <script type="text/javascript" src="${staticserver }/js/admin/jeap.js"></script>
    <script type="text/javascript" src="${staticserver }/js/common/SelectTree.js"></script>
    <script type="text/javascript" src="<%=path %>/editor/ckeditor362/ckeditor.js"></script>
    <script src="${context }/js/ligerui/js/ligerui.all.js" type="text/javascript"></script>


    <link href="${context}/css/global.css" rel="stylesheet" type="text/css" />
    <link href="${context}/css/input.css" rel="stylesheet" type="text/css" />
    <link href="${context}/css/grid.css" rel="stylesheet" type="text/css" />
    <link href="${context}/css/validate.css" rel="stylesheet" type="text/css" />
    <link href="${context}/css/dialog.css" rel="stylesheet" type="text/css" />
    <link href="${context }/js/ligerui/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <link href="${context }/js/ligerui/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
    <link href="${context }/js/ligerui/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />

</head>
<body>
${content }
</body>
</html>