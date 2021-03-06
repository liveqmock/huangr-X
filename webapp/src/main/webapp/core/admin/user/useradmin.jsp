<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>
<link href="${context}/js/ligerui/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
<link href="${context}/js/ligerui/skins/Gray/css/all.css" rel="stylesheet" type="text/css" />
<link href="${context}/js/ligerui/skins/ligerui-icons.css" rel="stylesheet" type="text/css" />
<script src=" ${context }/js/ligerui/js/ligerui.all.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerToolBar.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerDialog.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerGrid.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerFilter.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerDrag.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerResizable.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/grid/ligerGrid.showFilter.js" type="text/javascript"></script>
<script src="${context}/js/ligerui/js/plugins/ligerForm.js" type="text/javascript"></script>
<script src="/jeap/adminthemes/default/js/ligerui/js/grid/CustomersData.js" type="text/javascript"></script>
<script src="/jeap/form/config/lab.js" type="text/javascript"></script>
<link href="/jeap/admin/component/cms/lab.css" rel="stylesheet" type="text/css" />

<script src="${ctx}/admin/js/common/crud.js" type="text/javascript"></script>

<script type="text/javascript">
    var listgrid;
    //去掉  大于小于包括,并改变顺序
    $.ligerDefaults.Filter.operators['string'] =
            $.ligerDefaults.Filter.operators['text'] =
                    ["like" , "equal", "notequal", "startwith", "endwith" ];
    $(function ()
    {
        //创建表单结构
       $("#searchForm").ligerForm({
            fields: [
                { display: "用户名", name: "username",  type: "text" }
            ]
        });
        var AllProductData;
        //搜索 按钮
        lab.appendSearchButtons($("#searchForm"), listgrid, false, function ()
        {

            listgrid.options.data = $.extend(true, {}, AllProductData);
        });
        //搜索框 收缩/展开
        $(".searchtitle .togglebtn").live('click', function ()
        {
            if ($(this).hasClass("togglebtn-down")) $(this).removeClass("togglebtn-down");
            else $(this).addClass("togglebtn-down");
            var searchbox = $(this).parent().nextAll("div.searchbox:first");
            searchbox.slideToggle('fast');
        });
    });
    function listToolbar(){
        var items = [];
        <c:forEach var="btn" items="${operationBtns}" varStatus="status">
            items.push({text:'${btn.name}',click:${btn.operType},icon:'${btn.ico}'});
            items.push({ line: true });

        </c:forEach>


        return { items: items };
    }
    function customSearch()
    {
        listgrid.options.data = $.extend(true,{}, CustomersData);
        listgrid.showFilter();
    }
    function addUser(item)
    {
        addOrUpdateDialog(item,'增加管理员','userAdmin.do?add',500,700);
    }
    function modifyUser(item)
    {

        var row = listgrid.getSelectedRow();
        if(row==null){
            $.ligerDialog.error('请选择数据修改!');
            return;
        }
        addOrUpdateDialog(item,'修改管理员','userAdmin.do?edit&id='+row.userid,500,700);

    }
    function delUser(item)
    {
        var row = listgrid.getSelectedRow();
        if(row==null){
            $.ligerDialog.error('请选择数据删除!');
            return;
        }

        delObj(item,"userAdmin.do?delete&id=",row.userid);
    }

    function getStatusName(rowdata,index,value){
        if(value==1){
            return "启用";
        } else{
            return "禁用";
        }
    }
</script>

<grid:dataGrid action="userAdmin.do?dataGrid&ajax=yes" height="99%" width="100%" rownumbers="true" hasSearchBar="true">
    <grid:column title="userid" field="id" align="left" width="100" minWidth="60"/>
    <grid:column title="用户名" field="username"  minWidth="120"/>
    <grid:column title="姓名" field="realname"  minWidth="140"/>
    <grid:column title="状态" field="state" renderFun="getStatusName"/>
    <grid:toolbar title="增加" clickFun="addUser" icon="add"/>
    <grid:toolbar title="修改" clickFun="modifyUser" icon="modify"/>
    <grid:toolbar title="删除" clickFun="delUser" icon="delete"/>
    <grid:toolbar title="自定义搜索" clickFun="customSearch" icon="delete"/>
</grid:dataGrid>
<div style="display: none;">
</div>