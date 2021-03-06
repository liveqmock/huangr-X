package com.easysoft.component.dform.utils;

import java.util.Date;

import com.easysoft.core.utils.ResourceUtil;
import com.easysoft.framework.utils.ReflectHelper;
import com.easysoft.member.backend.manager.impl.UserServiceFactory;

/**
 * 公共方法
 * @author jueyue
 *
 */
public class PublicUtil {
	/**
	 * 表格一般通用字段createDate,createBy,createName
	 * updateDate,updateBy,updateName
	 * @param obj
	 * @param isUpdate
	 */
	public static void setCommonForTable(Object obj, boolean isCreate){
		ReflectHelper reflectHelper=new ReflectHelper(obj);
		if(isCreate){
			reflectHelper.setMethodValue("createDate", new Date());
			reflectHelper.setMethodValue("createBy", UserServiceFactory.getUserService().getCurrentUser().getUserid());
			reflectHelper.setMethodValue("createName", UserServiceFactory.getUserService().getCurrentUser().getUsername());
		}
		reflectHelper.setMethodValue("updateDate", new Date());
		reflectHelper.setMethodValue("updateBy", UserServiceFactory.getUserService().getCurrentUser().getUserid());
		reflectHelper.setMethodValue("updateName", UserServiceFactory.getUserService().getCurrentUser().getUsername());
	}
	/**
	 * 设置checkbox的值 -- Y/N
	 * @param obj
	 * @param params 以逗号隔开  "isNull,isShow,isQuery,isKey"
	 */
	public static void judgeCheckboxValue(Object obj,String params){
		ReflectHelper reflectHelper=new ReflectHelper(obj);
		String [] paramsArr = params.split(",");
		for(int i = 0;i<paramsArr.length;i++){
		    String checked = "N";
		    if(reflectHelper.getMethodValue(paramsArr[i])!=null
		    		&&!"N".equalsIgnoreCase((String)reflectHelper.getMethodValue(paramsArr[i]))){
		    	checked = "Y";
		    }
			reflectHelper.setMethodValue(paramsArr[i],checked);
		}
		
	}

	
	/**
	 * 对比值是否相同
	 * return true(相同) false(不同)
	 */
	public static boolean compareValue(Object oldvalue,Object newvalue){
		if(oldvalue==null){
			if(newvalue!=null){
				return false;
			}
		}else{
			if(newvalue==null){
				return false;
			}else{
				if(!oldvalue.equals(newvalue)){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 通过sql获取表名
	 * @param s
	 * @return
	 */
	public static String getTableName(String s){
		s = s.substring(s.indexOf("from")+4);
		if(s.indexOf("where")>1){
			s = s.substring(0, s.indexOf("where"));
		}
		return s.trim();
	}
}
