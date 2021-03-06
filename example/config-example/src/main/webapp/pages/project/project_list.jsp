<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
    String path = request.getContextPath();
    String CONTEXT_PATH = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><spring:message code="title.projManager" /> </title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>component/dhtmlxSuite/skins/skyblue/dhtmlx.css"/>
    <link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/dhtmlx.css"/>
    <link rel="stylesheet" type="text/css"
          href="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/fonts/font_roboto/roboto.css"/>
    <link rel="stylesheet" type="text/css" href="<%=CONTEXT_PATH%>resources/css/main.css"/>
    <script src="<%=CONTEXT_PATH%>component/dhtmlxSuite/codebase/dhtmlx.js"></script>
    <script language="javascript" src="<%= CONTEXT_PATH %>resources/js/jquery.js"></script>
    <script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/jqueryui.js"></script>
    <script language="javascript" src="<%= CONTEXT_PATH %>resources/js/Array.js"></script>
    <script language="javascript" src="<%= CONTEXT_PATH %>resources/js/control.js"></script>
    <script src="<%=CONTEXT_PATH%>resources/js/validate.js"></script>
    <style type="text/css">
        html, body {
            width: 100%;
            height: 100%;
            border: none;
            overflow: hidden;
        }

    </style>
    <script type="text/javascript">
        $(document).ready(function () {

        });
    </script>

</head>
<body onload="">

