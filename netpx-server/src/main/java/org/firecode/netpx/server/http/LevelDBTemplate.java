package org.firecode.netpx.server.http;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.firecode.netpx.common.util.StringUtil;
import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ChiangFire
 */
public class LevelDBTemplate {
	
	private static final Logger LOG = LoggerFactory.getLogger(LevelDBTemplate.class);
	
	private static final String DB_DATA_DIR = System.getProperty("user.dir")+File.separator+"db"+File.separator+"data";
	
	public DB openDB() throws IOException {
		Options options = new Options();
		options.createIfMissing(true);
		// 100MB cache
		options.cacheSize(100 * 1048576); 
		// Not compression
		options.compressionType(CompressionType.NONE);
		return factory.open(new File(DB_DATA_DIR), options);
	}
	
	public String get(String key) {
		return exce((DB db) -> asString(db.get(bytes(key))));
	}
	
	public int put(String key,String value) {
	    return exce((DB db) -> {
	    	db.put(bytes(key),bytes(value));
	    	return 1;
	    });
	}
	
	public List<DBEntity> get(final String key,int limit) {
		return exce((DB db) -> {
			List<DBEntity> res = new ArrayList<>();
			try(DBIterator iterator = db.iterator();) {
				if(StringUtil.isEmpty(key)) {
					iterator.seekToFirst();
				}else {
					iterator.seek(Iq80DBFactory.bytes(key));
				}
			    for(;iterator.hasNext(); iterator.next()) {
			    	DBEntity entity = new DBEntity(asString(iterator.peekNext().getKey()),asString(iterator.peekNext().getValue()));
			    	res.add(entity);
				}
			} catch(Exception e) {
				LOG.error("LevelDB query fail", e);
			}
			return res;
		});
	}
	
	public <R> R exce(Function<DB, R> function) {
		try(DB db = openDB();){
			return function.apply(db);
		} catch (Exception e) {
			LOG.error("Exception in LevelDB", e);
		}
		return null;
	}
	
	public static final class DBEntity {
		
	    private String key;
		
		private String value;
		
		public DBEntity(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
