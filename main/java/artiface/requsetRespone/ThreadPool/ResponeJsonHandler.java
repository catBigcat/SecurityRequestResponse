package artiface.requsetRespone.ThreadPool;

import org.apache.mina.core.session.IoSession;

import net.sf.json.JSONObject;

public abstract class ResponeJsonHandler extends ResponeHandler{
	@Override
	public byte[] deal(IoSession session, byte[] re) {
		return this.deal(session,JSONObject.fromObject(new String(re)) ).toString().getBytes();
	}
    public abstract JSONObject deal(IoSession session, JSONObject re);
}
