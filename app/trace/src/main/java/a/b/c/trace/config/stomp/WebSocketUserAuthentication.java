package a.b.c.trace.config.stomp;

import java.security.Principal;

public  class WebSocketUserAuthentication implements Principal {
    private String name;
    public WebSocketUserAuthentication(String name){
        this.name=name;
    }
    @Override
    public String getName() {
        return name;
    }

    public String toString(){
        return "登录用户="+name;
    }
}