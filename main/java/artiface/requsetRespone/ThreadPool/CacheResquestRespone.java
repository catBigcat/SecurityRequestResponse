package artiface.requsetRespone.ThreadPool;
import java.util.function.Function;
public class CacheResquestRespone {
	private final int CACHE_SIEZ = 32;
	private final int MARK=31;
	private volatile int max=0;// 准备接受的标记最大的逻辑时间戳
	private int maxIn=0;// 标记最大的逻辑时间戳应该写在哪个位置，这个初始的，准备接受的逻辑时间戳为0，应放在0的缓存处
	
	Object[] obj = new Object[32];
	boolean[] chaoyue =new boolean[32];
	//如果写操作不在当前的能缓存的范围内，则抛出异常
	public class NotInArrange extends Exception{
		
	}
	
	public synchronized void addResult(int id, Object ob) throws NotInArrange {
		if(id>=this.max-MARK&&id<=this.max+MARK) {
			// 可以添加数据，然后现在要添加数据
			if(id == this.max) {
				// 正常情况下
				this.max+=1;
				this.obj[this.maxIn]=ob;
				chaoyue[this.maxIn]=false;
				this.maxIn=(this.maxIn+1)&this.MARK;
			}else {
				if(id<this.max) {
					// 后来的数据执行完毕，但是，数据是超前执行的，所以，仅仅是把数据放进去即可。
					//推断位置,向后偏移量等于 max-id, 实际位置是maxIn-max+id,故加一个32保证不为负 
					this.obj[(this.maxIn-max+id+CACHE_SIEZ)&MARK]=ob;
					chaoyue[(this.maxIn-max+id+CACHE_SIEZ)&MARK]=false;
				}else {
					// 提前写入了新的结果，所以，此时应将结果向后偏移，并且将其余结果设置为null，来说明并没有结果。
					//细想的情况下，这里的id在大的方面没有特别的限制，比较特别大，只是清空了所有操作。，
					for( int i =this.max;i<id;i++) {
						++this.max;
						this.obj[this.maxIn]=null;
						chaoyue[this.maxIn]=true;
						this.maxIn=(this.maxIn+1)&this.MARK;
						
					}
					++this.max;
					this.obj[this.maxIn]=ob;
					chaoyue[this.maxIn]=false;
					this.maxIn=(this.maxIn+1)&this.MARK;
					
				}
			}
		}else throw new NotInArrange();
		
	}
	/**
	 * 
	 * @param id
	 * @param ob
	 * @return null 没有结果，Obj得到了计算结果
	 * @throws NotInArrange
	 */
	// 对一个时间戳只返回一次true
	public synchronized boolean canDo(int step) {
		if(step >= this.max) {
			boolean re = true;
			try {
				this.addResult(step, null);
			} catch (NotInArrange e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return re;
			
		}else {
			if(step>=this.max-32) {
			    boolean re = this.chaoyue[(this.maxIn-max+step+CACHE_SIEZ)&MARK ];
			    this.chaoyue[(this.maxIn-max+step+CACHE_SIEZ)&MARK ]=false;
			    return re;
		    }
		}
		return false;
		
	}
	public synchronized Object getResult(int id) throws NotInArrange {
		if(id<0)return null;
		if(id>=this.max-32&&id<this.max) {
			return this.obj[(this.maxIn-max+id+CACHE_SIEZ)&MARK ];
		}else throw new NotInArrange();
		
	}
    public int getMax() {
    	return this.max;
    }
}
