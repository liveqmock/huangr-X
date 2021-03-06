package ${bussiPackage}.${entityPackage}.controller;
import java.util.List;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.easysoft.core.common.vo.json.DataGridReturn;
import com.easysoft.framework.utils.JsonUtils;
import com.easysoft.core.common.controller.BaseController;
import com.easysoft.core.common.vo.json.AjaxJson;
import com.easysoft.core.common.dao.hibernate.DataGrid;
import com.easysoft.framework.utils.StringUtil;
import com.easysoft.framework.utils.BeanUtils;
import java.util.HashMap;
import java.util.Map;
import ${bussiPackage}.${entityPackage}.entity.${entityName}Entity;
import ${bussiPackage}.${entityPackage}.service.${entityName}ServiceI;
/**   
 * @Title: Controller
 * @Description: ${ftl_description}
 * @author onlineGenerator
 * @date ${ftl_create_time}
 * @since : v1.0.0
 *
 */
@Controller
@RequestMapping("/core/admin/${entityName?uncap_first}")
public class ${entityName}Controller extends BaseController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(${entityName}Controller.class);

	@Autowired
	private ${entityName}ServiceI ${entityName?uncap_first}Service;

	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * ${ftl_description}列表 页面跳转
	 * 
	 * @return
	 */
	@RequestMapping(params = "${entityName?uncap_first}")
	public ModelAndView ${entityName?uncap_first}() {
		return new ModelAndView("admin/component/${entityPackage}/${entityName?uncap_first}List");
	}

	/**
	 * easyui AJAX请求数据
	 * 
	 * @param request
	 * @param response
	 * @param dataGrid
	 * @param user
	 */

	@RequestMapping(params = "dataGrid")
	public ModelAndView datagrid(${entityName}Entity ${entityName?uncap_first},HttpServletRequest request, HttpServletResponse response, DataGrid dataGrid) {
        List entityist= this.${entityName?uncap_first}Service.queryForList();
        DataGridReturn dataGridReturn = new DataGridReturn(entityist.size(),entityist);
        String json = JsonUtils.beanToJson(dataGridReturn);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("json",json);
        return new ModelAndView("admin/json_message",map);
	}

    /**
    * ${ftl_description}新增页面跳转
    *
    * @return
    */
    @RequestMapping(params = "goAdd")
    public ModelAndView goAdd(${entityName}Entity ${entityName?uncap_first}, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(${entityName?uncap_first}.getId())) {
            ${entityName?uncap_first} = ${entityName?uncap_first}Service.queryById(${entityName?uncap_first}.getId());
            req.setAttribute("${entityName?uncap_first}Page", ${entityName?uncap_first});
        }
        return new ModelAndView("admin/component/${entityPackage}/${entityName?uncap_first}-add");
    }
    /**
    * 添加${ftl_description}
    *
    * @param ids
    * @return
    */
    @RequestMapping(params = "doAdd")
    @ResponseBody
    public AjaxJson doAdd(${entityName}Entity ${entityName?uncap_first}, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        message = "${ftl_description}添加成功";
        try{
            ${entityName?uncap_first}Service.save(${entityName?uncap_first});

        }catch(Exception e){
            e.printStackTrace();
            message = "${ftl_description}添加失败";
        }
        j.setMsg(message);
        return j;
    }

    /**
    * ${ftl_description}编辑页面跳转
    *
    * @return
    */
    @RequestMapping(params = "goUpdate")
    public ModelAndView goUpdate(${entityName}Entity ${entityName?uncap_first}, HttpServletRequest req) {
        if (StringUtil.isNotEmpty(${entityName?uncap_first}.getId())) {
             ${entityName?uncap_first} = ${entityName?uncap_first}Service.queryById(${entityName?uncap_first}.getId());
            req.setAttribute("${entityName?uncap_first}", ${entityName?uncap_first});
        }
        return new ModelAndView("admin/component/${entityPackage}/${entityName?uncap_first}-add");
    }
    /**
    * 更新${ftl_description}
    *
    * @param ids
    * @return
    */
    @RequestMapping(params = "doUpdate")
    @ResponseBody
    public AjaxJson doUpdate(${entityName}Entity ${entityName?uncap_first}, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();
        message = "${ftl_description}更新成功";
        ${entityName}Entity t = ${entityName?uncap_first}Service.queryById(${entityName?uncap_first}.getId());
        try {
            BeanUtils.copyBeanNotNull2Bean(${entityName?uncap_first}, t);
            ${entityName?uncap_first}Service.update(t);

        } catch (Exception e) {
            e.printStackTrace();
            message = "${ftl_description}更新失败";

        }
        j.setMsg(message);
        return j;
    }

    /**
    * 删除${ftl_description}
    *
    * @return
    */
    @RequestMapping(params = "delete")
    @ResponseBody
    public AjaxJson doDel(${entityName}Entity ${entityName?uncap_first}, HttpServletRequest request) {
        AjaxJson j = new AjaxJson();

        message = "${ftl_description}删除成功";
        try{
            ${entityName?uncap_first}Service.deleteById(${entityName?uncap_first}.getId());

        }catch(Exception e){
            e.printStackTrace();
            message = "${ftl_description}删除失败";

        }
        j.setMsg(message);
        return j;
    }

}
