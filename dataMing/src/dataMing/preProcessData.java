package dataMing;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.*;


public class preProcessData {

	TokenAnalyzer token = new TokenAnalyzer() ;
	
	void start() 
	{
		try {
			new TokenAnalyzer().getTextDef( "" ) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
		public Map getTextDef(String text) throws IOException {
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
