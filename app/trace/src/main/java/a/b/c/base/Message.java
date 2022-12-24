package a.b.c.base;

public class Message extends RuntimeException{

    public Message(String message, Exception ex){
        super(message,ex);
    }

    public Message(String message){
        super(message);
    }
}
