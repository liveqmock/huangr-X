package com.easysoft.tag.webapp.taglib.html;

import org.apache.commons.lang3.StringUtils;

import com.easysoft.component.form.manager.IFormManager;
import com.easysoft.component.form.model.AddFormPageMeta;
import com.easysoft.component.form.model.FormEntity;
import com.easysoft.framework.utils.ReflectionUtil;
import com.easysoft.framework.utils.SpringContextHolder;
import com.easysoft.tag.webapp.taglib.HtmlTaglib;

/**
 * 列表标签
 * @author : andy.huang
 * @since :
 */
public class FormTaglib extends HtmlTaglib{
    private String formCode;//对应表单code
    private String entityName;//实体名称，首字母小写，用出传值

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    protected String postStart() {
        if(StringUtils.isEmpty(formCode)){
            throw new RuntimeException("表单编码必须传入");
        }
        IFormManager fromManager = (IFormManager)SpringContextHolder.getBean("formManager");
        FormEntity formEntity  = fromManager.queryFormByCode(formCode);
        if(formEntity==null){
            throw new RuntimeException("表单不存在");
        }

        StringBuilder sb = new StringBuilder();
        /**由上下文中取出分页对象，以便计算总数、分页数**/
        Object obj = this.pageContext.getAttribute(entityName);
        if(obj==null){
            obj = this.getRequest().getAttribute(entityName);
            if(obj!=null){
                this.pageContext.setAttribute(entityName,obj);
            }

        }
        ReflectionUtil reflectionUtil = null;
        sb.append("<form name=\"objForm\" method=\"post\"   id=\"objForm\">");
        if(obj!=null){
            reflectionUtil = new ReflectionUtil(obj);
            sb.append("<input type=\"hidden\" name=\"id\" id=\"id\" value=\""+reflectionUtil.getMethodValue("id")+"\" />");
        }else{
            sb.append("<input type=\"hidden\" name=\"id\" id=\"id\" value=\"0\" />");
        }

        sb.append("<input type=\"hidden\" name=\"ajax\"  value=\"true\" />");
        sb.append("<div></div>");
        sb.append("<table cellpadding=\"0\" cellspacing=\"0\" class=\"l-table-edit\" >");
        /*for(AddFormPageMeta field : formEntity.getAddFormPageMetas()){
           
            sb.append("<tr>");
            sb.append("<td align=\"right\" class=\"l-table-edit-td\">"+field.getTitle()+":</td>");
            sb.append("<td align=\"left\" class=\"l-table-edit-td\">");
            sb.append(field.renderHtmlControl(obj));

            sb.append("</td><td align=\"left\"></td>");


            sb.append("</tr>");
        }*/
        sb.append("</table>");
        return sb.toString();
    }

    @Override
    protected String postEnd() {

        StringBuilder sb = new StringBuilder();

        sb.append("</form>");
        return sb.toString();
    }
}
