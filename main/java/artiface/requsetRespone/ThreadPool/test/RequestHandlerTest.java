package artiface.requsetRespone.ThreadPool.test;

import artiface.requsetRespone.ThreadPool.RequestHandler;

public class RequestHandlerTest extends RequestHandler{

	@Override
	public void deal(byte[] data) {
		// TODO Auto-generated method stub
		System.out.println(new String(data));
	}

}
