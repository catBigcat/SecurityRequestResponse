package artiface;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import artiface.coderLayer.SendReveiveCoder;
import artiface.compression.CompressionSnappy;
import artiface.handlers.Handler;
import artiface.keeplive.Receiver;
import artiface.keeplive.Sender;
import artiface.requsetRespone.NewRequest;
import artiface.requsetRespone.RequestResponeLevel;
import artiface.requsetRespone.ThreadPool.ASimpleThreadPool;
import artiface.requsetRespone.ThreadPool.test.RequestHandlerTest;
import artiface.requsetRespone.ThreadPool.test.ResponeHandlerTest;
import security.EDSHaver;
import security.RSAHaver;

public class Client {
	private static InetSocketAddress address = new InetSocketAddress("localhost", 9123); 
    public static void main(String...strings) throws InterruptedException {
    	SocketConnector  connector = new NioSocketConnector();
    	connector.getFilterChain().addFirst("codec",  new ProtocolCodecFilter(new SendReveiveCoder()));
    	connector.getFilterChain().addLast("keepLive", new Sender(2));
    	connector.getFilterChain().addLast("sercurity",new EDSHaver(3));
    	connector.getFilterChain().addLast("compression",new CompressionSnappy(4));
    	connector.getFilterChain().addLast("requestRespone",new RequestResponeLevel(5, new ASimpleThreadPool(
    			new RequestHandlerTest(),new ResponeHandlerTest()
    			)));
    	connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 4);
    	connector.setHandler(new Handler());
    	ConnectFuture future =connector.connect(address);
    	future.awaitUninterruptibly();
    	Thread.sleep(3000);
    	IoSession session=future.getSession();
    	StructData data = new StructData();
    	data.level=100;
    	data.obj="hello".getBytes();
    	
    	session.write(data);
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	session.write(new NewRequest("hello".getBytes()));
    	
    }
}