<script type="text/javascript">
    var ctx = "<%=CONTEXT_PATH%>";
    var imgPath = "<%=CONTEXT_PATH%>component/dhtmlxSuite/dhtmlx/imgs/icon/";
    var dhxLayout = new dhtmlXLayoutObject(document.body, "2E");
    var topPanel = dhxLayout.cells("a");
    var bottomPanel = dhxLayout.cells("b");
    topPanel.setHeight(120);
    topPanel.setText("");
    bottomPanel.setText("");
    var myForm = topPanel.attachForm();
    var cheight = document.body.clientHeight;

    var cwidth = document.body.clientWidth;
    //myForm.setSkin("dhx_skyblue");
    var queryUrl = ctx + "system/project/list";
    var userFrm = [
        {
            type: "block", lable: "项目名", width: cwidth-40, list: [
                {type: "settings", position: "label-left", labelWidth: 130, inputWidth: 120, offsetLeft: 10},
                {
                    type: "label", labelAlign: "left", list: [
                        {type: "hidden", name: "query.order", value: ""},
                        {type: "hidden", name: "query.orderDirection", value: ""},
                        {type: "hidden", name: "query.pageCount", value: ""},
                        {type: "hidden", name: "query.pageNumber", value: "1"},
                        {type: "hidden", name: "query.pageSize", value: "10"},
                        {type: "input", label: "项目名", name: "name", offsetTop: 10},
                        {type: "newcolumn", offset: 20},
                        {type: "input", label: "项目编码", name: "code"},
                    ]
                }
            ]
        },
        {
            type: "block", inputWidth: cwidth-80, list: [
                {type: "button", name: "submit", value: "<spring:message code="btn.submit"/>", offsetLeft: cwidth / 2 - 220}, {
                    type: "newcolumn",
                    offset: 40
                }, {type: "button", name: "reset", value: "<spring:message code="btn.clear"/>"}]
        }
    ];
    topPanel.hideHeader();
    bottomPanel.hideHeader();
    topPanel.fixSize(true, true);
    myForm.loadStruct(userFrm);

    myForm.attachEvent("onButtonClick", function (name) {
        if (name == 'submit') {
            this.send(ctx + "system/project/list", function (loader, response) {
                var tobj = eval('(' + response + ')');
                myGrid.clearAll();
                myGrid.parse(tobj, "json");
                var barstr = tobj.query.pageToolBar;
                statusbar.setText(barstr);
            });
        } else if (name = "reset") {
            this.reset();
        }
    });
    var statusbar = bottomPanel.attachStatusBar({height: 25});
    statusbar.setText("<div id='recinfoArea'></div>");

    var myGrid = bottomPanel.attachGrid();
    myGrid.setImagePath(ctx + "component/dhtmlxSuite/codebase/imgs/");
    myGrid.setHeader("#master_checkbox,项目名,项目编码,持久层,web框架,数据源", null, ["text-align:center", "text-align:center", "text-align:center", "text-align:center", "text-align:center", "text-align:center"]);//the headers of columns
    myGrid.setInitWidths("35,150,150,150,150,150");          //the widths of columns
    myGrid.setColAlign("center,center,center,center,center,center");       //the alignment of columns
    myGrid.setColTypes("ch,ro,ro,ro,ro,ro");                //the types of columns
    myGrid.setColSorting("int,str,str,str,str,str");          //the sorting types
    myGrid.enableAutoWidth(true);
    myGrid.enableAutoHeight(true);
    myGrid.init();
    var dhxToobar = bottomPanel.attachToolbar();
    //dhxToobar.setSkin("dhx_skyblue");
    dhxToobar.setIconsPath(ctx + "component/dhtmlxSuite/comm/imgs/");
    dhxToobar.addButton("new", 0, "<spring:message code="btn.add"/>", "new.gif", "new_dis.gif");
    dhxToobar.addButton("edit", 1, "修改", "open.gif", "open_dis.gif");
    dhxToobar.addButton("delete", 2, "删除", "close.gif", "close_dis.gif");
    dhxToobar.addButton("cvs", 3, "源码管理初始化", "open.gif", "open_dis.gif");
    dhxToobar.addButton("config", 4, "映射实体", "open.gif", "open_dis.gif");
    dhxToobar.addButton("checkin", 5, "代码签入", "pencil.png", "pencil_dis.gif");
    dhxToobar.addButton("genconf", 6, "生成配置", "pencil.png", "pencil_dis.gif");
    dhxToobar.attachEvent("onClick", function (id) {
        if (id == 'new') {
            goAdd();
        } else if (id == 'edit') {
            goEdit();
        } else if (id == 'delete') {
            goDelete();
        } else if (id == 'config') {
            goConfig();
        }else if(id == 'cvs'){
            genCvs();
        }else if(id == 'checkin'){
            checkin();
        }else if (id == 'genconf'){
            genConfig()
        }
    });

    function goSearch() {
        myForm.send(ctx + "system/project/list", function (loader, response) {
            var tobj = eval('(' + response + ')');
            myGrid.clearAll();
            myGrid.parse(tobj, "json");
            var barstr = tobj.query.pageToolBar;
            statusbar.setText(barstr);
        });

    }

    var formwidth;

    var editFormContent = [
        {type: "settings", position: "label-left", lableWidth: 100, inputWidth: 100},
        {
            type: "fieldset", label: "项目信息", offsetLeft: 20, inputWidth: 500, lableWidth: 110, list: [
                {type: "hidden", name: "id", value: ""},
                {type: "input", name: "projName", label: "项目名:", validate: "NotEmpty"},
                {type: "select", name: "projType", label: "工程类型:", validate: "NotEmpty"},
                {type: "newcolumn",offset: 60},
                {type: "checkbox", name: "useAnnotation", label: "使用注解:", checked: "true"},
                {type: "checkbox", name: "useMvc", label: "SpringMvc:", checked: "true"},
                {type: "newcolumn"},
                {type: "select", name: "presistType", label: "持久层类型:", validate: "NotEmpty"},
                {type: "input", name: "projBasePath", label: "初始路径:", validate: "NotEmpty"},
                {type: "newcolumn"},
                {type: "select", name: "webFrameId", label: "前台框架:", validate: "NotEmpty"},
                {type: "input", name: "webBasePath", label: "web路径:", validate: "NotEmpty"},
                {type: "newcolumn"},
                {type: "input", name: "company", label: "公司名称:", validate: "NotEmpty"},
                {type: "select", name: "dataSourceId", label: "数据源:", validate: "NotEmpty"},
                {type: "newcolumn"},
                {type: "input", name: "projCode", label: "项目编码:", validate: "NotEmpty"},
                {type: "select", name: "jarmanType", label: "jar包管理:", validate: "NotEmpty"},
                {type: "newcolumn"},
                {type: "input", name: "annotationPackage", label: "package:", validate: "NotEmpty"},
                {type: "select", name: "teamType", label: "源码管理:", validate: "NotEmpty"},
                {type: "newcolumn"},
                {type: "input", name: "teamUrl", label: "源码管理URL:", validate: "NotEmpty"},
            ]
        },
        {
            type: "block", inputWidth: 410, list: [
                {type: "settings", offsetTop: 10},
                {type: "button", name: "cmdOK", value: "确定", offsetLeft: 175},
                {type: "newcolumn"},
                {type: "button", name: "cmdCancel", value: "取消"}
            ]
        }
    ];
    var makeFormContent = [
        {type: "settings", position: "label-left", lableWidth: 100, inputWidth: 100},
        {
            type: "fieldset", label: "JAR管理", offsetLeft: 10, inputWidth: 500, lableWidth: 100, list: [
                {type: "hidden", name: "id", value: ""},
                {type: "input", name: "projName", label: "项目名:", validate: "NotEmpty"},
                {type: "select", name: "jarmanType", label: "管理方式:", validate: "NotEmpty"},

                {type: "newcolumn", offset: 20},
                {type: "checkbox", name: "useAnnotation", label: "使用注解:", checked: "true"},
                {type: "checkbox", name: "useMvc", label: "SpringMvc:", checked: "true"},
                {type: "newcolumn", offset: 20},
                {type: "combo", name: "presistType", label: "持久层类型:", validate: "NotEmpty"},
                {type: "combo", name: "webFrameId", label: "前台框架:", validate: "NotEmpty"},
                {type: "newcolumn", offset: 20},
                {type: "input", name: "projBasePath", label: "初始路径:", validate: "NotEmpty"},
                {type: "input", name: "webBasePath", label: "web初始路径:", validate: "NotEmpty"},
                {type: "newcolumn", offset: 20},
                {type: "input", name: "company", label: "公司名称:", validate: "NotEmpty"},
                {type: "combo", name: "dataSourceId", label: "数据源:", validate: "NotEmpty"},
                {type: "newcolumn", offset: 20},
                {type: "input", name: "projCode", label: "项目编码:", validate: "NotEmpty"},
                {type: "input", name: "annotationPackage", label: "package:", validate: "NotEmpty"},
            ]
        },
        {
            type: "block", inputWidth: 410, list: [
                {type: "settings", offsetTop: 10},
                {type: "button", name: "cmdOK", value: "确定", offsetLeft: 175},
                {type: "newcolumn"},
                {type: "button", name: "cmdCancel", value: "取消"}
            ]
        }
    ];

    function addInit(form) {

        initCombo(form.getCombo("projType"), ctx + "system/project/projecttype");
        initCombo(form.getCombo("presistType"), ctx + "system/project/presisttype");
        initCombo(form.getCombo("webFrameId"), ctx + "system/project/webframe");
        initCombo(form.getCombo("dataSourceId"), ctx + "system/project/datasource");
        initCombo(form.getCombo("teamType"), ctx + "system/project/teamtype");
        form.attachEvent("onButtonClick", function (name, command) {
            form.validate();
            if (name == "cmdOK") {
                this.send(ctx + "system/project/save", function (loader, response) {
                    var tobj = eval('(' + response + ')');
                    if (tobj.success == 'true') {
                        dhtmlx.message({
                            text: "保存成功",
                            expire: -1
                        });
                        closedialog(true);
                        reload();
                    } else {
                        openMsgDialog("保存用户失败", "错误信息:" + tobj.message, 300, 200);
                    }
                });
            } else if (name == 'cmdCancel') {
                closedialog(false);
            }
        });
    }

    function editInit(form) {
        initCombo(form.getCombo("projType"), ctx + "system/project/projecttype");
        initCombo(form.getCombo("presistType"), ctx + "system/project/presisttype");
        initCombo(form.getCombo("webFrameId"), ctx + "system/project/webframe");
        initCombo(form.getCombo("dataSourceId"), ctx + "system/project/datasource");
        initCombo(form.getCombo("teamType"), ctx + "system/project/teamtype");
        form.attachEvent("onButtonClick", function (name, command) {
            if (name == "cmdOK") {
                this.send(contextpath + "system/project/update", function (loader, response) {
                    var tobj = eval('(' + response + ')');
                    if (tobj.success == 'true') {
                        dhtmlx.message({
                            text: "修改成功",
                            expire: -1
                        });
                        closedialog(true);
                        reload();
                    } else {
                        openMsgDialog("修改用户失败", "错误信息:" + tobj.message, 300, 200);
                    }
                });
            } else if (name == 'cmdCancel') {
                closedialog(false);
            }
        });
    }

    function goAdd() {
        openWindowForAdd("添加工程", editFormContent, 540, 470, addInit);
    }

    function goConfig() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("用户赋权", "请选择用户", 300, 150);
        } else if (list.indexOf(",") != -1) {
            openMsgDialog("用户赋权", "只能修改一个用户", 300, 150);
        } else {
            var form = parent.addTab("system/datamapping/showschema?projId=" + list, "项目配置", list, 100);
        }
    }
    function genCvs() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("源码管理", "请选择项目", 300, 150);
        } else if (list.indexOf(",") != -1) {
            openMsgDialog("源码管理", "只能选择一个项目", 300, 150);
        } else {
            dhtmlx.confirm({
                title: "初始化源码管理",
                type: "confirm-warning",
                text: "确定要进行?",
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            type: "get",
                            url: ctx + "system/project/gencvs/" + list,
                            dataType: "json",
                            success: function (data) {
                                var obj = eval(data);
                                if (obj.success == true) {
                                    dhtmlx.message({
                                        text: "初始化成功",
                                        expire: 10
                                    });
                                    reload();
                                } else {
                                    openMsgDialog("初始化失败", "错误信息:" + obj.message, 300, 200);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    function genConfig() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("源码管理", "请选择项目", 300, 150);
        } else if (list.indexOf(",") != -1) {
            openMsgDialog("源码管理", "只能选择一个项目", 300, 150);
        } else {
            dhtmlx.confirm({
                title: "初始化源码管理",
                type: "confirm-warning",
                text: "确定要进行?",
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            type: "get",
                            url: ctx + "system/project/genconfig/" + list,
                            dataType: "json",
                            success: function (data) {
                                var obj = eval(data);
                                if (obj.success == true) {
                                    dhtmlx.message({
                                        text: "初始化成功",
                                        expire: 20
                                    });
                                    reload();
                                } else {
                                    openMsgDialog("初始化失败", "错误信息:" + obj.message, 300, 200);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    function checkin() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("签入代码", "请选择项目", 300, 150);
        } else if (list.indexOf(",") != -1) {
            openMsgDialog("签入代码", "只能选择一个项目", 300, 150);
        } else {
            dhtmlx.confirm({
                title: "代码签入",
                type: "confirm-warning",
                text: "确定要进行?",
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            type: "get",
                            url: ctx + "system/project/checkin/" + list,
                            dataType: "json",
                            success: function (data) {
                                var obj = eval(data);
                                if (obj.success == true) {
                                    dhtmlx.message({
                                        text: "签入成功",
                                        expire: 20
                                    });
                                    reload();
                                } else {
                                    openMsgDialog("签入失败", "错误信息:" + obj.message, 300, 200);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    function goDelete() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("删除用户", "请选择用户", 300, 200);
        } else {
            dhtmlx.confirm({
                title: "删除",
                type: "confirm-warning",
                text: "确定要删除对应的记录?",
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            type: "get",
                            url: ctx + "system/sysuser/delete?ids=" + list,
                            dataType: "json",
                            success: function (data) {
                                var obj = eval(data);
                                if (obj.success == 'true') {
                                    dhtmlx.message({
                                        text: "删除成功",
                                        expire: 10
                                    });
                                    reload();
                                } else {
                                    openMsgDialog("删除用户失败", "错误信息:" + obj.message, 300, 200);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    function goEdit() {
        var list = myGrid.getCheckedRows(0);
        if (list == '') {
            openMsgDialog("编辑工程", "请选择工程", 300, 150);
        } else if (list.indexOf(",") != -1) {
            openMsgDialog("编辑工程", "只能修改一个工程", 300, 150);
        } else {
            var form = openWindowForEdit("修改工程", editFormContent, 540, 470, editInit);
            //form.load(contextpath+"system/sysuser/edit?id="+list);
            $.ajax({
                type: "get",
                url: ctx + "system/project/view/" + list,
                dataType: "json",
                success: function (data) {
                    var obj = eval(data);
                    form.setItemValue("id", obj.id);
                    form.setItemValue("projType", obj.projType);
                    form.setItemValue("projName", obj.projName);
                    form.setItemValue("webFrameId", obj.webFrameId);
                    form.setItemValue("presistType", obj.presistType);
                    form.setItemValue("company", obj.company);
                    form.setItemValue("useAnnotation", obj.useAnnotation);
                    form.setItemValue("useMvc", obj.useMvc);
                    form.setItemValue("projCode", obj.projCode);
                    form.setItemValue("projBasePath", obj.projBasePath);
                    form.setItemValue("webBasePath", obj.webBasePath);
                    form.setItemValue("dataSourceId", obj.dataSourceId);
                    form.setItemValue("annotationPackage", obj.annotationPackage);
                    form.setItemValue("teamType", obj.teamType);
                    form.setItemValue("teamUrl", obj.Url);
                }
            });
        }
    }

</script>
<script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/crud.js"></script>
<script type="text/javascript" src="<%=CONTEXT_PATH%>resources/js/window.js"></script>


</body>
</html>