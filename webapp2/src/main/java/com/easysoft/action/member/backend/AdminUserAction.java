package com.easysoft.action.member.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.easysoft.core.common.controller.BaseController;
import com.easysoft.core.common.vo.json.AjaxJson;
import com.easysoft.core.common.vo.json.DataGridReturn;
import com.easysoft.core.context.EsfContext;
import com.easysoft.framework.db.PageOption;
import com.easysoft.framework.utils.JsonUtils;
import com.easysoft.member.backend.manager.IAdminUserManager;
import com.easysoft.member.backend.manager.ICompanyManager;
import com.easysoft.member.backend.manager.IDepartManager;
import com.easysoft.member.backend.manager.IPermissionManager;
import com.easysoft.member.backend.manager.IRoleManager;
import com.easysoft.member.backend.manager.PermissionManagerException;
import com.easysoft.member.backend.model.AdminUser;
import com.easysoft.member.backend.model.Depart;
import com.easysoft.member.backend.model.Role;
import com.easysoft.member.backend.model.UserRole;
import com.easysoft.member.backend.vo.UserSearchCondition;

/**
 * User: andy
 * Date: 14-1-16
 * Time: 下午1:26
 * @since :1.0
 */
@Controller
@RequestMapping({"/core/admin/user/userAdmin"})
public class AdminUserAction extends BaseController{
    @Autowired
    private IAdminUserManager adminUserManager;
    @Autowired
    private IRoleManager roleManager;
    @Autowired
    private IPermissionManager permissionManager;
    @Autowired
    private ICompanyManager companyManager;
    @Autowired
    private IDepartManager departManager;
    
    /**
     * 查询列表界面
     * @param menuId
     * @return
     * @throws Exception
     */
    @RequestMapping(params = {"list"})
    public ModelAndView list() throws Exception{
        return new ModelAndView("admin/core/auth/useradminlist");
    }
    @RequestMapping(params = {"dataGrid"})
    public ModelAndView dataGrid(PageOption pageOption,String username,String stype,String keyword,UserSearchCondition userSearch){
        if("0".equals(stype)){
        	username = keyword;
        }
        if(StringUtils.isEmpty(username)){
            username=null;
        }
        this.adminUserManager.queryForPage(pageOption,userSearch);
        DataGridReturn dataGridReturn = new DataGridReturn(pageOption.getTotalCount(),(List<AdminUser>)pageOption.getResult());
        String json = JsonUtils.beanToJson(dataGridReturn);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("json",json);
        return new ModelAndView("admin/json_message",map);
    }
    @RequestMapping(params = {"add"})
    public ModelAndView add() throws Exception{
        int multiSite = EsfContext.getContext().getCurrentSite().getMulti_site();
        List<Role> roleList = roleManager.list();

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("roleList",roleList);
        map.put("multiSite",multiSite);

        return new ModelAndView("admin/core/auth/addUserAdmin",map);
    }

    @RequestMapping(params = {"edit"})
    public ModelAndView edit(Integer id) throws Exception {
        int multiSite = EsfContext.getContext().getCurrentSite().getMulti_site();
        List roleList = roleManager.list();
        List<UserRole> userRoles =permissionManager.getUserRoles(id);
        AdminUser adminUser = this.adminUserManager.get(id);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("roleList",roleList);
        map.put("multiSite",multiSite);
        map.put("userRoles",userRoles);
        map.put("adminUser",adminUser);
        Depart depart = null;
        if(adminUser.getUserdept()!=null)
            depart = departManager.queryById(adminUser.getUserdept());
        map.put("depart",depart);
        return new ModelAndView("admin/core/auth/editUserAdmin",map);
    }
    @RequestMapping(params = {"addSave"})
    @ResponseBody
    public AjaxJson  addSave(AdminUser adminUser,int[] roleids) throws Exception {
        AjaxJson json = new AjaxJson();
        try{
            adminUser.setRoleids(roleids);
            adminUserManager.add(adminUser);
            json.setMsg("新增管理员成功");

        } catch (PermissionManagerException e) {
            e.printStackTrace();
            json.setMsg("新增用户失败:"+e.getComment());
            json.setSuccess(false);
        }
        return json;
    }
    @RequestMapping(params = {"chkUserExist"})
    @ResponseBody
    public AjaxJson  chkUserExist() throws Exception {
        AjaxJson json = new AjaxJson();
        try{
            json.setMsg("新增管理员成功");
        } catch (RuntimeException e) {
            json.setMsg(e.getMessage());
            json.setSuccess(false);
        }
        return json;
    }
    @RequestMapping(params = {"delete"})
    @ResponseBody
    public AjaxJson delete(Integer id) throws Exception {
        AjaxJson json = new AjaxJson();
        try {
            this.adminUserManager.delete(id);
            json.setMsg("管理员删除成功");
        } catch (RuntimeException e) {
            json.setMsg("管理员删除失败:" + e.getMessage());
            json.setSuccess(false);
        }

        return json;
    }

    @RequestMapping(params={"editSave"})
    @ResponseBody
    public AjaxJson editSave(String updatePwd, AdminUser adminUser,String newPassword,int[] roleids) throws Exception {
        AjaxJson json = new AjaxJson();
        try {
            if(updatePwd!=null){
                adminUser.setPassword(newPassword);
            }
            adminUser.setRoleids(roleids);
            this.adminUserManager.edit(adminUser);
            json.setMsg("修改管理员成功");

        } catch (RuntimeException e) {
            e.printStackTrace();
            this.logger.error(e,e.fillInStackTrace());
            json.setMsg("管理员修改失败:"+e.getMessage());
            json.setSuccess(false);
        }

        return json;
    }

    @RequestMapping(params = {"checkNameExist"})
    @ResponseBody
    public boolean checkNameExist(String username,Integer userid){
        AdminUser adminUser = adminUserManager.getAdminUserByName(username,userid);
        return adminUser == null;
    }
    
    @RequestMapping(params = {"checkEmailExist"})
    @ResponseBody
    public boolean checkEmailExist(String email,Integer userId){
    	 AdminUser adminUser = adminUserManager.getAdminUserByEmail(email,userId);
         return adminUser == null;
    }
}
