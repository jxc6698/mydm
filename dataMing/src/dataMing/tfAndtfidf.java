package dataMing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class tfAndtfidf {
	
	Integer sum = 0 ;
	
	void run()
	{
		;
	}
	
	void clear()
	{
		;
	}
	
	void tf_start()
	{
		sum = 0 ;
	}
	
	void tf_append( Set<String> featureset ,
			Map<String,Integer> tokens[][] ,
			List< List<Double> >[] list ,
			List<Integer> num[] ,
			int cross_class )
	{
		int count[] = new int[ tokens.length] ;
		List<Double> tmplist = null ;
		for( int i=0 ; i < tokens.length ; i ++ )
		{	
			for( int j = 0 ; j < tokens[i].length ; j ++ )
			{
				tmplist = new ArrayList<Double>() ;
				
				Iterator  iter = featureset.iterator() ;
		        while (iter.hasNext()) {
		        	String key = (String)iter.next();
//		        	if( tokens[i][j].containsKey( key ) ){
//		        		( (ArrayList)tmplist).add( new Double( tokens[i][j].get(key) ) );
//		        	}
//		        	else
//		        	{
//		        		( (ArrayList)tmplist).add( 0.0 );
//		        	}
		        	if( tokens[i][j].containsKey( key ) ){
		        		if( tokens[i][j].get( key ) != 0.0 )
		                	( (ArrayList)tmplist).add( 1.0 );
		                else
		                	( (ArrayList)tmplist).add( 0.0 );
		            }
		        	else {
		        		( (ArrayList)tmplist).add( 0.0 );
		            }
		        }

	        	( (ArrayList)list[ count[i] ]).add(tmplist);
	        	num[count[i]].add( i ) ;
	        	
	        	count[i] = ( (count[i] + 1 ) % cross_class ) ;
	        }
		}
		return ;
	}
	
	Map<String, Integer> tf_end( Map<String , Integer> words )
	{
		return null ;
	}
	
	
	/*
	 * not 
	 */
	Map<String, Integer> tf_average( Map<String , Integer> words )
	{
		return null ;
	}
	
	
	
	void tfidf( Set<String> featureset ,
			Map<String,Integer> tokens[][] ,
			Map<String,Double>[] totald ,
			List< List<Double> >[] list ,
			List<Integer> num[] ,
			int cross_class )
	{
//		Map<String,Double> 
		int count[] = new int[ tokens.length] ;
		List<Double> tmplist = null ;
		for( int i=0 ; i < tokens.length ; i ++ )
		{	
			for( int j = 0 ; j < tokens[i].length ; j ++ )
			{
				tmplist = new ArrayList<Double>() ;
				
				Iterator  iter = featureset.iterator() ;
		        while (iter.hasNext()) {
		        	String key = (String)iter.next();
		        	if( tokens[i][j].containsKey( key ) ){
		        		( (ArrayList)tmplist).add( new Double( tokens[i][j].get(key) ) *( Math.log( ( 1.0+tokens[i].length ) / (totald[i].get(key) + 1.0) ) ) );
		        	}
		        	else
		        	{
		        		( (ArrayList)tmplist).add( 0.0 );
		        	}
//		        	if( tokens[i][j].containsKey( key ) ){
//		        		if( tokens[i][j].get( key ) != 0.0 )
//		                	( (ArrayList)tmplist).add( 1.0 );
//		                else
//		                	( (ArrayList)tmplist).add( 0.0 );
//		            }
//		        	else {
//		        		( (ArrayList)tmplist).add( 0.0 );
//		            }
		        }

	        	( (ArrayList)list[ count[i] ]).add(tmplist);
	        	num[count[i]].add( i ) ;
	        	
	        	count[i] = ( (count[i] + 1 ) % cross_class ) ;
	        }
		}
		return ;
	}
	
}
