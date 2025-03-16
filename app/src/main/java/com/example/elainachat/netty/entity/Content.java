package com.example.elainachat.netty.entity;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Content {
    private ContentType type;
    private Object data;

    public Content(ContentType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
