package artiface.coderLayer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import artiface.StructData;


public class SendEncoder implements ProtocolEncoder{

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		StructData msg = (StructData)message;
		byte[] ar =(byte[])msg.obj;
		IoBuffer buffer = IoBuffer.allocate(ar.length+8);
		buffer.putInt(ar.length+4);
	    buffer.putInt(msg.level);
		buffer.put(ar);
	    buffer.flip();
	    out.write(buffer);
	  
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
