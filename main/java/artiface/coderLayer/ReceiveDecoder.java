package artiface.coderLayer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import artiface.StructData;

public class ReceiveDecoder extends CumulativeProtocolDecoder{

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		if(in.prefixedDataAvailable(4)) {
			int size = in.getInt();
			int level = in.getInt();
			StructData data= new StructData();
			data.level=level;
			byte[] buffer= new byte[size-4];
			in.get(buffer);
			data.obj=buffer;
			
			out.write(data);
			
			
			return true;
		}else
		    return false;
	}

}
