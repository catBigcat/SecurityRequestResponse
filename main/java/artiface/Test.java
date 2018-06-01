package artiface;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
        System.out.println("hello");
        
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();
        String hello = "test";
        
        // 传输
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] miwen = cipher.doFinal(hello.getBytes());
   
        
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] mingwen = cipher1.doFinal(miwen);
        //RSA TEST
        System.out.println(new String(mingwen));
        System.out.println(publicKey.toString());
        
        
        //
        long lon = new Random().nextLong();
        
        Cipher cipher2 = Cipher.getInstance("DES");
        SecureRandom random = new SecureRandom();
        
        byte[] deskey = new byte[8];
        random.nextBytes(deskey);
        
        byte[] data="zheshi ceshi ".getBytes();
        DESKeySpec desKey = new DESKeySpec( deskey );
        SecureRandom randoms = new SecureRandom();
        //创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory2 = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory2.generateSecret(desKey);
        //Cipher对象实际完成加密操作
        Cipher cipher3 = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象
        cipher3.init(Cipher.ENCRYPT_MODE, securekey);
        data = cipher3.doFinal(data);
        Cipher cipher4 = Cipher.getInstance("DES");
        //用密匙初始化Cipher对象
        cipher4.init(Cipher.DECRYPT_MODE, securekey);
        data = cipher4.doFinal(data);
        System.out.println(new String(data ));
        
        
        byte[] b = new byte[1];
        b[0]=2;
        byte[] a = Arrays.copyOf(b, 2);
        for(byte bt:a) {
        	System.out.println(bt);
        }
        boolean[] btt=new boolean[1];
        System.out.println(btt[0]);
        String msg = "{'rs':0,'fs':'登录成功!'}";

        JSONObject jsonObject = JSONObject.fromObject(msg);

        System.out.println(jsonObject.get("fs"));
	}

}
