package com.admiral.telebot.http.data;

import com.admiral.telebot.http.data.common.ChoicesItem;
import com.admiral.telebot.http.data.common.Usage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("object")
    private String object;
    @JsonProperty("created")
    private String created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("usage")
    private Usage usage;

    @JsonProperty("choices")
    private List<ChoicesItem> choices;
    @JsonProperty("finish_reason")
    private String finishReason;
    @JsonProperty("index")
    private Integer index;

    @JsonProperty("error")
    private MessageError error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public List<ChoicesItem> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoicesItem> choices) {
        this.choices = choices;
    }

    public MessageError getError() {
        return error;
    }

    public void setError(MessageError error) {
        this.error = error;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created='" + created + '\'' +
                ", model='" + model + '\'' +
                ", usage=" + usage +
                ", choices=" + choices +
                ", finishReason='" + finishReason + '\'' +
                ", index=" + index +
                ", error=" + error +
                '}';
    }
}
