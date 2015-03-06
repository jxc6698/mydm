package nlplab;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import dataMing.globalconfig;
import dataMing.inputClass;
import dataMing.preProcessData;
import dataMing.readData;
import dataMing.run;



/**
 * this lab is for n grame
 * @author jxc
 *
 */
public class nlplab2 {
	public static void main(String argv[])
	{
		globalconfig.nlp_ngram = true ;
//		new nlplab1().prepare( readdata , null );

		globalconfig.whether_cross_check = true ;
		
		String Dir = "lilynew" ;
		
		inputClass inputclass = new inputClass( Dir ) ;
		inputclass.getAllFiles();

		readData readdata = inputclass.readAllFiles() ;
		
		preProcessData preprocessdata = new preProcessData( readdata ) ;
		
		preprocessdata.nbd_start();
		preprocessdata.nbcd_start() ;
		preprocessdata.nbcg_start() ;
		
		
		return ;
	}
	
	
	public void prepare( readData readdata , Map<String,Integer> tokenss[][] )
	{
		TokenAnalyzer token = new TokenAnalyzer() ;
		
		int classnum = readdata.line.length ;
		List<String> tokens[][] = new ArrayList[readdata.line.length][] ;
		int posts = 0 ;    // number of total posts
		
		for( int i=0 ; i < readdata.line.length ; i ++ )
		{
			tokens[i] = new ArrayList[ readdata.line[i].size() ] ;
			posts += readdata.line[i].size() ;
			for( int j = 0 ; j < readdata.line[i].size() ; j ++ )
			{
				tokens[i][j] = new ArrayList<String>() ;
				try {
					tokens[i][j] = token.getTextDefbylist( (String)readdata.line[i].get(j) );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/*  for word to num(index)  */
		Map<String,Integer> keyset = new HashMap<String,Integer>() ;
		Map<Integer,String> strset = new HashMap<Integer,String>() ;
		
		for( int i=0;i<tokens.length;i++){
			for( int j=0;j<tokens[i].length;j++){
				for(int p=0;p<tokens[i][j].size();p++){
					String word = tokens[i][j].get(p) ;
					if( isNumeric(word ) ){
						tokens[i][j].remove(p);
						p -- ;
						continue ;
					}
						
					
					if( keyset.containsKey(word) )
						;
					else {
						keyset.put(word, keyset.size());
						strset.put(strset.size(), word);
					}
				}
			}
		}
		
		List<List> words = new ArrayList();
		List totalset = new ArrayList<Double>() ;
		for(int i=0;i<tokens.length;i++){
			for(int j=0;j<tokens[i].length;j++){
				words.add( tokens[i][j] ) ;
				totalset.add( (double)i);
			}
		}
		
		List<List> dataset[] = new ArrayList[10] ;
		List numset[] = new ArrayList[10] ;
		for(int i=0;i<10;i++)
		{
			dataset[i] = new ArrayList();
			numset[i] = new ArrayList() ;
		}
		divide_fold( words , totalset , dataset , numset , 10 ) ;
				
		/* first word index , second word index to frequency */
		List<Map<Integer,Integer>> wordf = new ArrayList();
		for( int i=0;i<keyset.size() ; i ++ ){
			wordf.add( new HashMap<Integer,Integer>());
		}
		
		Map<String,List> result = new HashMap() ;
		
		for( int i =0;i<10;i++)
//		int i = 0 ;
		{
//			List<List<String>> estdata = new ArrayList() ;
//			List<Double> estnum = new ArrayList() ;
			List<List<String>> traindata = new ArrayList() ;
			List<Double> trainnum = new ArrayList() ;
			
//			cross_fold_devide( dataset , numset , traindata , trainnum ,
//					estdata , estnum , i , 10 ) ;
			for( int j =0;j< 10 ; j ++ )
			{
				for( int k=0;k<numset[j].size();k++){
					if( (double)numset[j].get(k) == i )
					{
						traindata.add(dataset[j].get(k));
						trainnum.add((double)j) ;
					}
					
				}
			}
//			traindata.addAll( (Collection<? extends List<String>>) dataset[i] ) ;
//			trainnum.addAll( numset[i]) ;
			
			List<Double> prob[] = new ArrayList[classnum] ;
			for( int j=0;j<classnum;j++)
				prob[j] = new ArrayList<Double>() ;
			int wordfSize = wordf.size() ;
//			for( int j=0;j< classnum ; j ++ )
//			{
				for( int p = 0 ;p < traindata.size() ;p ++)
				{
//					Double pob = 1.0 ;
			//		if( trainnum.get(p) == j )  
					{
						if( traindata.get(p).size() <2 )
							continue ;
						for( int k=0;k<traindata.get(p).size()-1 ;k++){
							String key1 = traindata.get(p).get(k), key2 = traindata.get(p).get(k+1) ;
							int x = keyset.get(key1) ;
							int y = keyset.get(key2) ;
							if( wordf.get(x).containsKey(y) )
								wordf.get(x).put(y,wordf.get(x).get(y)+1) ;
							else
								wordf.get(x).put(y,1) ;
							
							if( wordf.get(x).containsKey( wordfSize ) )
								wordf.get(x).put( wordfSize ,wordf.get(x).get( wordfSize )+1) ;
							else
								wordf.get(x).put( wordfSize,1) ;
						}
					}
				}
				for( int p =0;p<wordfSize ; p++ )
				{
					
					Iterator iter = wordf.get(p).keySet().iterator() ;
					if( !wordf.get(p).containsKey(wordfSize) )
						continue ;
					double total = wordf.get(p).get(wordfSize) ;
					while(iter.hasNext()){
						int key = (int) iter.next();
						int val = wordf.get(p).get(key);
						if( val == total )
							continue ;
						if( total > 5 && ((double)val)/total > 0.5 ){
							String key1 = strset.get(p);
							String key2 = strset.get(key);
							if( result.containsKey( strset.get(p) ) )
								result.get(key1).add( key2 );
							else {
								List<String> tmp = new ArrayList<String>();
								tmp.add(key2);
								result.put(key1, tmp );
							}
//							System.out.println( key1 +"  "+ key2 );
						}
					}
				}
//			}

		}
		System.out.println("------");
	
		for( int i=0 ; i < readdata.line.length ; i ++ ){
			for( int j = 0 ; j < readdata.line[i].size() ; j ++ ){
				for(int p=0;p<tokens[i][j].size()-1;p++){
					String key1 = tokens[i][j].get(p), key2 = tokens[i][j].get(p+1) ;
					if( result.containsKey(key1) ){
						if( result.get(key1).contains(key2) ){
							tokens[i][j].set(p, key1+key2);
							tokens[i][j].remove(p+1);
						}
					}
				}
			}
		}
		
		
		for( int i=0 ; i < readdata.line.length ; i ++ )
		{
			tokenss[i] = new Map[ readdata.line[i].size() ] ;
			posts += readdata.line[i].size() ;
			for( int j = 0 ; j < readdata.line[i].size() ; j ++ )
			{
				tokenss[i][j] = new HashMap<String, Integer>() ;
				for(int p=0;p<tokens[i][j].size();p++){
					String word = tokens[i][j].get(p) ;
					if( tokenss[i][j].containsKey(word))
						tokenss[i][j].put(word, tokenss[i][j].get(word));
					else
						tokenss[i][j].put(word, 1);
				}
			}
		}
		
	}
	


	void divide_fold(List< List> list ,List num ,
			List< List>[] list1 , List[] num1 , int n )
	{
		for( int i=0;i<list.size() ; i ++ ){
			list1[ i%n ].add( list.get(i) ) ;
			num1[ i%n ].add( Double.parseDouble( num.get(i).toString()) ) ;
		}
		return ;
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
	
	void cross_fold_devide( List< List>[] list1 , List[] num1 ,
			List<List<String>> traindata ,List trainnum ,
			List<List<String>> estdata ,List estnum , int index , int n )
	{
		for( int i=0; i < n-1 ; i++ )
		{
			int j = (index+i)%n ;
			traindata.addAll( (Collection<? extends List<String>>) list1[j]);
			estdata.addAll( num1[j]);
		}
		int j = (index+n-1)%n ;
		estdata.addAll( (Collection<? extends List<String>>)list1[ j ] ) ;
		estnum = num1[ j ] ;
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
		
		public List<String> getTextDefbylist( String text )throws IOException {
			List<String> wordsFren=new ArrayList<String>();
			IKSegmenter ikSegmenter = new IKSegmenter(new StringReader(text), true);
			Lexeme lexeme;
			while ((lexeme = ikSegmenter.next()) != null) {
				if(lexeme.getLexemeText().length()>1){
					wordsFren.add(lexeme.getLexemeText());
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
