package artiface.keeplive;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import artiface.StructData;

public class Sender extends IoFilterAdapter {
	private final int level;
	public  Sender(int level) {
		this.level = level;
	}
    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
    	StructData data=new StructData();
    	data.level = this.level;
    	data.obj = new byte[1];
    	session.write(data);
       	nextFilter.sessionIdle(session, status);
    }
}
