package artiface.requsetRespone.ThreadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;

import artiface.requsetRespone.Request;
import artiface.requsetRespone.Respone;
import artiface.requsetRespone.ThreadPool.CacheResquestRespone.NotInArrange;

public class ASimpleThreadPool extends ThreadPool{
	private final RequestHandler reHander;
	private final ResponeHandler responeHander;
	private final int RESEND_TIME = 2000; // 这个时间表示请求超时重传的时间
	private final int RESEND_TIMES = 10;// 这个表示超时重传的最多次数
	
	public ASimpleThreadPool( RequestHandler reHander,ResponeHandler responeHander ){
		this.reHander = reHander;
		this.responeHander = responeHander;
	}
	
	// 通过线程池来处理submitTask函数。
	@Override
	public void submitTast(IoSession session, Request re) {
	
		// 处理session
		if(session.getAttribute("cacheRequest")==null) {
			synchronized (session) {
				if(session.getAttribute("cacheRequest")==null)
			        session.setAttribute("cacheRequest",new CacheResquestRespone());
				
			}
		}
		CacheResquestRespone responePool = (CacheResquestRespone)session.getAttribute("cacheRequest");
		if(responePool.canDo(re.getStep())) {
			byte[] obj =this.responeHander.deal(session, re.getReQuest());
			try {
				responePool.addResult(re.getStep(), obj);
			} catch (NotInArrange e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				session.write(new Respone(re.getStep(),obj));
			}
			// 处理并写入结果responePool,然后并发送
		}else
		
		try {
			byte[] respone =null;
			respone = (byte[]) responePool.getResult(re.getStep());
			if(respone==null) {
				// 还没有处理完，等待
			}else {
				session.write(new Respone(re.getStep(),respone));
					
			}
		} catch (NotInArrange e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 提交一个Request，等待收到respone超过一定时间则永远不会收到，所以这里要做的是，超时重传
	ThreadPoolExecutor exe = new ThreadPoolExecutor(10,20,60,TimeUnit.SECONDS,new ArrayBlockingQueue(20));
	@Override
	public void submitRequest(IoSession session, Request req) {
		// TODO Auto-generated method stub
		
		if(session.getAttribute("cacheRespone")==null) {
			synchronized (session) {
				if(session.getAttribute("cacheRespone")==null)
			        session.setAttribute("cacheRespone",new CacheResquestRespone());	
			}
			
		}else {
			// XXX
			CacheResquestRespone cache = (CacheResquestRespone)session.getAttribute("cacheRespone");
			// 超时重传
			exe.execute(()->{
			for(int i =0;i<10;i++) {
				try {
					Thread.sleep(this.RESEND_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					if(cache.getResult(req.getStep())!=null) {
						break;
					}
				} catch (NotInArrange e) {
					// TODO Auto-generated catch block

					if(cache.getMax()>req.getStep()) {
						break;
					}
					e.printStackTrace();
				}
				session.write(req);
			
			};   } );
		}
		session.write(req);
		
	}
	// 当收到了respone之后，考虑怎么处理，之暴露给处理函数一次，处理完就结束
	@Override
	public void receiveRespone(IoSession session, Respone req) {
		// TODO Auto-generated method stub

		CacheResquestRespone cache = (CacheResquestRespone)session.getAttribute("cacheRespone");
		if(cache.canDo(req.getStep())) {
		
			try {
				cache.addResult(req.getStep() , req.getRespone());
			} catch (NotInArrange e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {

			    this.reHander.deal(req.getRespone());
			}
		}
	}
	
}
