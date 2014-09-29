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

	final boolean debug = false ;
	
	
	int CROSS_NUM = 10 ;
	
	public TokenAnalyzer token = new TokenAnalyzer() ;
	readData readdata = null ;
	Map<String,Integer> tokens[][] = null ;
	Map<String,Double>[] total = null ;
	Map<String,Double>[] totald = null ;
	
	int classnum = 0 ;
	
	double accuracy[] = new double[ CROSS_NUM ];
	
	
	public preProcessData( readData data )
	{
		this.readdata = data ;
	}
	
	public void getpreparedata( predata pred )
	{
		pred.tokens = this.tokens ;
		pred.total = total ;
		pred.totald = totald ;
		pred.stopwords = readdata.stopwords ;
	}
	
	public void prepare()
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
		total = new HashMap[ classnum ] ;
		totald = new HashMap[ classnum ] ;
		for( int i=0 ; i < classnum ; i ++)
		{
			total[i] = new HashMap<String,Double>() ;
			totald[i] = new HashMap<String,Double>() ;
		}
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
		        	// total  tf 
		        	Integer val = (Integer)entry.getValue(); 
		        	if(total[i].containsKey( (String)key ) ){
		                total[i].put( (String)key ,total[i].get( (String)key )+ new Double( val) );
		            }
		        	else {
		                total[i].put((String)key, new Double( val) );
		            }
		        	// word in appear times in t
		        	if(totald[i].containsKey( (String)key ) ){
		                totald[i].put( (String)key ,totald[i].get( (String)key )+ 1.0 );
		            }
		        	else {
		                totald[i].put((String)key, new Double( 1) );
		            }
		        }
	        }
		}
	}
	
	void nbd_start() 
	{
		this.prepare();
		int featurenum = 0 ;
		// fillter part attribute
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
		{
			naive.clear();
			for( int i = 0 ;i < CROSS_NUM -1  ; i ++ )
			{		
				naive.train( list[(j+i)%CROSS_NUM] , num[(j+i)%CROSS_NUM] , readdata.line.length , featurenum );			
			}
			if(debug)	System.out.println("------------");
			
			Integer result[] = null ;
			result = naive.estimate( list[(j+CROSS_NUM -1)%CROSS_NUM] ,featurenum ) ;
			int accu = 0 ;
			for( int i = 0 ; i < result.length ; i ++ )
			{
				if( result[i] == num[(j+CROSS_NUM -1)%CROSS_NUM].get(i) )
				{
					accu ++ ;
				}
			}
			accuracy[j] = ((double)accu ) / result.length ; 
			if(debug) System.out.println( result.length + "  "+ accu ) ;		
		}
		this.getresultmeanvariance();
	}
	
	public int fillter( Map<String,Double>[] totalm ,
			int featurenum , Set<String> list ,
			Set<String> stopwords )
	{
		Iterator iter = null ;
		for( int i = 0 ; i < totalm.length ; i ++ )
		{
			iter = totalm[i].entrySet().iterator() ;
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
			if( totalm[0].get(item) == null )
				tmp1 = -1 ;
			else
				tmp1 = 1 ;
			for( int i = 1 ; i < totalm.length ; i ++ )
			{
				if( totalm[i].get(item) == null )
					tmp2 = -1 ;
				else
					tmp2 = 1 ;
				tmp1 += tmp2 ;
			}
//			if( tmp1 >7 || tmp1 < -7 )  // 17329
//			if( tmp1 >8 || tmp1 < -8 )  // 71
//			if( tmp1 >7 || tmp1 < -8  )	// 206
			if( tmp1 <5  && tmp1 > -5 )
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
	
	
	
	
	
	void nbcd_start()
	{
		this.prepare();
		
		int featurenum = 0 ;
		// fillter part attribute
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
		tfidf.tfidf( featureset , tokens , total , list , num , this.CROSS_NUM ) ; 
		
		NaiveBayes naive = new NaiveBayes() ;
		for( int j = 0 ; j < CROSS_NUM ; j ++ )
			{
				naive.clear();
				for( int i = 0 ;i < CROSS_NUM -1  ; i ++ )
				{		
					naive.train( list[(j+i)%CROSS_NUM] , num[(j+i)%CROSS_NUM] , readdata.line.length , featurenum );			
				}
				if(debug) System.out.println("------------");
				
				Integer result[] = null ;
				result = naive.estimate( list[(j+CROSS_NUM -1)%CROSS_NUM] ,featurenum ) ;
				int accu = 0 ;
				for( int i = 0 ; i < result.length ; i ++ )
				{
					if( result[i] == num[(j+CROSS_NUM -1)%CROSS_NUM].get(i) )
					{
						accu ++ ;
					}
				}
				accuracy[j] = ((double)accu ) / result.length ; 
				if(debug) System.out.println( result.length + "  "+ accu ) ;		
			}
		this.getresultmeanvariance();
	}
	
	void nbcg_start()
	{
		this.prepare();
		int featurenum = 0 ;
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
		tfidf.tfidf( featureset , tokens , totald , list , num , this.CROSS_NUM ) ; 
		
		NaiveBayes naive = new NaiveBayes() ;
		for( int j = 0 ; j < CROSS_NUM ; j ++ )
			{
				naive.gclear();
				for( int i = 0 ;i < CROSS_NUM -1  ; i ++ )
				{		
					naive.gtrain( list[(j+i)%CROSS_NUM] , num[(j+i)%CROSS_NUM] , readdata.line.length , featurenum );			
				}
				naive.gendtrain();
				if(debug) System.out.println("------------");
				
				Integer result[] = null ;
				result = naive.gestimate( list[(j+CROSS_NUM -1)%CROSS_NUM] ,featurenum ) ;

				int accu = 0 ;
				for( int i = 0 ; i < result.length ; i ++ )
				{
					if( result[i] == num[(j+CROSS_NUM -1)%CROSS_NUM].get(i) )
					{
						accu ++ ;
					}
				}
				accuracy[j] = ((double)accu ) / result.length ; 
				if(debug) System.out.println( result.length + "  "+ accu ) ;		
			}
		this.getresultmeanvariance();
	}
	
	
	void getresultmeanvariance()
	{
		Double mean = new Double(0) ;
		Double variance = new Double(0) ;
		for( int i = 0 ; i< accuracy.length ; i ++ )
		{
			mean += accuracy[i] ;
			variance += accuracy[i] * accuracy[i] ;
		}
		mean /= accuracy.length ;
		variance /= accuracy.length ;
		System.out.println("mean " + mean );
		System.out.println("variance " + variance ) ;
	}
	
	class TokenAnalyzer{

		/*
		 * return Map class of tokens of input text String
		 */
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
