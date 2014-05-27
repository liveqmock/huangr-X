package com.easysoft.tag.webapp.taglib.html.support;

import com.easysoft.tag.webapp.taglib.vo.DataGridColumn;
import com.easysoft.tag.webapp.taglib.vo.ToolBar;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-5-25.
 */
public class DataGridTag extends BodyTagSupport{
    private String action;
    private String style="ligerui";
    private String height;

    private List<DataGridColumn> columns = new ArrayList<DataGridColumn>();
    private List<ToolBar> toolBars = new ArrayList<ToolBar>();

    public void setAction(String action) {
        this.action = action;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setStyle(String style) {
        this.style = style;
    }



    @Override
    public int doStartTag() throws JspException {
        columns.clear();
        toolBars.clear();
        return EVAL_PAGE;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();

        try {
            if("ligerui".equals(style)){
                out.write(end());
            }else{
                out.write("other");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;

    }





    public String end(){
        height=(height==null)?"auto":height;
        StringBuilder sb = new StringBuilder("<script type=\"text/javascript\">");
        sb.append("var listgrid;");
        sb.append("$(function (){");
        sb.append("listgrid = $(\"#maingrid\").ligerGrid({");
        sb.append("height:'"+height+"',");
        sb.append("columns: [");
        int i=0;
        for(DataGridColumn column : columns){
            String width = StringUtils.isEmpty(column.getWidth())?"auto":column.getWidth();
            String minWidth = StringUtils.isEmpty(column.getMinWidth())?"auto":column.getMinWidth();;
            sb.append("{");
            sb.append("display:'"+column.getTitle()+"',");
            sb.append("name:'"+column.getField()+"',");
            sb.append("align:'"+column.getAlign()+"',");
            sb.append("width:'"+width+"',");
            sb.append("minWidth:'"+minWidth+"'");
            if(StringUtils.isNotEmpty(column.getRenderFun())){
                sb.append(",render:"+column.getRenderFun()+"");
            }
            if(i==columns.size()-1){
                sb.append("}");
            }else{
                sb.append("},");
            }

            i++;

        }

        sb.append("],");
        sb.append("url:'"+action+"',  pageSize:30 ,rownumbers:true,");
        sb.append("toolbar: { items: [");
        int j=0;
        for(ToolBar toolBar : toolBars){
            sb.append("{");
            sb.append("text:'"+toolBar.getTitle()+"',");
            sb.append("click:"+toolBar.getClickFun()+",");
            sb.append("icon:'"+toolBar.getIcon()+"'");
            if(j==toolBars.size()-1){
                sb.append("}");
            }else{
                sb.append("},");
                sb.append("{ line: true },");
            }
            j++;
        }
        sb.append("]}");
        sb.append("});");
        sb.append("});");
        sb.append("</script>");
        sb.append("<div class=\"grid\">");
        sb.append("<div id=\"maingrid\"></div>");
        sb.append("</div>");
        return sb.toString();
    }

    public void setColumns(DataGridColumn column){
        columns.add(column);
    }
    public void setToolBars(ToolBar toolBar){
        toolBars.add(toolBar);
    }
}
