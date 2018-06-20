package artiface.requsetRespone.ThreadPool;

import org.apache.mina.core.session.IoSession;

import artiface.requsetRespone.Request;

public abstract class ResponeHandler {
    public abstract byte[] deal(IoSession session, byte[] re );
}
