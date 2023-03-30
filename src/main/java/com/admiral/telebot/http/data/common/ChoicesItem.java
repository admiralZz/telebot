package com.admiral.telebot.http.data.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChoicesItem {

	@JsonProperty("finish_reason")
	private String finishReason;

	@JsonProperty("index")
	private Integer index;

	@JsonProperty("message")
	private Message message;

	public void setFinishReason(String finishReason){
		this.finishReason = finishReason;
	}

	public String getFinishReason(){
		return finishReason;
	}

	public void setIndex(Integer index){
		this.index = index;
	}

	public Integer getIndex(){
		return index;
	}

	public void setMessage(Message message){
		this.message = message;
	}

	public Message getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return
			"ChoicesItem{" +
			"finish_reason = '" + finishReason + '\'' +
			",index = '" + index + '\'' +
			",message = '" + message + '\'' +
			"}";
		}
}