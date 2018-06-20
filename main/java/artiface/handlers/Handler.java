package artiface.handlers;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import artiface.StructData;

public class Handler extends IoHandlerAdapter{
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    	StructData data = (StructData)message;
        byte[] ar =(byte[]) data.obj;
    	String s = new String(ar);
    	System.out.println(s);
    }
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // Empty handler
    	super.sessionClosed(session);
    	System.out.println("关闭连接");
    }
}
