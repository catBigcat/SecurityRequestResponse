package artiface.requsetRespone;

import java.util.Arrays;

public class Request {
	private static final byte requestMark=0;
	private static final byte responeMark=1;
	private int step ;
	public byte[] getReQuest() {
		return request;
	}

	public int getStep() {
		return step;
	}

	private byte[] request;
	public Request(int step,byte[] request) {
		this.step = step;
		this.request = request;
	}
	public Request(byte[] request) {
	
		this.request = Arrays.copyOf(request, request.length-5);
		this.step =(request[request.length-5]<<24)
				+ (request[request.length-4]<<16)
				+ (request[request.length-3]<<8)
				+ (request[request.length-2]);
	}
	public byte[] tobyte() {
		byte[] re = Arrays.copyOf(request, request.length+5);
		re[re.length-1]=requestMark;
		re[re.length-2]=(byte)(step<<24>>>24);
		re[re.length-3]=(byte)(step<<16>>>24);
		re[re.length-4]=(byte)(step<<8>>>24);
		re[re.length-5]=(byte)(step>>>24);
		return re;
	}
	
	public static boolean isRequest(byte[] by) {
	    if(by[by.length-1]==requestMark)
	        return true;	
	    return false;
	}
	
}
