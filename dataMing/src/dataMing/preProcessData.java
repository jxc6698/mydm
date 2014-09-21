package dataMing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.*;


public class preProcessData {

	int CROSS_NUM = 10 ;
	
	TokenAnalyzer token = new TokenAnalyzer() ;
	readData readdata = null ;
	Map<String,Integer> tokens[][] = null ;
	List attr = null ;
	
	preProcessData( readData data )
	{
		this.readdata = data ;
	}
	
	void start() 
	{
		tokens = new Map[readdata.line.length][] ;
		int posts = 0 ;    // number of total posts
		
		for( int i=0 ; i < readdata.line.length ; i ++ )
		{
			tokens[i] = new Map[ readdata.line[i].size() ] ;
			posts += readdata.line[i].size() ;
			for( int j = 0 ; j < readdata.line[i].size() ; j ++ )
			{
				tokens[i][j] = new HashMap<String, Integer>() ;
				try {
					tokens[i][j] = token.getTextDef( (String)readdata.line[i].get(j) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// get total 
		Map<String,Double> total = new HashMap<String,Double>() ;
		for( int i=0 ; i < readdata.line.length ; i ++ )
		{	
			for( int j = 0 ; j < readdata.line[i].size() ; j ++ )
			{
				Iterator iter = tokens[i][j].entrySet().iterator() ;
		        while (iter.hasNext()) {
		        	Map.Entry entry = (Map.Entry) iter.next();
		        	Object key = entry.getKey();
		        	if( isNumeric((String)key ) )
		        	{
		        		iter.remove();
		        		continue ;
		        	}
		        	Integer val = (Integer)entry.getValue(); 
		        	if(total.containsKey( (String)key ) ){
		                total.put( (String)key ,total.get( (String)key )+ new Double( val) );
		            }
		        	else {
		                total.put((String)key, new Double( val) );
		            }
		        }
	        }
		}
		int featurenum = 100 ;
		// fillter part attribute
		attr = new ArrayList<Double>() ;
		this.fillter( total , featurenum );
		
		// divide the data into  CROSS_NUM parts 
		List< List<Double> >[] list = new ArrayList[ CROSS_NUM ] ;
		List<Integer> num[] = new ArrayList[ CROSS_NUM ] ;
		for(int i=0;i<CROSS_NUM; i ++)
		{
			list[i] = new ArrayList() ;
			num[i] = new ArrayList<Integer>() ;
		}
		
		
		System.out.println( "total token size : "+ total.size() );
		Iterator iter = total.entrySet().iterator() ;

//		for( int i = 0 ; i < 1000 ; i ++ )
//		{
//			Map.Entry entry = (Map.Entry) iter.next();
//			System.out.println( i + "  "+ entry.getKey()+" "+isNumeric((String)entry.getKey() ) );
//
//		}
		
		// build attr list
		
		int count[] = new int[ this.CROSS_NUM ] ;
		
		List<Double> tmplist = null ;
		for( int i=0 ; i < tokens.length ; i ++ )
		{	
			for( int j = 0 ; j < tokens[i].length ; j ++ )
			{
				tmplist = new ArrayList<Double>() ;
				
				iter = total.entrySet().iterator() ;
		        while (iter.hasNext()) {
		        	Map.Entry entry = (Map.Entry) iter.next();
		        	Object key = entry.getKey();
		        	if( tokens[i][j].containsKey( (String)key ) ){
		        		if( tokens[i][j].get( (String)key ) != 0.0 )
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
		        
	        	count[i] = (( count[i] + 1) % CROSS_NUM ) ;
	        }
		}
	
		NaiveBayes naive = new NaiveBayes() ;
		
		naive.clear();
		for( int i = 0 ;i < CROSS_NUM -1  ; i ++ )
		{		
			naive.train( list[i] , num[i] , readdata.line.length , featurenum );	
			
		}
		System.out.println("------------");
		
		Integer result[] = null ;
		result = naive.estimate( list[CROSS_NUM -1] ,featurenum ) ;
		int accu = 0 ;
		for( int i = 0 ; i < result.length ; i ++ )
		{
			if( result[i] == num[CROSS_NUM -1].get(i) )
				accu ++ ;
		}
		System.out.println( result.length + "  "+ accu ) ;		
	}
	
	void fillter( Map<String,Double> total , int featurenum )
	{
		Iterator iter = total.entrySet().iterator() ;
        while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if( isNumeric((String)entry.getKey() ) )
				iter.remove();
		}
        
        int i = 0 ;
		iter = total.entrySet().iterator() ;
        while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if( i >= featurenum )
				iter.remove() ;
			i ++ ;
		}
        
	}
	
	
	public boolean isNumeric(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() )
		{
			return false;
		}
		return true;
	}
	
	
	
	class TokenAnalyzer{
		
		public void func()
		{
			String text="基于java语言开发的轻量级的中文分词工具包"; 	
			Analyzer analyzer = new IKAnalyzer() ;

			StringReader reader=new StringReader(text);  
			//分词  
			TokenStream ts= analyzer.tokenStream("", reader);
	        CharTermAttribute term=ts.getAttribute(CharTermAttribute.class);  
	        //遍历分词数据  
	        try {
				while(ts.incrementToken()){  
				    System.out.print(term.toString()+"|");  
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	               
	        reader.close();  
	        System.out.println();			
	
			return ;
		}
	
		public Map getTextDef(String text ) throws IOException {
	        Map<String, Integer> wordsFren=new HashMap<String, Integer>();
	        IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
	        Lexeme lexeme;
	        while ((lexeme = ikSegmenter.next()) != null) {
	            if(lexeme.getLexemeText().length()>1){
	                if(wordsFren.containsKey(lexeme.getLexemeText())){
	                    wordsFren.put(lexeme.getLexemeText(),wordsFren.get(lexeme.getLexemeText())+1);
	                }else {
	                    wordsFren.put(lexeme.getLexemeText(),1);
	                }
	            }
	        }
	        
	        Iterator iter = wordsFren.entrySet().iterator();
//	        while (iter.hasNext()) {
//	        	Map.Entry<String , Integer> entry = (Map.Entry<String,Integer>) iter.next();
//	        	String key = (String) entry.getKey();
//	        	Integer val = (Integer)entry.getValue();
//	        }

	        return wordsFren;
	    }
			
		public void showToken( Map<String, Integer> wordsFren )
		{
	        Iterator iter = wordsFren.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Map.Entry entry = (Map.Entry) iter.next();
	        	Object key = entry.getKey();
	        	Object val = entry.getValue();
	        	System.out.printf("  %s   %d \n" , (String)key , (Integer)val) ;
			}
		}
			
	}
}
