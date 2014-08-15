<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/commons/taglibs.jsp"%>



<script src="${staticserver}/js/common/jquery-1.6.4.js" type="text/javascript"></script>
<script src="${staticserver}/js/common/jquery.validate.js" type="text/javascript"></script>
<script src="${context }/js/ligerui/js/core/base.js" type="text/javascript"></script>
<script src="${context }/js/ligerui/js/plugins/ligerDialog.js" type="text/javascript"></script>
<script src="${staticserver}/js/admin/jeap.js" type="text/javascript"></script>
<link href="${context }/css/form.css" rel="stylesheet"/>

<script type="text/javascript">

    var dialog = frameElement.dialog;
    $(function() {
        $("#objForm").validate({
            rules:{

                password:{
                    required:true,
                    minlength:6,
                    maxlength:18
                }
            },

            submitHandler: function ()
            {

                $("#objForm").ajaxSubmit({
                    url :"userAdmin.do?editSave&ajax=true",
                    type : "POST",
                    dataType:"json",
                    success : function(result) {

                        if(result.success){
                            $.ligerDialog.waitting('更新成功');
                            setTimeout(function ()
                            {
                                $.ligerDialog.closeWaitting();

                            }, 1000);
                            window.parent.listgrid.loadData();
                            dialog.close();
                        }else{
                            alert(result.msg)
                        }
                    },
                    error : function(e) {
                        alert("出错啦:(");
                    }
                });

            }
        });
        $("#updatePwd").change(function () {

            if(this.checked){

                $("#pwdtr").show();
                $("#repwdtr").show();
                $( "#password" ).rules( "add", {
                    required:true,
                    minlength: 2,
                    maxlength:18,
                    messages:{
                        required:"请输入新密码",
                        minlength:"密码最少2位",
                        maxlength:"密码最长18位"
                    }

                });
                $( "#repassword" ).rules( "add", {
                    required:true,
                    minlength: 2,
                    maxlength:18,
                    equalTo:'#password',
                    messages:{
                        required:"请输入确认密码",
                        equalTo:"两次输入密码不一致",
                        minlength:"密码最少2位",
                        maxlength:"密码最长18位"

                    }
                });

            }else{
                $("#pwdtr").hide();
                $("#repwdtr").hide();
                $( "#password" ).rules( "remove");
                $( "#repassword" ).rules( "remove");
            }

        });
        $("#notSuperChk").click(function(){

            if(this.checked)
                $("#roletr").show();
        });
        $("#superChk").click(function(){

            if(this.checked)
                $("#roletr").hide();
        });
    });
    function submitForm(){
        $("#objForm").submit();
    }
</script>

<style type="text/css">
    #commentForm { width: 500px; }
    #commentForm label { width: 250px; }
    #commentForm label.error, #commentForm input.submit { margin-left: 253px; }
    #signupForm { width: 670px; }
    #signupForm label.error {
        margin-left: 10px;
        width: auto;
        display: inline;
    }
    #newsletter_topics label.error {
        display: none;
        margin-left: 103px;
    }
    #repwdtr,#pwdtr{display:none}
</style>
<style type="text/css">
    body{ font-size:12px;}
    .l-table-edit {}
    .l-table-edit-td{ padding:4px;}
    .l-button-submit,.l-button-test{width:80px; float:left; margin-left:10px; padding-bottom:2px;}
    .l-verify-tip{ left:230px; top:120px;}

</style>
<form name="objForm" method="post"   id="objForm">
    <input type="hidden" name="userid" value="${adminUser.userid }" />
    <input type="hidden" name="ajax" value="true"/>
    <table cellpadding="0" cellspacing="0" class="l-table-edit" >
        <c:if test="${multiSite==1}">
            <tr>
                <td align="right"><label class="Validform_label">站点：</label></td>
                <td >
                    <select name="adminUser.siteid" id="adminUserSite"/>
                </td>
            </tr>
            <script>
                $(function(){

                    $.ajax({
                        type: "GET",
                        url: "../multiSite!listJson.do",
                        data:   "ajax=yes",
                        dataType:'json',
                        success: function(result){
                            if(result.result==0){
                                $("#adminUserSite").selectTree(result.data);
                            }else{
                                alert("站点列表获取失败，请重试");
                            }
                        },
                        error:function(){
                            alert("站点列表获取失败");
                        }
                    });
                });
            </script>
        </c:if>
        <tr>
            <td align="right" class="l-table-edit-td">用户名:</td>
            <td align="left" class="l-table-edit-td">
                <input name="username" type="text" id="username" class="form-control" value="${adminUser.username }" readonly/>
            </td>
            <td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td"></td>
            <td align="left" class="l-table-edit-td">
                <input type="checkbox" name="check_name" id="updatePwd">同时修改密码
            </td>
        </tr>
        <tr id="pwdtr">
            <td align="right" class="l-table-edit-td">密码:</td>
            <td align="left" class="l-table-edit-td">
                <input name="updatePwd" type="password" id="password"  class="form-control"/>
            </td>
            <td align="left"></td>
        </tr>
        <tr id="repwdtr">
            <td align="right" class="l-table-edit-td">确认密码:</td>
            <td align="left" class="l-table-edit-td">
                <input name="newPassword" type="password" id="repassword"  class="form-control"/>
            </td>
            <td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td" valign="top">类型:</td>
            <td align="left" class="l-table-edit-td">
                <input id="notSuperChk" type="radio" name="founder" value="0" checked="checked" /><label for="notSuperChk">普通管理员</label>
                <input id="superChk" type="radio" name="founder" value="1" /><label for="superChk">超级管理员</label>
            </td><td align="left"></td>
        </tr>
        <tr id="roletr">
            <td align="right" class="l-table-edit-td" valign="top">角色:</td>
            <td colspan="3" class="value">
                <c:forEach var="role" items="${roleList }">
                    &nbsp;<input id="roleids" type="checkbox" name="roleids"  value="${role.roleid }"/><label for="roleids">${role.rolename }&nbsp;</label><br />
                </c:forEach>

            </td>
        </tr>

        <c:forEach items="${htmlList }" var="html">${html }</c:forEach>
        <tr>
            <td align="right" class="l-table-edit-td" valign="top">状态:</td>
            <td align="left" class="l-table-edit-td">
                <input id="active" type="radio" name="state" value="1" checked="checked" /><label for="active">启用</label>
                <input id="inActive" type="radio" name="state" value="1" /><label for="inActive">禁用</label>
            </td><td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td">姓名:</td>
            <td align="left" class="l-table-edit-td"><input name="realname" type="text" id="realname" class="form-control" value="${adminUser.realname }"/></td>
            <td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td">编号:</td>
            <td align="left" class="l-table-edit-td"><input name="userno" type="text" id="userno" value="${adminUser.userno }" class="form-control" /></td>
            <td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td">部门:</td>
            <td align="left" class="l-table-edit-td"><input name="userdept" type="text" id="userdept"  value="${adminUser.userdept }" class="form-control"/></td>
            <td align="left"></td>
        </tr>
        <tr>
            <td align="right" class="l-table-edit-td">备注:</td>
            <td align="left" class="l-table-edit-td">
                <textarea name="remark" cols="100" rows="4"  class="form-control" id="remark" style="width:400px;height: 100px;">${adminUser.remark }</textarea>
            </td>
            <td align="left"></td>
        </tr>

    </table>
</form>


