package dataMing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class tfAndtfidf {
	
	Map<String , Integer> total = null ;
	
	void run()
	{
		;
	}
	
	void clear()
	{
		total = null ;
	}
	
	void tf_start()
	{
		total = new HashMap<String , Integer>() ;
	}
	
	Map<String, Integer> tf_append( Map<String , Integer> words )
	{
		Iterator iter = words.entrySet().iterator() ;
        while (iter.hasNext()) {
        	Map.Entry entry = (Map.Entry) iter.next();
        	Object key = entry.getKey();
        	Object val = entry.getValue(); 
        	if(total.containsKey( (String)key ) ){
                total.put( (String)key ,words.get( (String)key )+ (Integer)val );
            }
        	else {
                total.put((String)key, (Integer)val );
            }
		}
		return total;
	}
	
	Map<String, Integer> tf_end( Map<String , Integer> words )
	{
		return this.total ;
	}
	
}