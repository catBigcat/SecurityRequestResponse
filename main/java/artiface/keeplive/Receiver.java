package artiface.keeplive;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import artiface.StructData;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * @author Administrator 
 *
 */
public class Receiver extends IoFilterAdapter{
	private final int level;
	private final int count;
	public Receiver(int level , int count ){
		this.level = level;
		this.count=count;
	}
	
    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
    	AtomicInteger at = new AtomicInteger();
    	at.set(this.count);
    	session.setAttribute("keepLive",at);
    	nextFilter.sessionOpened(session);
    }
    
    @Override
    public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        // nothing to do 
    	nextFilter.filterWrite(session, writeRequest);
    }
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
    	AtomicInteger at =(AtomicInteger)session.getAttribute("keepLive");
  
    	at.set(this.count);
    	StructData msg = (StructData)message;
    	if(msg.level<=this.level)return;
    	
    	nextFilter.messageReceived(session, message);
    }
    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
       	AtomicInteger at =(AtomicInteger)session.getAttribute("keepLive");

       	int i = at.decrementAndGet();
       	if(i<=0)session.closeOnFlush();
       	nextFilter.sessionIdle(session, status);
    }
    
}
