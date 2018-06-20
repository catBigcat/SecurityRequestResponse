package artiface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import artiface.coderLayer.SendReveiveCoder;
import artiface.compression.CompressionSnappy;
import artiface.handlers.Handler;
import artiface.keeplive.Receiver;
import artiface.requsetRespone.RequestResponeLevel;
import artiface.requsetRespone.ThreadPool.ASimpleThreadPool;
import artiface.requsetRespone.ThreadPool.test.RequestHandlerTest;
import artiface.requsetRespone.ThreadPool.test.ResponeHandlerTest;
import security.RSAHaver;

public class Server {
	static IoAcceptor acceptor;
	public  static  IoAcceptor instance() throws IOException{
		if(acceptor==null) {
			synchronized(Server.class) {
				if(acceptor==null)
					acceptor=builder();
			}
		}
		return acceptor;
	}
	//bulid
	private static IoAcceptor builder() throws IOException {
		// 这个是设置处理数据到来的IO的线程数
		IoAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors()+1);
		acceptor.getFilterChain().addFirst("codec",  new ProtocolCodecFilter(new SendReveiveCoder()));
		acceptor.getFilterChain().addLast("keepLive", new Receiver(2, 4));
	 	acceptor.getFilterChain().addLast("sercurity",new RSAHaver(3));
	 	acceptor.getFilterChain().addLast("compression",new CompressionSnappy(4));
	 	acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
	 	acceptor.getFilterChain().addLast("requestRespone",new RequestResponeLevel(5, new ASimpleThreadPool(
    			new RequestHandlerTest(),new ResponeHandlerTest())));
		acceptor.setHandler(new Handler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 4);
		acceptor.bind(new InetSocketAddress(9123));
		return acceptor;
	}
	public static void main(String...strings ) throws IOException {
		Server.instance();
	}

}
