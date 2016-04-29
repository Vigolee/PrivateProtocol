package model;

/**
 * 承载业务消息、心跳消息、握手请求和应答等消息
 * Created by Vigo on 16/4/21.
 */
public final class Message {
    /** 消息头*/
    private Header header;

    /** 消息体*/
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message [header=" + header + ",body=" + body + "]";
    }
}
