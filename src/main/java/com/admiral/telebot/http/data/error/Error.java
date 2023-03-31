package com.admiral.telebot.http.data.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error{

	@JsonProperty("code")
	private String code;

	@JsonProperty("param")
	private Object param;

	@JsonProperty("message")
	private String message;

	@JsonProperty("type")
	private String type;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setParam(Object param){
		this.param = param;
	}

	public Object getParam(){
		return param;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"Error{" + 
			"code = '" + code + '\'' + 
			",param = '" + param + '\'' + 
			",message = '" + message + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}