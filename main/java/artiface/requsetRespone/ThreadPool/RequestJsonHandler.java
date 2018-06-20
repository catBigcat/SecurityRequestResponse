package artiface.requsetRespone.ThreadPool;

import net.sf.json.JSONObject;

public abstract class RequestJsonHandler extends RequestHandler{

	@Override
	public void deal(byte[] data) {
		JSONObject jsonObject = JSONObject.fromObject(new String(data));
        this.deal(jsonObject);
	}
	public abstract JSONObject deal(JSONObject json);

}
