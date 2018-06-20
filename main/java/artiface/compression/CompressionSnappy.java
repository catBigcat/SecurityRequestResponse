package artiface.compression;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.xerial.snappy.Snappy;

import artiface.StructData;

public class CompressionSnappy extends IoFilterAdapter{
	private final int level;
	public CompressionSnappy(int level) {
		this.level=level;
	}
    public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        StructData msg = (StructData)writeRequest.getMessage();
        if(msg.level<this.level)
            nextFilter.filterWrite(session, writeRequest);
        else {
        	byte[] data = (byte[])msg.obj;
        	msg.obj = Snappy.compress(data);
        	nextFilter.filterWrite(session,new DefaultWriteRequest(msg,null,writeRequest.getDestination()) );
        }
    }
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
    	StructData msg = (StructData)message;
    	if(msg.level<this.level)return;
    	msg.obj = Snappy.uncompress( (byte[])msg.obj);
    	nextFilter.messageReceived(session, message);
    }
    
}
