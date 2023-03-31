package com.admiral.telebot.http.data.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse{

	@JsonProperty("error")
	private Error error;

	public void setError(Error error){
		this.error = error;
	}

	public Error getError(){
		return error;
	}

	@Override
 	public String toString(){
		return 
			"ErrorResponse{" + 
			"error = '" + error + '\'' + 
			"}";
		}
}