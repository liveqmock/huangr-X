package com.easysoft.core.dispatcher.core;
import com.easysoft.core.dispatcher.core.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * @author andy
 * @version 1.0
 */
public class StringResponse implements Response {
    private String content;
    private String contentType;
    private String statusCode;
	public StringResponse(){
		contentType= ContextType.HTML;
	}

	public void finalize() throws Throwable {

	}

	public String getContent(){
		
		return content;
	}

	public String getStatusCode(){
		return statusCode;
	}

	public String getContentType(){
		return this.contentType;
	}
  
	/**
	 * 
	 * @param content
	 */
	public void setContent(String content){
		this.content = content;
	}

	/**
	 * 
	 * @param code
	 */
	public void setStatusCode(String code){
		this.statusCode = code;
	}

	/**
	 * 
	 * @param contentType
	 */
	public void setContentType(String contentType){
		this.contentType = contentType;
	}
	
	
	public InputStream getInputStream() {
	 
		try {
			InputStream  in = new   ByteArrayInputStream(this.content.getBytes("UTF-8"));
			return  in;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

}