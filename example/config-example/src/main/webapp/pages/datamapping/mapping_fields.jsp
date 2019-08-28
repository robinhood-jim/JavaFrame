<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
	String path = request.getContextPath();
	String CONTEXT_PATH = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>component/dhtmlxSuite/skins/skyblue/dhtmlx.css"/>
<link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/dhtmlx.css" />
<link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/fonts/font_roboto/roboto.css"/>
<link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>resources/css/main.css"/>
<script src="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/dhtmlx.js"></script>
<script language="javascript" src="<%= CONTEXT_PATH %>resources/js/jquery.js"></script>
<script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/jqueryui.js"></script>
<script language="javascript" src="<%= CONTEXT_PATH %>resources/js/Array.js"></script>
<script language="javascript" src="<%= CONTEXT_PATH %>resources/js/control.js"></script>
<script src="<%=CONTEXT_PATH%>resources/js/validate.js"></script>
<style type="text/css">

/* --------------以下为新增css---------------- */
html, body {
	width: 100%;
	height: 100%;
	border:none;
	overflow: hidden;
}

</style>
<script type="text/javascript">
   $(document).ready(function(){
	  
	});
</script>

</head>
<body onload="">

<script type="text/javascript">
	var ctx = "<%=CONTEXT_PATH%>";	
	var projId='${param.projId}';
	var schema='${param.schema}';
	var tableName='${param.table}';
	var sourceId='${sourceId}';
	var mappingId='${mappingId}';
	var imgPath="<%=CONTEXT_PATH%>component/dhtmlxSuite/dhtmlx/imgs/icon/";
	 var dhxLayout = new dhtmlXLayoutObject(document.body, "2E");
	 var topPanel=dhxLayout.cells("a");
	 var bottomPanel=dhxLayout.cells("b");
	 topPanel.setHeight(140);
	 topPanel.setText("");
	 bottomPanel.setText("用户列表");
	 var myForm=topPanel.attachForm();
	 //myForm.setSkin("dhx_skyblue");
	 var myGrid=bottomPanel.attachGrid();
	 var cwidth=document.body.clientWidth;
	 var queryUrl=ctx+"system/user/list";
	 var userFrm=[
						{type: "block", lable:"实体信息",width: 900, list:[
	            		{type: "settings", position: "label-left", labelWidth: 100, inputWidth: 120, offsetLeft: 0},
	            		{type:"label",labelAlign:"left",list:[
						{type:"hidden",name:"mappingId"},
	            		{type:"hidden",name:"gridinput"},
	            		{type:"hidden",name:"schema"},
	            		{type:"hidden",name:"table"},
	            		{type:"hidden",name:"projId"},
	            		{type:"hidden",name:"sourceId"},
	            		{type:"hidden",name:"mappingId"},
	            		{type: "input", label: "模式名",name:"schema", offsetTop: 10},
	            		{type:"checkbox",label:"生成配置",name:"genConfig"},
	            		{type:"checkbox",label:"生成Dao",name:"genDao"},
	            		{type:"newcolumn",offset:10},
	            		{type: "input", label: "表名",name:"tableName"},
	            		{type:"checkbox",label:"生成service",name:"genService"},
                        {type:"checkbox",label:"生成Web",name:"genWeb"},
	            		]}
	            	]}
	            ];
	 topPanel.hideHeader();
	 bottomPanel.hideHeader();
	 topPanel.fixSize(true,true);
	 myForm.loadStruct(userFrm);
	 myForm.setItemValue("tableName",tableName);
	 myForm.setItemValue("table",tableName);
	 myForm.setItemValue("schema",schema);
	 myForm.setItemValue("projId",projId);
	 myForm.setItemValue("sourceId",sourceId);
    myForm.setItemValue("projId",projId);
	if(mappingId!=''){
		myForm.setItemValue("mappingId",mappingId);
	}
	 
	
	var statusbar = bottomPanel.attachStatusBar({height:25});
	statusbar.setText("<div id='recinfoArea'></div>");
	
	 myGrid.setImagePath(ctx+"component/dhtmlxSuite/codebase/imgs/");   
     myGrid.setHeader("字段名,类型,属性名,JAVA类型,中文名,展示方式,表格显示,查询条件,可编辑");//the headers of columns  
     myGrid.setInitWidths("100,100,120,120,120,100,70,70,70");          //the widths of columns  
     myGrid.setColAlign("center,center,center,center,center,center,center,center,center");       //the alignment of columns   
     myGrid.setColTypes("ro,ro,ed,coro,ed,coro,ch,ch,ch");                //the types of columns  
     //myGrid.setColSorting("int,str,str,str,str");          //the sorting types   
     var combotype = myGrid.getCombo(3);
     <c:forEach var="type" items="${requestScope.typeList}" >
     combotype.put('${type.value}','${type.codeName}');
     </c:forEach>
     var combodisplay = myGrid.getCombo(5);
     <c:forEach var="display" items="${requestScope.displayList}" >
     combodisplay.put('${display.value}','${display.codeName}');
     </c:forEach>
 	
	

     myGrid.enableAutoWidth(true);
	 myGrid.enableAutoHeight(true);
     myGrid.init();    
     var dhxToobar = bottomPanel.attachToolbar();
     dhxToobar.setSkin("dhx_skyblue");
     dhxToobar.setIconsPath(ctx+"component/dhtmlxSuite/comm/imgs/");
	 dhxToobar.addButton("config", 0, "配置实体", "settings.gif", "settings_dis.gif");
	 dhxToobar.addButton("savemapping", 1, "保存映射","save.gif", "save_dis.gif");
	 dhxToobar.addButton("genCode", 2, "生成代码","clicknrun.png", "save_dis.gif");
	 dhxToobar.attachEvent("onClick", function(id){
		 if(id=='config'){
			 goConfig();
		 }else if(id=='savemapping'){
			 savemapping();
		 }
		 else if(id=='genCode'){
			 genCode();
		 }
	 });
     goSearch();
     
    function goSearch(){
    	myForm.send(ctx+"system/datamapping/listfields?projId="+projId+"&schema="+schema+"&table="+tableName,function(loader, response){
      	  var tobj= eval('(' + response + ')'); 
      	  myGrid.clearAll();
      	  myGrid.parse(tobj,"json");
      	  //var barstr=tobj.query.pageToolBar;
      	  //statusbar.setText(barstr);
        });      
    	
    }
    var formwidth;
    
    var editFormContent=[
							{type:"settings", position:"label-left",lableWidth: 80,inputWidth: 200},
							{type: "fieldset", label: "配置信息",offsetLeft:10, inputWidth: 700, lableWidth: 100,list:[
							{type: "hidden", name:"id", value:""},
                            {type:"hidden",name:"projId"},
                            {type:"hidden",name:"sourceId"},
                            {type:"hidden",name:"mappingId"},
							{type:"input", name:"dbschema",readonly:"1", label:"模式名:",validate:"NotEmpty"},
							{type:"input", name:"name", label:"中文名:",validate:"NotEmpty"},
							{type:"input", name:"modelPackage", label:"实体包名",validate:"NotEmpty"},
							{type:"input", name:"webPackage", label:"web包名",validate:"NotEmpty"},
							{type:"input", name:"pagePath", label:"页面路径",validate:"NotEmpty"},
							{type:"combo", name:"genType", label:"主键生成",validate:"NotEmpty"},
							{type:"newcolumn",offset:20},
							{type:"input", name:"entityCode", label:"表名:",readonly:"1",validate:"NotEmpty"},
							{type:"input", name:"javaClass", label:"实体名:",validate:"NotEmpty"},
							{type:"input", name:"servicePackage", label:"service包名",validate:"NotEmpty"},
							{type:"input", name:"webPath", label:"url路径",validate:"NotEmpty"},
							{type:"combo", name:"pkType", label:"主键类型",validate:"NotEmpty"},
							]},
							{type: "block", inputWidth: 410, list: [
							                       					{type: "settings", offsetTop: 10},
							                       					{type:"button", name:"cmdOK", value:"确定",offsetLeft: 175},
							                       					{type: "newcolumn"},
							                       					 {type:"button", name:"cmdCancel",value:"取消"}
							                       				]}
	                   ];
	function configInit(form){
		initCombo(form.getCombo("pkType"),ctx+"system/datamapping/getPkType?allowNull=false");
		 initCombo(form.getCombo("genType"),ctx+"system/datamapping/getPkGen?allowNull=false");
		 form.setItemValue("projId",projId);
         form.setItemValue("sourceId",sourceId);
         form.setItemValue("mappingId",mappingId);
		 form.attachEvent("onButtonClick", function(name, command){
			 form.validate();
			  if(name=="cmdOK"){
			          this.send(ctx+"system/datamapping/saveConfig",function(loader, response){
			        	  var tobj= eval('(' + response + ')'); 
			        	  if(tobj.success==true){
			        		  dhtmlx.message({
									text: "保存成功",
									expire: -1
								});
			        	  	 closedialog(true);
			        	 	 reload();
			        	  }else{
			        		  openMsgDialog("保存用户失败","错误信息:"+tobj.message,300,200);
			        	  }
			          });      
			    }else if(name=='cmdCancel'){
			    	closedialog(false);
			    }
			});
		  form.setItemValue("entityCode",tableName);
		  form.setItemValue("dbschema",schema);
		  if(mappingId!=undefined && mappingId!=''){ 
			 $.ajax({
					type:"post",
					url:ctx+"system/datamapping/getConfig?id="+mappingId,
					dataType: "json",
				    success:function(data){
					var obj=eval(data);
					form.setItemValue("id",obj.id);
					form.setItemValue("name",obj.name);
					form.setItemValue("modelPackage",obj.modelPackage);
					form.setItemValue("servicePackage",obj.servicePackage);
					form.setItemValue("webPackage",obj.webPackage);
					form.setItemValue("pagePath",obj.pagePath);
					form.setItemValue("webPath",obj.webPath);
					form.setItemValue("javaClass",obj.javaClass);
					form.setItemValue("genType",obj.genType);
					form.setItemValue("pkType",obj.pkType);
				}
				});
		  }
	}
	
	function goConfig(){
		openWindowForAdd("配置实体信息",editFormContent,730,370,configInit);
	}
	function savemapping(){
		openConfrim("保存映射", "确定继续吗?",'submitSaveMap');
	}
	function genCode(){
		openConfrim("生成代码", "确定继续吗?",'submitGen');
	}
	function submitSaveMap(){
		var context=myGrid.serialize();
		alert(context);
		myForm.setItemValue("gridinput",context);
		myForm.send(ctx+"system/datamapping/saveMapping",function(loader, response){
        	  var tobj= eval('(' + response + ')'); 
        	  if(tobj.success==true){
        		  openMsg('成功',tobj.message);
        	  }else{
        		  openAlert('失败', tobj.message);
        	  }
          });    
	}
	function submitGen(){
		myForm.send(ctx+"system/datamapping/genCode",function(loader, response){
        	  var tobj= eval('(' + response + ')'); 
        	  if(tobj.success==true){
        		  openMsg('成功',tobj.message);
        	  }else{
        		  openAlert('失败', tobj.message);
        	  }
          });    
	}
	
 
</script>
<script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/crud.js"> </script>
<script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/window.js"> </script>


</body>
</html>