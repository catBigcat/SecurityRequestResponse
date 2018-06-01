package artiface.compression;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.xerial.snappy.Snappy;

public class Compression extends IoFilterAdapter {
	private final int level;

	public Compression(int level) {
		this.level=level;
	}

}

