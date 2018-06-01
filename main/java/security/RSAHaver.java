package security;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;

import artiface.StructData;

import java.util.Base64;
import java.util.concurrent.LinkedBlockingDeque;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Semaphore;

public class RSAHaver extends IoFilterAdapter{

	   private static class RSAKeys extends LinkedBlockingDeque<Object>{
		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Semaphore a;
		   private Semaphore b;
		   private Thread thread=null;
		   private volatile boolean noStop=true;
		   public RSAKeys(){
			   super();
			   a = new Semaphore(0);
			   b = new Semaphore(100);
			   thread =new Thread( ()->{
				   KeyPairGenerator kpg;
			       try {
					kpg = KeyPairGenerator.getInstance("RSA");
				    kpg.initialize(1024);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					throw new Error("初始化 RSA算法错误");
					
				}
				   while(noStop) {
				
					   add(kpg.generateKeyPair());
				   }
				   
			   }) 
				   
			    ;
			// 这里提前泄露了this指针，所以删掉   thread.start();
		   }
		   public void init() {
			   thread.start();
		   }
		   protected void finalize(){
			   this.noStop = false;
			   thread.interrupt();
		   }
		   
		   
		   @Override
		   public boolean add(Object e) {
               try {
				b.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				return false;
	
			}
			   boolean re = super.add(e);
			   a.release();
			   return re;
		   }
		   @Override
		   public Object poll() {
			   try {
				a.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   Object o = super.poll();
			   b.release();
			   return o;
		   }
		   Thread t;
		   
	   }
	   static RSAKeys createKey = new RSAKeys();
	   private final int level; 
	   
	   public RSAHaver(int level) {
		   this.level=level;
		   createKey.init();
	   }
	   
	   public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
	
		   KeyPair keyPair = (KeyPair)createKey.poll();
		   Key privateKey = keyPair.getPrivate();
		   Key publicKey = keyPair.getPublic();
	       Cipher cipher1 = Cipher.getInstance("RSA");
	       cipher1.init(Cipher.DECRYPT_MODE, privateKey);
	       session.setAttribute("rsaCipher", cipher1);
	       
	       StructData data = new StructData();
	       data.level = this.level;
	       data.obj = publicKey.getEncoded();
	       session.write(data);
	       nextFilter.sessionOpened(session);
	   
	     
	 
	   }
	   @Override
	   public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		   
			StructData data = (StructData)message;
			if(this.level>data.level)return;
		 if(session.getAttribute("desChipher")==null) {
		
			//message is des key then
			
	        if(data.level>this.level)throw new Exception("没有建立安全连接，抛出异常");
		    byte[] deskey = (byte[])data.obj;
		    deskey = ((Cipher)(session.getAttribute("rsaCipher"))).doFinal(deskey);
		       
		    DESKeySpec desKey = new DESKeySpec( deskey );
		    SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance("DES");
		    SecretKey securekey = keyFactory2.generateSecret(desKey);
		    Cipher cipher3 = Cipher.getInstance("DES");
		        //用密匙初始化加密Cipher对象
		    cipher3.init(Cipher.ENCRYPT_MODE, securekey);
		    session.setAttribute("desChipher", cipher3); 
		    Cipher cipher4 = Cipher.getInstance("DES");
		        //用密匙初始化解密Cipher对象
		    cipher4.init(Cipher.DECRYPT_MODE, securekey);
		    session.setAttribute("desChipherde", cipher4);
		
		 }else {
			
			 Cipher cipher = (Cipher) session.getAttribute("desChipherde");
			 byte[] da = (byte[])data.obj;
			 data.obj = cipher.doFinal(da);
			 nextFilter.messageReceived(session,data); 
			
		 }
	   }  
		 public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
			 StructData data = (StructData)writeRequest.getMessage();
			 
			 byte[] msg = (byte[])data.obj;
			 if(this.level>data.level) {nextFilter.filterWrite(session, writeRequest);return; };
			 
			 if(session.getAttribute("desChipher")==null) {
		         nextFilter.filterWrite(session, writeRequest);
			 }else {
				// msg = this.encoder.encode(msg);
				 msg = ((Cipher)(session.getAttribute("desChipher"))).doFinal(msg);
				 data.obj=msg;
				 nextFilter.filterWrite(session,  new DefaultWriteRequest(data,null,writeRequest.getDestination()) );
				
			 }
		 }
}
