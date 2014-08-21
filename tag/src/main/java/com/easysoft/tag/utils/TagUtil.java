package com.easysoft.tag.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.easysoft.core.common.dao.hibernate.DataGrid;
import com.easysoft.framework.utils.ReflectionUtil;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


import com.easysoft.core.common.dao.hibernate.PageList;
import com.easysoft.core.common.vo.DataTableReturn;
import com.easysoft.core.model.json.ComboBox;
import com.easysoft.framework.utils.StringUtil;
import com.easysoft.framework.utils.oConvertUtils;
import com.easysoft.member.backend.model.Role;
import com.easysoft.tag.webapp.bean.easyui.Autocomplete;


/**
 * 
 * 类描述：标签工具类
 * 
 * andy
 * @version 1.0
 */
public class TagUtil {
	/**
	 * <b>Summary: </b> getFiled(获得实体Bean中所有属性)
	 * 
	 * @param objClass
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Field[] getFiled(Class objClass) throws ClassNotFoundException {
		Field[] field = null;
		if (objClass != null) {
			Class class1 = Class.forName(objClass.getName());
			field = class1.getDeclaredFields();// 这里便是获得实体Bean中所有属性的方法
			return field;
		} else {
			return field;
		}
	}

	/**
	 * 
	 * 获取对象内对应字段的值
	 * @param fields
	 */
	public static Object fieldNametoValues(String FiledName, Object o){
		Object value = "";
		String fieldName = "";
		String childFieldName = null;
        ReflectionUtil reflectHelper=new ReflectionUtil(o);
		if (FiledName.indexOf("_") == -1) {
			if(FiledName.indexOf(".") == -1){
				fieldName = FiledName;
			}else{
				fieldName = FiledName.substring(0, FiledName.indexOf("."));//外键字段引用名
				childFieldName = FiledName.substring(FiledName.indexOf(".") + 1);//外键字段名
			}
		} else {
			fieldName = FiledName.substring(0, FiledName.indexOf("_"));//外键字段引用名
			childFieldName = FiledName.substring(FiledName.indexOf("_") + 1);//外键字段名
		}
		value = reflectHelper.getMethodValue(fieldName)==null?"":reflectHelper.getMethodValue(fieldName);
		if (value !=""&&value != null && (FiledName.indexOf("_") != -1||FiledName.indexOf(".") != -1)) {
			value = fieldNametoValues(childFieldName, value);
		}
		if(value != "" && value != null) {
			value = value.toString().replaceAll("\r\n", "");
		}
		return value;
	}

