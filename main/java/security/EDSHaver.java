package security;

import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;

import artiface.StructData;

public class EDSHaver extends IoFilterAdapter {
	   private final int level; 
	   public EDSHaver(int level) {
		   this.level=level;
	   }

	 public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		 if(session.getAttribute("desChipherde")==null) {
			 // this message is RSA key so 
			 
		        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		        StructData data = (StructData)message;
		        if(data.level<this.level)return;//这个是底层的数据，不用管
		        if(data.level>this.level)throw new Exception("没有建立安全连接，抛出异常");
		  
		        byte[] publicKey = (byte[])data.obj;
		        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
		        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
			    
		        Cipher cipher = Cipher.getInstance("RSA");
		        cipher.init(Cipher.ENCRYPT_MODE,key);
		    
		        SecureRandom random = new SecureRandom();
		        byte[] deskey = new byte[8];
		        random.nextBytes(deskey);
		        
		        
		        DESKeySpec desKey = new DESKeySpec( deskey );
		        SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance("DES");
		        SecretKey securekey = keyFactory2.generateSecret(desKey);
		        Cipher cipher3 = Cipher.getInstance("DES");
		        //用密匙初始化Cipher对象
		        cipher3.init(Cipher.ENCRYPT_MODE, securekey);
		        session.setAttribute("desChipher", cipher3);
		        
		        Cipher cipher4 = Cipher.getInstance("DES");
		        //用密匙初始化Cipher对象
		        cipher4.init(Cipher.DECRYPT_MODE, securekey);
		        session.setAttribute("desChipherde", cipher4);
		        deskey = cipher.doFinal(deskey);
		        
		        StructData date = new StructData();
		        data.level=this.level;
		        data.obj = deskey;
		        session.write(data);
		     
		       // byte[] miwen = cipher.doFinal(hello.getBytes());
		 }else {
			 
			 StructData data = (StructData)message;
			 if(this.level>data.level)return;
			 
			 byte[] msg = (byte[])data.obj;
			 msg = ((Cipher)(session.getAttribute("desChipherde"))).doFinal(msg);
			// msg = this.decoder.decode(msg);
			 data.obj=msg;
			 nextFilter.messageReceived(session, data);
		 }
	
	 }
	 public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
		 
		 
		 StructData data = (StructData)writeRequest.getMessage();
		 if(data.level<=this.level) {
			 nextFilter.filterWrite(session,writeRequest);return;
		 }
		 byte[] msg = (byte[])data.obj;
		 if(session.getAttribute("desChipher")==null) {
	         nextFilter.filterWrite(session, writeRequest);
		 }else {
		
			 try {
				// msg = this.encoder.encode(msg);
			     msg = ((Cipher)(session.getAttribute("desChipher"))).doFinal(msg);
			 }catch(Exception e) {e.printStackTrace(); }data.obj = msg;
			 nextFilter.filterWrite(session,  new DefaultWriteRequest(data,null,writeRequest.getDestination()) );
		   
		 }       
	} 
}
