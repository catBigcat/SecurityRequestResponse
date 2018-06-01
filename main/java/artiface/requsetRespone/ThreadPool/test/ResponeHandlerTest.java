package artiface.requsetRespone.ThreadPool.test;

import org.apache.mina.core.session.IoSession;

import artiface.requsetRespone.ThreadPool.ResponeHandler;

public class ResponeHandlerTest extends ResponeHandler {
    
	@Override
	public byte[] deal(IoSession session, byte[] re) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "respone12315".getBytes();
	}

}