	/**
	 * 对象转数组
	 * @param fields
	 * @param o
	 * @param stack
	 * @return
	 * @throws Exception
	 */
	protected static Object[] field2Values(String[] fields, Object o) throws Exception {
		Object[] values = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].toString();
			values[i] = fieldNametoValues(fieldName, o);
		}
		return values;
	}
	/**
	 * 循环LIST对象拼接EASYUI格式的JSON数据
	 * @param fields
	 * @param total
	 * @param list
	 */
	private static String listtojson(String[] fields, int total, List list,String[] footers) throws Exception {
		Object[] values = new Object[fields.length];
		String jsonTemp = "{\'total\':" + total + ",\'rows\':[";
		for (int j = 0; j < list.size(); j++) {
			jsonTemp = jsonTemp + "{\'state\':\'closed\',";
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].toString();
				if(list.get(j) instanceof Map){
					values[i] = ((Map)list.get(j)).get(fieldName);
				}else{
					values[i] = fieldNametoValues(fieldName, list.get(j));
				}
				jsonTemp = jsonTemp + "\'" + fieldName + "\'" + ":\'" + values[i] + "\'";
				if (i != fields.length - 1) {
					jsonTemp = jsonTemp + ",";
				}
			}
			if (j != list.size() - 1) {
				jsonTemp = jsonTemp + "},";
			} else {
				jsonTemp = jsonTemp + "}";
			}
		}
		jsonTemp = jsonTemp + "]";
		if(footers!=null){
			jsonTemp = jsonTemp + ",";
			jsonTemp = jsonTemp + "\'footer\':[";
			jsonTemp = jsonTemp + "{";
			jsonTemp = jsonTemp + "\'name\':\'合计\',";
			for(String footer:footers){
				String footerFiled = footer.split(":")[0];
				Object value = null;
				if(footer.split(":").length==2){
					value = footer.split(":")[1];
				}else{
					value = getTotalValue(footerFiled,list);
				}
				jsonTemp = jsonTemp +"\'"+footerFiled+"\':\'"+value+"\',";
			}
			if(jsonTemp.endsWith(",")){
				jsonTemp = jsonTemp.substring(0,jsonTemp.length()-1);
			}
			jsonTemp = jsonTemp + "}";
			jsonTemp = jsonTemp + "]";
		}
		jsonTemp = jsonTemp + "}";
		return jsonTemp;
	}
	/**
	 * 计算指定列的合计
	 * @param filed 字段名
	 * @param list 列表数据
	 * @return
	 */
	private static Object getTotalValue(String fieldName,List list){
		Double sum = 0D;
		try{
			for (int j = 0; j < list.size(); j++) {
				Double v = 0d;
				String vstr =String.valueOf(fieldNametoValues(fieldName, list.get(j)));
				if(!StringUtil.isEmpty(vstr)){
					v = Double.valueOf(vstr);
				}else{
					
				}
				sum+=v;
			}
		}catch (Exception e) {
			return "";
		}
		return sum;
	}
	/**
	 * 循环LIST对象拼接自动完成控件数据
	 * @param fields
	 * @param total
	 * @param list
	 * @throws Exception 
	 */
	public static String getAutoList(Autocomplete autocomplete, List list) throws Exception {
		String field=autocomplete.getLabelField()+","+autocomplete.getValueField();
		String[] fields=field.split(",");
		Object[] values = new Object[fields.length];
		String jsonTemp = "{\'totalResultsCount\':1,\'geonames\':[";
		if(list.size()>0)
		{
		for (int j = 0; j < list.size(); j++) {
			jsonTemp = jsonTemp + "{'nodate':'yes',";
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].toString();
				values[i] = fieldNametoValues(fieldName, list.get(j));
				jsonTemp = jsonTemp + "\'" + fieldName + "\'" + ":\'" + values[i] + "\'";
				if (i != fields.length - 1) {
					jsonTemp = jsonTemp + ",";
				}
			}
			if (j != list.size() - 1) {
				jsonTemp = jsonTemp + "},";
			} else {
				jsonTemp = jsonTemp + "}";
			}
		}
		}
		else {
			jsonTemp+="{'nodate':'数据不存在'}";
		}
		jsonTemp = jsonTemp + "]}";
		return JSONObject.fromObject(jsonTemp).toString();
	}
	/**
	 * 循环LIST对象拼接DATATABLE格式的JSON数据
	 * @param fields
	 * @param total
	 * @param list
	 */
	private static String datatable(String field, int total, List list) throws Exception {
		String[] fields=field.split(",");
		Object[] values = new Object[fields.length];
		String jsonTemp = "{\'iTotalDisplayRecords\':" + total + ",\'iTotalRecords\':" + total + ",\'aaData\':[";
		for (int j = 0; j < list.size(); j++) {
			jsonTemp = jsonTemp + "{";
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].toString();
				values[i] = fieldNametoValues(fieldName, list.get(j));
				jsonTemp = jsonTemp + "\'" + fieldName + "\'" + ":\'" + values[i] + "\'";
				if (i != fields.length - 1) {
					jsonTemp = jsonTemp + ",";
				}
			}
			if (j != list.size() - 1) {
				jsonTemp = jsonTemp + "},";
			} else {
				jsonTemp = jsonTemp + "}";
			}
		}
		jsonTemp = jsonTemp + "]}";
		return jsonTemp;
	}
	
	/**
	 * 返回列表JSONObject对象
	 * @param field
	 * @param dataGrid
	 * @return
	 */
	private static JSONObject getJson(DataGrid dg) {
		JSONObject jObject = null;
		try {
			if(!StringUtil.isEmpty(dg.getFooter())){
				jObject = JSONObject.fromObject(listtojson(dg.getField().split(","), dg.getTotal(), dg.getResults(),dg.getFooter().split(",")));
			}else{
				jObject = JSONObject.fromObject(listtojson(dg.getField().split(","), dg.getTotal(), dg.getResults(),null));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jObject;

	}
	/**
	 * 返回列表JSONObject对象
	 * @param field
	 * @param dataGrid
	 * @return
	 */
	private static JSONObject getJson(DataTableReturn dataTable,String field) {
		JSONObject jObject = null;
		try {
			jObject = JSONObject.fromObject(datatable(field, dataTable.getiTotalDisplayRecords(), dataTable.getAaData()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jObject;

	}


	/**
	 * 获取指定字段类型 <b>Summary: </b> getColumnType(请用一句话描述这个方法的作用)
	 * 
	 * @param fileName
	 * @param fields
	 * @return
	 */
	public static String getColumnType(String fileName, Field[] fields) {
		String type = "";
		if (fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				String name = fields[i].getName(); // 获取属性的名字
				String filedType = fields[i].getGenericType().toString(); // 获取属性的类型
				if (fileName.equals(name)) {
					if (filedType.equals("class java.lang.Integer")) {
						filedType = "int";
						type = filedType;
					}else if (filedType.equals("class java.lang.Short")) {
						filedType = "short";
						type = filedType;
					}else if (filedType.equals("class java.lang.Double")) {
						filedType = "double";
						type = filedType;
					}else if (filedType.equals("class java.util.Date")) {
						filedType = "date";
						type = filedType;
					}else if (filedType.equals("class java.lang.String")) {
						filedType = "string";
						type = filedType;
					}else if (filedType.equals("class java.sql.Timestamp")) {
						filedType = "Timestamp";
						type = filedType;
					}else if (filedType.equals("class java.lang.Character")) {
						filedType = "character";
						type = filedType;
					}else if (filedType.equals("class java.lang.Boolean")) {
						filedType = "boolean";
						type = filedType;
					}else if (filedType.equals("class java.lang.Long")) {
						filedType = "long";
						type = filedType;
					}

				}
			}
		}
		return type;
	}

	/**
	 * 
	 * <b>Summary: </b> getSortColumnIndex(获取指定字段索引)
	 * 
	 * @param fileName
	 * @param fieldString
	 * @return
	 */
	protected static String getSortColumnIndex(String fileName, String[] fieldString) {
		String index = "";
		if (fieldString.length > 0) {
			for (int i = 0; i < fieldString.length; i++) {
				if (fileName.equals(fieldString[i])) {
					int j = i + 1;
					index = oConvertUtils.getString(j);
				}
			}
		}
		return index;

	}

	// JSON返回页面MAP方式
	public static void ListtoView(HttpServletResponse response, PageList pageList) {
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pageList.getCount());
		map.put("rows", pageList.getResultList());
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = JSONObject.fromObject(map);
		try {
			mapper.writeValue(response.getWriter(), map);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 控件类型：easyui
	 * 返回datagrid JSON数据
	 * @param response
	 * @param dataGrid
	 */
	public static void datagrid(HttpServletResponse response,DataGrid dg) {
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		JSONObject object = TagUtil.getJson(dg);
		try {
			PrintWriter pw=response.getWriter();
			pw.write(object.toString());
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 控件类型：datatable
	 * 返回datatable JSON数据
	 * @param response
	 * @param datatable
	 */
	public static void datatable(HttpServletResponse response, DataTableReturn dataTableReturn,String field) {
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-store");
		JSONObject object = TagUtil.getJson(dataTableReturn,field);
		try {
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 手工拼接JSON
	 */
	public static String getComboBoxJson(List<Role> list, List<Role> roles) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (Role node : list) {
			if (roles.size() > 0) {
				buffer.append("{\"id\":" + node.getRoleid() + ",\"text\":\"" + node.getRolename() + "\"");
				for (Role node1 : roles) {
					if (node.getRoleid() == node1.getRoleid()) {
						buffer.append(",\"selected\":true");
					}
				}
				buffer.append("},");
			} else {
				buffer.append("{\"id\":" + node.getRoleid() + ",\"text\":\"" + node.getRolename() + "\"},");
			}

		}
		buffer.append("]");

		// 将,\n]替换成\n]

		String tmp = buffer.toString();
		tmp = tmp.replaceAll(",]", "]");
		return tmp;

	}

	/**
	 * 根据模型生成JSON
	 * @param all 全部对象
	 * @param in  已拥有的对象
	 * @param comboBox 模型
	 * @return
	 */
	public static List<ComboBox> getComboBox(List all, List in, ComboBox comboBox) {
		List<ComboBox> comboxBoxs = new ArrayList<ComboBox>();
		String[] fields = new String[] { comboBox.getId(), comboBox.getText() };
		Object[] values = new Object[fields.length];
		for (Object node : all) {
			ComboBox box = new ComboBox();
            ReflectionUtil reflectHelper=new ReflectionUtil(node);
			for (int i = 0; i < fields.length; i++) {
				String fieldName = fields[i].toString();
				values[i] = reflectHelper.getMethodValue(fieldName);
			}
			box.setId(values[0].toString());
			box.setText(values[1].toString());
			if (in != null) {
				for (Object node1 : in) {
                    ReflectionUtil reflectHelper2=new ReflectionUtil(node);
					if (node1 != null) {
						String fieldName = fields[0].toString();
						String	test = reflectHelper2.getMethodValue(fieldName).toString();
						if (values[0].toString().equals(test)) {
							box.setSelected(true);
						}
					}
				}
			}
			comboxBoxs.add(box);
		}
		return comboxBoxs;

	}
	/**
	 * 获取自定义函数名
	 * 
	 * @param functionname
	 * @return
	 */
	public static String getFunction(String functionname) {
		int index = functionname.indexOf("(");
		if (index == -1) {
			return functionname;
		} else {
			return functionname.substring(0, functionname.indexOf("("));
		}
	}

	/**
	 * 获取自定义函数的参数
	 * 
	 * @param functionname
	 * @return
	 */
	public static String getFunParams(String functionname) {
		int index = functionname.indexOf("(");
		String param="";
		if (index != -1) {
			String testparam = functionname.substring(functionname.indexOf("(")+1,
					functionname.length() - 1);
			if(StringUtil.isNotEmpty(testparam))
			{
				String[] params=testparam.split(",");
				for (String string : params) {
					param+=(string.indexOf("{")!=-1)?
							("'\"+"+ string.substring(1,string.length()-1) + "+\"',")
							:("'\"+rec."+ string + "+\"',");
				}
			}
		} 
		param+="'\"+index+\"'";//传出行索引号参数
		return param;
	}
	
	public static String getAutoFunction(String field,String entity){
		return null;
	}
}
