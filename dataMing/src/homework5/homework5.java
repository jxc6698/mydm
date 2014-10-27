package homework5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dataMing.globalconfig;
import dataMing.inputClass;
import dataMing.preProcessData;
import dataMing.predata;
import dataMing.readData;
import dataMing.tfAndtfidf;

public class homework5 {

	
	public static void main( String argc[] ){
		
//		task1() ;
		task2() ;
//		task3() ;
//		task4() ;
//		task5() ;
		return ;
	}
	/* ID3 is better than C4.5...
	 * with feature 3700   91 / 162
	 * with feaure 17329   99 / 162
	 * */
	
	static void cross_fold(List< List<Double>> list ,List num ,
			List< List<Double>>[] list1 , List[] num1 , int n )
	{
		for( int i=0;i<list.size() ; i ++ )
		{
			list1[ i%n ].add( list.get(i) ) ;
			num1[ i%n ].add( num.get(i) ) ;
		}
		return ;
	}
	
	
	static void task1()
	{
		String Dir = "lily" ;
		predata pred = new predata() ;
		
		globalconfig.whether_cross_check = false ;
		globalconfig.tfbool = false ;
		
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
//		tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
		tfidf.tf_append(featureset , pred.tokens , list , num , pred.CROSS_NUM ) ; 
	
		/* get 10 cross */
		List< List<Double> >[] list1 = new ArrayList[ pred.CROSS_NUM ] ;
		List<Integer> num1[] = new ArrayList[ pred.CROSS_NUM ] ;
		for( int i=0;i<pred.CROSS_NUM ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		cross_fold( list[0] , num[0] , list1 , num1 , pred.CROSS_NUM ) ;
		
		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
//		int i = 0 ;
		{
			List<List<Double>> traindata = new ArrayList() ;
			List<Integer> trainnum = new ArrayList() , result = new ArrayList() ;
			for(int j=0;j< pred.CROSS_NUM-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
				trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
			}
			DT maintree = new DT() ; 
			maintree.train(traindata, trainnum, classnum , 500, -1 );
			maintree.estimate( list1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ] , result );
		
			System.out.println("------------");
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( num1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ].get(j) == result.get(j) )
				{
					accu ++ ;
//					System.out.println(num1[9].get(j)) ;
				}
				else
//					System.out.println( num1[9].get(j) +"  " + result.get(j) ) ;
					;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
	}
	
	
	static void task2()
	{
		String Dir = "home5" ; //"home5/breast-cancer.data" ;
		predata pred = new predata() ;
		
		globalconfig.whether_cross_check = false ;
		globalconfig.tfbool = false ;
		
		inputClass inputclass = new inputClass( Dir ) ;
		inputclass.getAllFiles();

		readData readdata = inputclass.readAllFiles() ;
		
		
		List< List<Double> >[] list = new ArrayList[ 4 ] ;
		List<Double> num[] = new ArrayList[ 4] ;
		for(int i=0;i<4; i ++)
		{
			list[i] = new ArrayList() ;
			num[i] = new ArrayList<Double>() ;
		}
		
		int featurenum = 0 , classnum = 0 ;
		featurenum = extractdata( readdata , list , num ) ;
		
		// fillter part attribute
//		featurenum = preprocessdata.fillter( pred.total , featurenum , featureset , pred.stopwords );
		
		// divide the data into  CROSS_NUM parts 

			
		/* get 10 cross */
		List< List<Double> >[] list1 = new ArrayList[ pred.CROSS_NUM ] ;
		List<Integer> num1[] = new ArrayList[ pred.CROSS_NUM ] ;
		for( int i=0;i<pred.CROSS_NUM ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		cross_fold( list[0] , num[0] , list1 , num1 , pred.CROSS_NUM ) ;
		
		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
//			int i = 0 ;
			{
				List<List<Double>> traindata = new ArrayList() ;
				List<Integer> trainnum = new ArrayList() , result = new ArrayList() ;
				for(int j=0;j< pred.CROSS_NUM-1 ;j++)
				{
					traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
					trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
				}
				DT maintree = new DT() ; 
				maintree.train(traindata, trainnum, classnum , 500, -1 );
				maintree.estimate( list1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ] , result );
			
				System.out.println("------------");
				int accu = 0 ;
				for( int j=0 ; j<result.size() ; j ++ )
				{
					if( num1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ].get(j) == result.get(j) )
					{
						accu ++ ;
//						System.out.println(num1[9].get(j)) ;
					}
					else
//						System.out.println( num1[9].get(j) +"  " + result.get(j) ) ;
						;
				}
				System.out.println( "total  "+result.size() +"  accu  "+accu );
			}
			
	}
	
	
	
	
	
	static int  extractdata( readData readdata , List<List<Double>>[] data ,
			List<Double> num[] )
	{
		int order[] = new int[4] ;
		for( int i= 0 ; i< readdata.line.length ; i ++)
		{
			if( readdata.filenames[i].equals( "breast-cancer.data") )
				order[0] = i ;
			else if( readdata.filenames[i].equals( "segment.data") )
				order[1] = i ;
			else if( readdata.filenames[i].equals( "housing.data") )
				order[2] = i ;
			else if( readdata.filenames[i].equals( "meta.data") )
				order[3] = i ;
			else
				System.out.println("error 2");
		}
		for( int i=0;i < order.length ; i++ )
		{
			for( int j = 1 ; j < readdata.line[ order[i] ].size() ; j ++ )
			{
				String line = (String) readdata.line[ order[i] ].get(j) ;
				String[] da = line.split(",") ;
				List<Double> newlist = new ArrayList<Double>() ;
				for( int k=0;k<da.length-1 ; k ++)
				{
					newlist.add( Double.parseDouble(da[i])) ;
//					System.out.println(da[k]);
				}
				data[i].add(newlist) ;
				num[i].add(Double.parseDouble( da[da.length-1]));
			}
		}
		return 0 ;
	}
	
	
}
