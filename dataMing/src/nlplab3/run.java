package nlplab3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.DTNB;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class run {

	public static void main(String argv[])
	{
		System.out.println(new Date());
		run worker = new run();
		worker.start();

		System.out.println(new Date());
		return;
	}
	
	int totalfeaturenum = 100;
	
	void start()
	{
		_input input = new _input();
		List<List<String>>[] words = new ArrayList[2];
		List<Double>[] tag = new ArrayList[2];
		List<List<String>> est = new ArrayList();
		words[0] = new ArrayList(); words[1] = new ArrayList();
		tag[0] = new ArrayList(); tag[1] = new ArrayList();
		List<List<String>> estwords = new ArrayList();
		
		input.read(words, tag, est, estwords);
		
		Map<String,Integer> dic = new HashMap<String,Integer>();
		List<String> resdic = new ArrayList();
		List<String> featurelist = new ArrayList();
		getfeaturelist(dic, resdic, featurelist, words);
		
	//	formfeature( words, wordsf);
		System.out.println("pre-process data over");
		run_weka_nb( featurelist, words, estwords);

	}
	
	void run_weka_nb( List<String> featurelist, List<List<String>>[] words,
			List<List<String>> estwords) {
		Map<String,Integer> dicfea = new HashMap();
		int featurenum = featurelist.size();
		System.out.println("featurenum  "+featurenum);
		for(int i=0;i<featurenum;i++) {
			dicfea.put(featurelist.get(i), dicfea.size());
		}
		FastVector fvw = new FastVector(featurenum+1);
		Attribute at[] = new Attribute[featurenum];
		for(int i=0;i<featurenum;i++) {
			at[i] = new Attribute("f"+i);
			fvw.addElement(at[i]);
		}
		FastVector fres = new FastVector(2);
		fres.addElement("+1");
		fres.addElement("-1");
		Attribute classnum = new Attribute("theClass", fres);
		fvw.addElement(classnum);
		
		Instances isTrainingSet = new Instances("Rel", fvw , 12000);
		Instances isEstSet = new Instances("Rel", fvw , 5000);
		isTrainingSet.setClassIndex(featurelist.size()); /* count from 0 */
		isEstSet.setClassIndex(featurelist.size()); /* count from 0 */	
		
		List<Instance> iExamplen = new ArrayList();
		for( int i=0;i<words[0].size();i++) {
			Instance tempd= new Instance(featurenum+1);
			
			for(int j=0;j<featurenum;j++) {
				tempd.setValue((Attribute)fvw.elementAt(j), 0);
			}
			for(int j=0;j<words[0].get(i).size();j++) {
				Integer tmp = null;
				tmp = dicfea.get(words[0].get(i).get(j));
				if( tmp != null)
					tempd.setValue((Attribute)fvw.elementAt(tmp), 1);
			}
			tempd.setValue((Attribute)fvw.elementAt(featurenum), "-1");
			iExamplen.add(tempd);
		}
		List<Instance> iExamplep = new ArrayList();
		for( int i=0;i<words[1].size();i++) {
			Instance tempd= new Instance(featurenum+1);
			for(int j=0;j<featurenum;j++)
				tempd.setValue((Attribute)fvw.elementAt(j), 0);
			for(int j=0;j<words[1].get(i).size();j++) {
				Integer tmp = null;
				tmp = dicfea.get(words[1].get(i).get(j));
				if( tmp != null)
					tempd.setValue((Attribute)fvw.elementAt(tmp), 1);
			}
			tempd.setValue((Attribute)fvw.elementAt(featurenum), "+1");
			iExamplep.add(tempd);
		}
		

		for(int i=0;i<iExamplen.size();i++)
			isTrainingSet.add(iExamplen.get(i));
		for(int i=0;i<iExamplep.size();i++)
			isTrainingSet.add(iExamplep.get(i));
		iExamplen = iExamplep = null;
		
		for( int i=0;i<estwords.size();i++) {
			Instance tempd= new Instance(featurenum+1);
			for(int j=0;j<featurenum;j++)
				tempd.setValue((Attribute)fvw.elementAt(j), 0);
			for(int j=0;j<estwords.get(i).size();j++) {
				Integer tmp = null;
				tmp = dicfea.get(estwords.get(i).get(j));
				if( tmp != null)
					tempd.setValue((Attribute)fvw.elementAt(tmp), 1);
			}
			tempd.setValue((Attribute)fvw.elementAt(featurenum), "+1");
			isEstSet.add(tempd);
		}
		
		
		Classifier cModel = (Classifier)new DTNB();
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double result[] = null;
		
		/* estimate with train data */
		System.out.println("estimate with train data");
		Evaluation eTest = null;

		try {
			eTest = new Evaluation(isTrainingSet);
			eTest.evaluateModel(cModel, isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
//		double[][] cmMatrix = eTest.confusionMatrix();
//		for(int row_i=0; row_i<cmMatrix.length; row_i++) {
//			for(int col_i=0; col_i < cmMatrix.length; col_i++) {
//				System.out.print(cmMatrix[row_i][col_i]);
//				System.out.print("|");
//			}
//			System.out.println();
//		}
		
		/* estimate with estimate data */
		System.out.println("estimate with estimate data");
		eTest = null;
		try {
			eTest = new Evaluation(isEstSet);
			result = eTest.evaluateModel(cModel, isEstSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		
		System.out.println(result.length);
		for(int i=0;i<result.length;i++) {
			System.out.println(result[i]);
		}
	}
	
	void getfeaturelist(Map<String,Integer> dic, List<String> resdic, List<String> featurelist,
			List<List<String>>[] words)
	{
		for(int k=0;k<words.length;k++) {
			for(int i=0;i<words[k].size();i++) {
				List<String> tip=words[k].get(i);
				for(int j=0;j<tip.size();j++) {
					String word = tip.get(j);
					if(dic.get(word) == null) {
						dic.put(word, dic.size());
						resdic.add(word);
					}
				}
			}
		}
		int count_p[] = new int[dic.size()];
		int count_n[] = new int[dic.size()];
		for(int i=0;i<words[0].size();i++) {
			List<String> tip=words[0].get(i);
			for(int j=0;j<tip.size();j++) {
				count_n[dic.get(tip.get(j))]++;
			}
		}
		for(int i=0;i<words[1].size();i++) {
			List<String> tip=words[1].get(i);
			for(int j=0;j<tip.size();j++) {
				count_p[dic.get(tip.get(j))]++;
			}
		}
		
		ArrayList<datacmp> tmp = new ArrayList<datacmp>() ;
		
		double delta = 0.001;
		for(int i=0;i<dic.size();i++) {
			if( count_p[i] < 100 && count_n[i] < 100 )
				continue;
			if( count_p[i] > count_n[i])
				tmp.add( new datacmp( i, ((double)count_p[i]-count_n[i] ) /(count_p[i]+count_n[i])));
			else
				tmp.add( new datacmp( i, ((double)count_n[i]-count_p[i] ) /(count_p[i]+count_n[i])));
		}
		Collections.sort( tmp , comparator );
		for(int i=0;i< this.totalfeaturenum ;i++) {
			int index = tmp.get(i).id;
			featurelist.add( resdic.get(index) );
//			System.out.println(tmp.get(i).data +"  "+resdic.get(index) + "  "
//					+ count_p[index]+"  "+count_n[index]);
			System.out.println( resdic.get(index) );

		}
		return;
	}
	
	class datacmp{
		int id ;
		double data ;
		public datacmp( int id , double data )
		{
			this.id = id ;
			this.data = data ;
		}
	}
	
	Comparator<datacmp> comparator = new Comparator<datacmp>(){  
		public int compare(datacmp s1, datacmp s2) {
			if(s1.data>s2.data)
				return -1 ;
			else if( s1.data == s2.data )
				return 0 ;
			else
				return 1 ;
			}  
	};
	
	class _input{

//		public void read(Map<String,Integer> dic, 
//				List<Map<InteVirtualized Dataceger,Integer>> words,
//				List<Double> tag,
//				List<Map<Integer,Integer>> est)
		public void read(List<List<String>>[] words,
				List<Double>[] tag,
				List<List<String>> est,
				List<List<String>> estwords)
		{
			TokenAnalyzer token = new TokenAnalyzer();
			String path = "review_sentiment.v1";
			BufferedReader br = null, semifile = null;
			try {
				br = new BufferedReader(new FileReader(path+"/"+"train2.rlabelclass"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String data = null;
			while(true){
				try {
					data = br.readLine();
					if( data == null )
						break;
					String[] temp = data.split(" ");
					semifile = new BufferedReader(new FileReader(path+"/train2utf8/"+temp[0]));
					char[] tmp = new char[4096];
					semifile.read(tmp);
					String tipContent = new String(tmp);

					if(temp[1].equals("+1")) {
						words[1].add( token.getTextDefByList(tipContent) );
					} else if ( temp[1].equals("-1") ) {
						words[0].add( token.getTextDefByList(tipContent) );
					} else
						System.err.println("error: not distinguish +1/-1");
					semifile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/* read test data */
			try {
				br.close();
				br = new BufferedReader(new FileReader(path+"/"+"test2.list"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			while(true){
				try {
					data = br.readLine();
					if( data == null )
						break;
//					String[] temp = data.split(" ");
					semifile = new BufferedReader(new FileReader(path+"/test2utf8/"+data));
					char[] tmp = new char[4096];
					semifile.read(tmp);
					String tipContent = new String(tmp);

					estwords.add( token.getTextDefByList(tipContent) );
					semifile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	};
	
	
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
		
		public List<String> getTextDefByList( String text )throws IOException {
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
