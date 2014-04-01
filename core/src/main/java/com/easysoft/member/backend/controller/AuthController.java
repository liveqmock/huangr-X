package com.easysoft.member.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.easysoft.core.common.controller.BaseController;
import com.easysoft.core.common.vo.json.AjaxJson;
import com.easysoft.framework.utils.StringUtil;
import com.easysoft.member.backend.manager.IAuthActionManager;
import com.easysoft.member.backend.vo.AuthVO;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-2-23
 * Time: 上午8:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping({"/core/admin/auth"})
public class AuthController extends BaseController {
    @Autowired
    private IAuthActionManager authActionManager;
    @RequestMapping(params = {"add"})
    public ModelAndView add(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("isEdit",0);
        return new ModelAndView("core/admin/auth/auth_input",map);
    }
    @RequestMapping(params = {"save"})
    @ResponseBody
    public AjaxJson save(AuthVO authVO){
        AjaxJson result = new AjaxJson();

        if(authVO.getEdit()==0){
            return this.saveAdd(authVO.getName(),authVO.getMenuids());
        }else {
            return this.saveEdit(authVO.getName(),authVO.getMenuids(),authVO.getAuthIdInt());
        }

    }
    @RequestMapping(params = {"saveAdd"})
    @ResponseBody
    public AjaxJson saveAdd(String name,Integer[] menuids){
        AjaxJson result = new AjaxJson();
        try{
            com.easysoft.member.backend.model.AuthAction act = new com.easysoft.member.backend.model.AuthAction();
            act.setName(name);
            act.setType("menu");
            act.setObjvalue(StringUtil.arrayToString(menuids, ","));
            int authid = this.authActionManager.add(act);
            result.addAttribute("authid",authid);

        }catch(RuntimeException e){
            this.logger.error(e.getMessage(), e.fillInStackTrace());
            result.setSuccess(false);
            result.setMsg(e.getMessage());

        }
        return result;
    }
    @RequestMapping(params = {"saveEdit"})
    @ResponseBody
    public AjaxJson saveEdit(String name,Integer[] menuids,int authid){
        AjaxJson result = new AjaxJson();
        try{
            com.easysoft.member.backend.model.AuthAction act = new com.easysoft.member.backend.model.AuthAction();
            act.setName(name);
            act.setType("menu");
            act.setActid(authid);
            act.setObjvalue(StringUtil.arrayToString(menuids, ","));
            this.authActionManager.edit(act);
            result.addAttribute("authid",authid);

        }catch(RuntimeException e){
            this.logger.error(e.getMessage(), e.fillInStackTrace());
            result.setMsg(e.getMessage());
        }
        return result;
    }
    @RequestMapping(params = {"delete"})
    @ResponseBody
    public AjaxJson delete(int authid){
        AjaxJson result = new AjaxJson();
        try{
            this.authActionManager.delete(authid);
            result.addAttribute("authid",authid);

        }catch(RuntimeException e){
            this.logger.error(e.getMessage(), e.fillInStackTrace());
            result.setSuccess(false);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
