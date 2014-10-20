package com.easysoft.tag.webapp.taglib.html.support;

import com.easysoft.tag.webapp.taglib.vo.DataGridColumn;
import com.easysoft.tag.webapp.taglib.vo.SearchControl;
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
    private boolean usePager = true;//是否分页
    private String width;//宽度
    private String tree;
    private boolean rownumbers;//是否显示行号

    private boolean hasSearchBar = false;//是否有搜索栏


    private List<DataGridColumn> columns = new ArrayList<DataGridColumn>();
    private List<ToolBar> toolBars = new ArrayList<ToolBar>();
    private List<SearchControl> searchControls = new ArrayList<SearchControl>();//搜索栏
    public void setAction(String action) {
        this.action = action;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setUsePager(boolean usePager) {
        this.usePager = usePager;
    }

    public void setRownumbers(boolean rownumbers) {
        this.rownumbers = rownumbers;
    }

    public void setTree(String tree) {
        this.tree = tree;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHasSearchBar(boolean hasSearchBar) {
        this.hasSearchBar = hasSearchBar;
    }



    @Override
    public int doStartTag() throws JspException {
        columns.clear();
        toolBars.clear();
        searchControls.clear();
        return EVAL_PAGE;
    }

    @Override
    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            if("ligerui".equals(style)){
                out.write(end());
            }else if("html".equals(style)){
                out.write(endHtml());
            }
            else if("easyui".equals(style)){
                if(StringUtils.isNotEmpty(tree)&&"true".equals(tree)){
                    out.write(endEasyUI4Tree());
                }else{
                    out.write(endEasyUI());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EVAL_PAGE;

    }


    public String endHtml(){
        return "";
    }
    private String endEasyUI4Tree(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"main\">");
        sb.append("<div id=\"dialogInfo\" style=\"display: none;\"></div>");
        sb.append(buildToolBar());
        sb.append(buildGrid());
        sb.append("</div>");
        return sb.toString();
    }

    /**
     * 构建工具条
     * @return
     */
    private String buildToolBar(){
        StringBuilder sb = new StringBuilder("<div class=\"buttonArea\">");
        for(ToolBar toolBar : toolBars){
            sb.append(" <a href=\"javascript:void(0)\" class=\"button blueButton\" data-options=\"iconCls:'icon-add',plain:true\" onclick=\""+toolBar.getClickFun()+"()\">"+toolBar.getTitle()+"</a>");
        }
        sb.append("</div>");
        return sb.toString();
    }
    private String buildGrid(){
        StringBuilder sb = new StringBuilder("<form action=\"\" id=\"dataGridform\">");
        sb.append("<table class=\"easyui-treegrid\" id=\"dataGrid\"");
        sb.append("data-options=\"url:'menu.do?dataGrid&ajax=yes',fitColumns:'true',idField: 'id',treeField: 'title'\">");
        sb.append("<thead>");
        sb.append("<tr>");
        for(DataGridColumn column : columns){
            sb.append("<th data-options=\"field:'"+column.getField()+"',width:"+column.getWidth()+",align:'"+column.getAlign()+"'\" ");
            if(StringUtils.isNotEmpty(column.getRenderFun())){
                sb.append("formatter=\""+column.getRenderFun()+"\"");
            }
            sb.append(">");
            sb.append(column.getTitle());
            sb.append("</th>");
        }

        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("</table>");
        sb.append("</form>");
        return sb.toString();
    }
    public String endEasyUI(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"main\">");
        sb.append("<div id=\"dialogInfo\" style=\"display: none;\"></div>");
        sb.append(" <form id=\"dataGridform\">");
        sb.append("<div id=\"tb\" style=\"height: auto\">");
        for(ToolBar toolBar : toolBars){
            sb.append(" <a href=\"javascript:void(0)\" class=\"button blueButton\" data-options=\"iconCls:'icon-add',plain:true\" onclick=\""+toolBar.getClickFun()+"()\">"+toolBar.getTitle()+"</a>");
        }

        sb.append("<span style=\"float: right;\">");
        sb.append("<span id=\"simpleSearch\">");
        List<SearchControl> advances = new ArrayList<SearchControl>();
        if(!searchControls.isEmpty()){
            for(SearchControl control : searchControls){
                if(control.isShortSearch()){
                    sb.append(control.buildSearchControl());
                }else{
                    advances.add(control);
                }
            }
        }

        sb.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" data-options=\"plain:true\" onclick=\"searchMember()\">搜索</a>");
        sb.append("</span>");
        if(!advances.isEmpty()){
            sb.append("<a href=\"javascript:void(0)\" class=\"button\" data-options=\"plain:true\" id=\"aAdvanced\">高级搜索</a>");
        }

        sb.append("</span>");
        sb.append("</div>");

        sb.append("<div style=\"display: block;\" class=\"searchAdvanced\">");
        sb.append(" <input id=\"Advanced\" name=\"Advanced\" type=\"hidden\" value=\"0\"/>");
        sb.append("<table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"8\">");
        for(int i=0;i<advances.size();i++){
            SearchControl control = advances.get(i);
            if(i%3==0){
                sb.append("<tr>");
            }

            sb.append("<th width=\"70\" align=\"right\">"+control.getLabel()+"</th>");
            sb.append("<td>");
            sb.append(control.buildSearchControl());
            sb.append("</td>");

            if((i+1)%3==0){
                sb.append("</tr>");
            }

        }

        sb.append("<tr>");
        sb.append("<td width=\"60\" align=\"right\"></td>");
        sb.append(" <td colspan=\"7\" align=\"center\"><a id=\"searchAdvance\" class=\"button blueButton\" onclick=\"searchMember()\" href=\"javascript:;\">开始搜索</a></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append("<div class=\"clear height10\"></div>");
        sb.append("<div class=\"shadowBoxWhite tableDiv\">");
        sb.append("<table ");
        if(StringUtils.isNotEmpty(tree)&&"true".equals(tree)){
            sb.append("class=\"easyui-treegrid\"");
        }else{
            sb.append("class=\"easyui-datagrid\"");
        }
        sb.append(" pagination=\"true\"  sortName=\"member_id\" sortOrder=\"desc\" id=\"dataGrid\"");
        sb.append(" data-options=\"url:'"+action+"',pageList: [5,10,15,20],fitColumns:'true',singleSelect:true,idField: 'id',treeField: 'title'\"");
        sb.append(">");
        sb.append(" <thead><tr>");

        for(DataGridColumn column : columns){
            sb.append("<th data-options=\"field:'"+column.getField()+"',width:"+column.getWidth()+",align:'"+column.getAlign()+"'\" ");
            if(StringUtils.isNotEmpty(column.getRenderFun())){
                sb.append("formatter=\""+column.getRenderFun()+"\"");
            }
            sb.append(">");
            sb.append(column.getTitle());
            sb.append("</th>");
        }
        sb.append("</tr></thead>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append("</form>");
        sb.append("<div id=\"divdia\" style=\"display: none;\"></div>");

        sb.append("</div>");
        return sb.toString();
    }



    public String end(){
        height=(height==null)?"auto":height;
        width=(width==null)?"auto":width;
        StringBuilder sb = new StringBuilder("<script type=\"text/javascript\">");
        sb.append("var listgrid;");
        sb.append("$(function (){");
        sb.append("listgrid = $(\"#maingrid\").ligerGrid({");
        sb.append("height:'"+height+"',");
        sb.append("width:'"+width+"',");
        sb.append("cssClass:'table-grid',");
        sb.append("usePager:"+usePager+",");
        if(usePager){
            sb.append("pageParmName:'currentPageNo',");
            sb.append("pagesizeParmName:'pageSize',");
        }
        if(StringUtils.isNotEmpty(tree)){
            sb.append("tree:{columnId: 'title',idField: 'id',parentIDField: 'pid'},");
        }

        sb.append("rownumbers:"+rownumbers+",");
        sb.append("columns: [");
        int i=0;
        for(DataGridColumn column : columns){
            String width = StringUtils.isEmpty(column.getWidth())?"auto":column.getWidth();

            sb.append("{");
            sb.append("display:'"+column.getTitle()+"',");
            sb.append("name:'"+column.getField()+"'");
            sb.append(",align:'"+column.getAlign()+"'");
            sb.append(",width:'"+width+"'");
            if(StringUtils.isNotEmpty(column.getMinWidth())){
                sb.append(",minWidth:'"+column.getMinWidth()+"'");
            }
            if(StringUtils.isNotEmpty(column.getId())){
                sb.append(",id:'"+column.getId()+"'");
            }
            if(StringUtils.isNotEmpty(column.getRenderFun())){
                sb.append(",render:"+column.getRenderFun()+"");
            }
            if(StringUtils.isNotEmpty(column.getSortType())){
                sb.append(",type:'"+column.getSortType()+"'");
            }
            if(i==columns.size()-1){
                sb.append("}");
            }else{
                sb.append("},");
            }

            i++;

        }

        sb.append("],");
        sb.append("url:'"+action+"',  pageSize:20 ,");
        sb.append(buildToolBars());
        sb.append("});");
        sb.append("});");
        sb.append("</script>");
        if(this.hasSearchBar){
            sb.append(buildSearchBar());
        }
        sb.append("<div class=\"grid\">");

        sb.append("<div id=\"maingrid\"></div>");
        sb.append("</div><div style=\"display:none;\"></div>");
        return sb.toString();
    }

    private String buildSearchBar(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"searchbar\">");
        sb.append("<div style=\" width:100%\">");
        sb.append("<div class=\"searchtitle\">");
        sb.append(" <span>搜索</span><img src=\"/jeap/admin/images/icons/searchtool.gif\" />");
        sb.append("  <div class=\"togglebtn\"></div>");
        sb.append("</div>");
        sb.append(" <div class=\"navline\" style=\"margin-bottom:10px; margin-top:4px;\"></div>");
        sb.append(" <div class=\"searchbox\">");
        sb.append("<form>");
        if(!searchControls.isEmpty()){
            for(SearchControl control : searchControls){
                sb.append(control.getLabel());
                sb.append("<input type=\"text\" class=\"form-control\" style=\"height: 10px\"");
                sb.append(" id='"+control.getName()+"Qry'/>");

            }

        }

        sb.append(" <button class=\"btn btn-info\" style=\"height: 25px\" onclick=\"return query();\">查询</button>");
        sb.append("</form>");
        sb.append("<div class=\"l-clear\"></div>");
        sb.append("</div></div></div>");
        return sb.toString();
    }

    public void setColumns(DataGridColumn column){
        columns.add(column);
    }
    public void setToolBars(ToolBar toolBar){
        toolBars.add(toolBar);
    }

    public void setSearchControls(SearchControl searchControl){
        searchControls.add(searchControl);
    }

    public String buildToolBars(){
        StringBuffer sb = new StringBuffer();
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
        return sb.toString();
    }
    public String buildToolBars4EasyUI(){
        StringBuffer sb = new StringBuffer();
        sb.append(" <div id=\"tb\" style=\"height: auto\">");
        for(ToolBar toolBar : toolBars){
            sb.append(" <a href=\"javascript:void(0)\" class=\"button blueButton\" data-options=\"iconCls:'icon-add',plain:true\" onclick=\""+toolBar.getClickFun()+"()\">"+toolBar.getTitle()+"</a>");
        }
        sb.append("<span style=\"float: right;\">");
        sb.append("<span id=\"simpleSearch\">");
        sb.append("<input id=\"searchKeyword\" class=\"input_text\" type=\"text\" value=\"\" size=\"30\" style=\"width: 300px;\" placeholder=\"请输入姓名，手机号\" name=\"searchKeyWord\">");
        sb.append("<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" data-options=\"plain:true\" onclick=\"searchMember()\">搜索</a>");
        sb.append("</span>");
        sb.append("</span>");
        sb.append("</div>");

        return sb.toString();
    }
}
