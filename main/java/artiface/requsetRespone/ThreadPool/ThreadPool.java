package artiface.requsetRespone.ThreadPool;

import org.apache.mina.core.session.IoSession;

import artiface.requsetRespone.Request;
import artiface.requsetRespone.Respone;

public abstract class ThreadPool {
	public abstract void submitTast(IoSession session,Request re);
	public abstract void submitRequest(IoSession session ,Request req );
	public abstract void receiveRespone(IoSession session ,Respone req);
	
}
