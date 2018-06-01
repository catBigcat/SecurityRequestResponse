package artiface.requsetRespone;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;

import artiface.StructData;
import artiface.requsetRespone.ThreadPool.ThreadPool;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
public class RequestResponeLevel extends IoFilterAdapter{
	private static final byte OTHER = 2;
	private final int level;
	private final ThreadPool pot;

	public RequestResponeLevel(int level , ThreadPool pot) {
		this.level = level;
		this.pot = pot;
	}
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
    	AtomicInteger ati = new AtomicInteger(0);
    	session.setAttribute("request",ati);
    	nextFilter.sessionOpened(session);
    }
	
	
	@Override
    public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		//
		Object message =writeRequest.getMessage();
		if(message instanceof NewRequest)
		{
	
		    AtomicInteger ati = (AtomicInteger) session.getAttribute("request");
		    Request request = new Request(ati.getAndIncrement(),((NewRequest) message).request);
		    this.pot.submitRequest(session,request);
		    
		}else 
		if(message instanceof Request) {
		    StructData data = new StructData();
		    data.level = this.level;
			data.obj = ((Request)message).tobyte();
			nextFilter.filterWrite(session, new DefaultWriteRequest(data,null,writeRequest.getDestination()));
			System.out.println("send request"+((Request)message).getStep());
		}else {
			if(message instanceof Respone) {
			    StructData data = new StructData();
			    data.level = this.level;
				data.obj = ((Respone)message).tobyte();
				nextFilter.filterWrite(session, new DefaultWriteRequest(data,null,writeRequest.getDestination()));
			}else {
				 StructData data =  (StructData)message;
				 if(this.level>data.level)nextFilter.filterWrite(session, writeRequest);
				 else {
					 byte[] bt = (byte[])data.obj;
					 bt = Arrays.copyOf( bt , bt.length+1);
					 bt[bt.length-1]=OTHER;
					 data.obj=bt;
					 nextFilter.filterWrite(session, new DefaultWriteRequest(data,null,writeRequest.getDestination()));
				 }
			}
		}
    }
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
    	StructData msg = (StructData)message;
    	if(msg.level<this.level)return;
    	byte[] data = (byte[]) msg.obj;
    	if(Request.isRequest(data)) {
    		
    		Request request = new Request(data);
    		System.out.println("receive id"+request.getStep());
    		this.pot.submitTast(session ,request);
    	}else if(Respone.isRespone(data)){
    		Respone request = new Respone(data);
    		System.out.println("respone id"+request.getStep());
    		this.pot.receiveRespone(session ,request);
    	}else{
    		data = Arrays.copyOf(data, data.length-1);
    		msg.obj=data;
    	 	nextFilter.messageReceived(session, msg);
    	}
    	
    }
}
