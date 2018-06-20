package artiface.requsetRespone;

import java.util.Arrays;

public class Respone {
	private static final byte requestMark=0;
	private static final byte responeMark=1;
	int step;
	byte[] respone;
	
	public int getStep() {
		return step;
	}
	public byte[] getRespone() {
		return respone;
	}
	public Respone(int step,byte[] respone) {
	   this.step=step;
	   this.respone=respone;
	}
	public Respone(byte[] respone) {
		this.respone = Arrays.copyOf(respone, respone.length-5);
		this.step =(respone[respone.length-5]<<24)
				+ (respone[respone.length-4]<<16)
				+ (respone[respone.length-3]<<8)
				+ (respone[respone.length-2]);
	}
	public byte[] tobyte() {
		byte[] re = Arrays.copyOf(respone, respone.length+5);
		re[re.length-1]=responeMark;
		re[re.length-2]=(byte)(step<<24>>>24);
		re[re.length-3]=(byte)(step<<16>>>24);
		re[re.length-4]=(byte)(step<<8>>>24);
		re[re.length-5]=(byte)(step>>>24);
		return re;
	}
	public static boolean isRespone(byte[] by) {
	    if(by[by.length-1]==responeMark)
	        return true;	
	    return false;
	}
	
}
