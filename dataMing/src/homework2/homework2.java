package homework2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataMing.globalconfig;
import dataMing.inputClass;
import dataMing.preProcessData;
import dataMing.predata;
import dataMing.readData;
import dataMing.tfAndtfidf;


public class homework2 {
	
	static boolean debug = globalconfig.debug ;
	
	public static void main( String argc[] ){
		String Dir = "lily" ;
		predata pred = new predata() ;
		
		inputClass inputclass = new inputClass( Dir ) ;
		inputclass.getAllFiles();

		readData readdata = inputclass.readAllFiles() ;
		
		preProcessData preprocessdata = new preProcessData( readdata ) ;

		preprocessdata.prepare() ;
		preprocessdata.getpreparedata( pred );
		
		int classnum = pred.tokens.length ;
		
		int featurenum = 0 ;
		// fillter part attribute
		Set featureset = new HashSet<String>() ;
		featurenum = preprocessdata.fillter( pred.total , featurenum , featureset , pred.stopwords );
		
		// divide the data into  CROSS_NUM parts 
		List< List<Double> >[] list = new ArrayList[ pred.CROSS_NUM ] ;
		List<Integer> num[] = new ArrayList[ pred.CROSS_NUM ] ;
		for(int i=0;i<pred.CROSS_NUM; i ++)
		{
			list[i] = new ArrayList() ;
			num[i] = new ArrayList<Integer>() ;
		}
		
		
		tfAndtfidf tfidf = new tfAndtfidf() ;
		tfidf.tf_start();
	//	tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
		tfidf.tf_append(featureset , pred.tokens , list , num , pred.CROSS_NUM ) ; 
		gradientDescent grad[] = new gradientDescent[ classnum  ] ;

		
		List<List<Double>> c1 = null , c2 = null , allset = null , estset = null ;
		List<Integer> allnum = new ArrayList<Integer>() , estnum = new ArrayList<Integer>() ;
		List<Integer> n1=new ArrayList<Integer>() , n2 = new ArrayList<Integer>() ;
		c1 =new ArrayList() ; c2= new ArrayList() ; allset = new ArrayList() ;
		estset = new ArrayList() ;
		for( int j = 0 ; j < pred.CROSS_NUM ; j ++ )
//		int j = 0 ;
		{
			// init classifier
			for( int i =0;i<grad.length ; i ++ )
				grad[i] = new gradientDescent() ;
			allset.clear();
			allnum.clear();
			c1.clear(); n1.clear();
			c2.clear(); n2.clear();
			for( int k=0 ; k < pred.CROSS_NUM-1 ; k ++ )
			{
				int tt =(j+k)%pred.CROSS_NUM ;
				for( int p = 0 ; p < list[ tt].size() ; p ++ ) 
				{
					allset.add( list[ tt ].get(p) );
					allnum.add( num[tt].get(p) ) ;
				}
			}
			estset.clear();
			estnum.clear();
			estset.addAll( list[(j+ pred.CROSS_NUM-1 )%pred.CROSS_NUM ] ) ;
			estnum.addAll( num[ (j+ pred.CROSS_NUM-1 )%pred.CROSS_NUM ] ) ;
			
			int totaltrain = estset.size() ;
//			List<Integer> result = null ;
			List<Double> result[] = new ArrayList[ classnum ] ;
			int accu = 0 ;
			for( int k = 0 ; k < classnum  ; k ++ )
//			int k = 0 ;	
			{
				getc( allset , c1 , c2 , allnum , k , n1 , n2 ) ;
				grad[k].init( featurenum ) ;
				grad[k].trainclassifier(c1, c2);
				if(debug)
				{
					System.out.println("------------");
					
					
					result[k] = grad[k].estimate( c2 ) ;
					
					Iterator iter = result[k].iterator() ;
					Iterator iter2 = c2.iterator() ;
					Iterator iter3 = n2.iterator() ;
					while( iter.hasNext() )
					{
						Integer in = (Integer) iter.next() ;
						iter2.next() ;
						iter3.next() ;
						if( in == 0 )
						{
							;
						}else if( k == classnum-2 )
						{
							accu ++ ;
						}
					}
					
					List tmp = null ;
					result[0] = grad[k].estimate( c1 ) ;
					iter = result[0].iterator() ;
					iter2 = c1.iterator() ;
					iter3 = n1.iterator() ;
					while( iter.hasNext() )
					{
						Integer in = (Integer) iter.next() ;
						tmp = (List)iter2.next() ;
						iter3.next() ;
						if( in == 0 )
						{
							accu ++ ;
						}
					}
					
				}
		//		allnum = n2 ;
		//		allset = c2 ;
	
				c1 =new ArrayList() ; c2= new ArrayList() ;
				n1=new ArrayList<Integer>() ; n2 = new ArrayList<Integer>() ;
				System.gc();
			}
			if( !debug )
			{
			for( int k = 0 ; k < classnum ; k ++ )
		//	int k = 0 ;
			{
				result[k] = grad[k].estimate( estset ) ;
			}
			for( int k=0; k< estset.size() ; k ++ )
			{
				int res = 0 ;
				Double tmp = 100.0 ;
				for( int i = 0 ; i < classnum ; i ++ )
				{
					if( result[i].get(k) < tmp )
					{
						res = i ;
						tmp = result[i].get(k) ;
					}
				}
		//		System.out.println(res + "  " + estnum.get(k)) ;
				if( res == estnum.get(k) )
				{
					accu ++ ;
				}
				
			}
				
			System.out.println(accu);
//			accu += estset.size() ;
			}
			System.out.println( "total  " + totaltrain + "  accuracy  " + accu ) ;
		}
		
		
		return ;
	}
	
	static void getc( List<List<Double>> list ,
			List< List<Double> > c1 ,
			List< List<Double> > c2 ,
			List<Integer> num ,
			int f1 , List<Integer> n1 , List<Integer> n2 )
	{
		for( int i = 0 ; i < list.size() ; i ++ )
		{
			if( num.get(i) == f1 )
			{
				c1.add( list.get(i) ) ;
				n1.add( num.get(i) ) ;
//				n1.add( 1 ) ;
			}
			else //if( num.get(i) == f1 +1 )
			{
				c2.add( list.get(i) ) ;
				n2.add( num.get(i) ) ;
//				n2.add( 0 ) ;
			}
		}
	}
}
