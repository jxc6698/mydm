package dataMing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
	
	int classnum = 0 ;
	
	
	preProcessData( readData data )
	{
		this.readdata = data ;
	}
	
	void start() 
	{
		this.classnum = readdata.line.length ;
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
		Map<String,Double>[] total = new HashMap[ classnum ] ;
		for( int i=0 ; i < classnum ; i ++)
			total[i] = new HashMap<String,Double>() ;
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
		        	if(total[i].containsKey( (String)key ) ){
		                total[i].put( (String)key ,total[i].get( (String)key )+ new Double( val) );
		            }
		        	else {
		                total[i].put((String)key, new Double( val) );
		            }
		        }
	        }
		}
		int featurenum = 0 ;
		// fillter part attribute
		// build attr list
		attr = new ArrayList<Double>() ;
		
		Set featureset = new HashSet<String>() ;
		featurenum = this.fillter( total , featurenum , featureset ,readdata.stopwords );
		
		// divide the data into  CROSS_NUM parts 
		List< List<Double> >[] list = new ArrayList[ CROSS_NUM ] ;
		List<Integer> num[] = new ArrayList[ CROSS_NUM ] ;
		for(int i=0;i<CROSS_NUM; i ++)
		{
			list[i] = new ArrayList() ;
			num[i] = new ArrayList<Integer>() ;
		}
		
		
		tfAndtfidf tfidf = new tfAndtfidf() ;
		tfidf.tf_start();
		tfidf.tf_append( featureset , tokens , list , num , this.CROSS_NUM ) ; 
		
		NaiveBayes naive = new NaiveBayes() ;
		
		for( int j = 0 ; j < CROSS_NUM ; j ++ )
//		int j=0;
		{
			naive.clear();
			for( int i = 0 ;i < CROSS_NUM -1  ; i ++ )
			{		
				naive.train( list[(j+i)%CROSS_NUM] , num[(j+i)%CROSS_NUM] , readdata.line.length , featurenum );			
			}
			System.out.println("------------");
			
			Integer result[] = null ;
			result = naive.estimate( list[(j+CROSS_NUM -1)%CROSS_NUM] ,featurenum ) ;
			int accu = 0 ;
			for( int i = 0 ; i < result.length ; i ++ )
			{
//				System.out.println(result[i] + "  " + num[(j+CROSS_NUM -1)%CROSS_NUM].get(i) );
				if( result[i] == num[(j+CROSS_NUM -1)%CROSS_NUM].get(i) )
				{
					accu ++ ;
				}
			}
			System.out.println( result.length + "  "+ accu ) ;		
		}
	}
	
	int fillter( Map<String,Double>[] total ,
			int featurenum , Set<String> list ,
			Set<String> stopwords )
	{
		Iterator iter = null ;
		for( int i = 0 ; i < total.length ; i ++ )
		{
			iter = total[i].entrySet().iterator() ;
	        while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if( isNumeric((String)entry.getKey() ) )
					iter.remove();
				else
					list.add( (String)entry.getKey() ) ;
			}
		}

		iter = stopwords.iterator() ;
		while( iter.hasNext() )
		{
			String item = (String)iter.next() ;
			if( list.contains( item ) )
				list.remove( item ) ;
		}
		
		iter = list.iterator() ;
        while (iter.hasNext()) {
			String item = (String) iter.next();
			int tmp1 = 0 , tmp2 , mark ;
			mark = 0 ;
			if( total[0].get(item) == null )
				tmp1 = -1 ;
			else
				tmp1 = 1 ;
			for( int i = 1 ; i < total.length ; i ++ )
			{
				if( total[i].get(item) == null )
					tmp2 = -1 ;
				else
					tmp2 = 1 ;
				tmp1 += tmp2 ;
			}
			if( tmp1 >5 || tmp1 < -7 )
			{
				//iter.remove();
			}
			else
				iter.remove();
        }
        
        System.out.println( list.size() );
        return list.size() ;

        
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
		
		
		/*
		 * this function is just for test use
		 */
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
