package com.easysoft.core.dispatcher.core.freemarker;

import com.easysoft.framework.context.webcontext.ThreadContextHolder;
import com.easysoft.core.ParamSetting;
import com.easysoft.core.freemarker.utils.FreeMarkerUtil;
import com.easysoft.framework.utils.JeapUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMarker解析器
 * @author andy
 */
public final class FreeMarkerParser {
	private static ThreadLocal<FreeMarkerParser> managerLocal = new ThreadLocal<FreeMarkerParser>();
	
	public FreeMarkerParser(){
		data = new HashMap<String, Object>();
		this.clazz =null;
		this.pageFolder =null;
	}
	
	public void setClz(Class clz){
		this.clazz = clz;
	}
	/**
	 * 获取当前线程的 fremarkManager
	 * @return
	 */
	public final static FreeMarkerParser getInstance(){
		
		if( managerLocal.get()==null ){
			throw new RuntimeException("freemarker paser is null");
		}
		FreeMarkerParser fmp =managerLocal.get();
		
		fmp.setPageFolder(null);
		fmp.setWrapPath(true);

		return fmp;
	}
	
	public final static FreeMarkerParser getCurrInstance(){
		if( managerLocal.get()==null ){
			throw new RuntimeException("freemarker paser is null");
		}
		FreeMarkerParser fmp =managerLocal.get();

		return fmp;
	}
	
	public final static void set(FreeMarkerParser fp){
		 managerLocal.set(fp);
	} 
	public final static void remove(){
		 managerLocal.remove();
	} 
	
	private Class clazz;
	public FreeMarkerParser(Class clz){
		this.clazz = clz;
		data = new HashMap<String, Object>();
	}

	public FreeMarkerParser(Class clz, String folder){
		this.clazz = clz;
		this.pageFolder = folder;
		data = new HashMap<String, Object>();
		
	}
	/**
	 * 设置挂件模板的变量
	 * 
	 * @param key
	 * @param value
	 */
	public void putData(String key, Object value) {
		if (key != null && value != null)
			data.put(key, value);
	}
	
	public void putData(Map  map){
		if(map!=null)
			data.putAll(map);
	}
	
	public Object getData(String key){
		if(key==null) return null;
		
		return data.get(key);
	}
	
	private boolean wrapPath=true;
	
	public void setWrapPath(boolean wp){
		wrapPath =wp;
	}

	public String proessPageContent(){
		
		try {
			String name = this.clazz.getSimpleName();
			pageExt = pageExt == null ? ".html" : pageExt;
			name = this.pageName == null ? name : pageName;
		 
			cfg = this.getCfg();
			cfg.setNumberFormat("0.##");
			Template temp = cfg.getTemplate(name + pageExt);
            ByteOutputStream stream = new ByteOutputStream();
            Writer out = new OutputStreamWriter(stream);
			temp.process(data, out);
			out.flush();
			String content = stream.toString();
			
			if(wrapPath){

				content = JeapUtil.wrapjavascript(content, this.getResPath());
				content =  JeapUtil.wrapcss(content, getResPath());

			}

			return content;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

		return "widget  processor error";
	}
    //处理xml内容
    public String processXmlContent(){

        try {
            String name = this.clazz.getSimpleName();
            pageExt = pageExt == null ? ".ftl" : pageExt;
            name = this.pageName == null ? name : pageName;

            cfg = this.getCfg();
            cfg.setNumberFormat("0.##");
            Template temp = cfg.getTemplate(name + pageExt);
            ByteOutputStream stream = new ByteOutputStream();
            Writer out = new OutputStreamWriter(stream);
            temp.process(data, out);
            out.flush();
            String content = stream.toString();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return "widget  processor error";
    }
		
	
	private static   Configuration  cfg;
	

	/*
	 * freemarker data model 通过putData方法设置模板中的值
	 */
	private Map<String, Object> data;

	/*
	 * 模板路径前缀 默认为"" 可以通过 {@link #setPathPrefix(String)} 设置
	 */
	private String pathPrefix;

	/*
	 * 模板文件的名字，默认为与插件类同名
	 */
	private String pageName;

	/*
	 * 模板页面的扩展名，默认为.html
	 */
	private String pageExt;
 
	
	
	/*
	 * 页面所在文件夹
	 * 默认为插件类所在文件夹
	 */
	private String pageFolder; 
	
 
	
	private Configuration getCfg(){
			
			if(cfg==null){
				 cfg = FreeMarkerUtil.getCfg();		
			}

			 
			pathPrefix = pathPrefix== null?"":pathPrefix;
			
			if(pageFolder==null) {//默认使用挂件所在文件夹
				cfg.setClassForTemplateLoading(this.clazz, pathPrefix);
				}
			else{
			  cfg.setServletContextForTemplateLoading(ThreadContextHolder.getHttpRequest().getSession().getServletContext(), pageFolder);
			}
			cfg.setObjectWrapper(new DefaultObjectWrapper());			
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(java.util.Locale.CHINA);
			cfg.setEncoding(java.util.Locale.CHINA, "UTF-8");
		return cfg;
	}

	/**
	 * 设置模板路径前缀
	 * 
	 * @param path
	 */
	public void setPathPrefix(String path) {
		this.pathPrefix = path;
	}

	
	/**
	 * 设置模板文件的名称
	 * 
	 * @param pageName
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * 设置模板页面扩展名
	 * 
	 * @param pageExt
	 */
	public void setPageExt(String pageExt) {
		this.pageExt = pageExt;
	}

	public void setPageFolder(String pageFolder){
		this.pageFolder = pageFolder;
	}

	
	/**
	 * 获取资源根路径
	 * @return
	 */
	private String getResPath(){
		String ctx = ParamSetting.CONTEXT_PATH;
		ctx = ctx.equals("/")?"":ctx;
		if( this.pageFolder ==null ){
			return ctx+"/resource/"+ this.clazz.getPackage().getName().replaceAll("\\.", "/")+"/";
		}else{
			return ctx+pageFolder+"/";
		}
		
	}
	
 

 
 
	
	
	

		
}
