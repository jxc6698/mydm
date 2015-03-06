package homework6;

import homework5.DT;

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

public class homework6 {
	
	
	public static void main( String argc[] ){
		
		globalconfig.debug_result = false ;
//		random_forest() ;
		ada_boost() ;
//		gradient_boosting() ;
		return ;
	}
	
	public static void random_forest()
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
		List<Double> num1[] = new ArrayList[ pred.CROSS_NUM ] ;
		for( int i=0;i<pred.CROSS_NUM ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		cross_fold( list[0] , num[0] , list1 , num1 , pred.CROSS_NUM ) ;
		
//		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
		int i = 0 ;
		{
			List<List<Double>> traindata = new ArrayList() ;
			List<Double> trainnum = new ArrayList() , result = new ArrayList() ;
			for(int j=0;j< pred.CROSS_NUM-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
				trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
			}
			randomForest maintree = new randomForest() ; 
			maintree.train(traindata, trainnum, classnum , 500, -1 , 0 );
			maintree.estimate( list1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ] , result );
		
			System.out.println("------------");
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( num1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ].get(j).equals( result.get(j) ) )
				{
					accu ++ ;
					if(globalconfig.debug_result)System.out.println(num1[9].get(j)) ;
				}
				else
					if(globalconfig.debug_result)System.out.println( num1[9].get(j) +"  " + result.get(j) ) ;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
		
	}
	
	public static void ada_boost()
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
		tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
//		tfidf.tf_append(featureset , pred.tokens , list , num , pred.CROSS_NUM ) ; 
	
		/* get 10 cross */
		List< List<Double> >[] list1 = new ArrayList[ pred.CROSS_NUM ] ;
		List<Double> num1[] = new ArrayList[ pred.CROSS_NUM ] ;
		for( int i=0;i<pred.CROSS_NUM ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		cross_fold( list[0] , num[0] , list1 , num1 , pred.CROSS_NUM ) ;
		
//		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
		int i = 0 ;
		{
			List<List<Double>> traindata = new ArrayList() ;
			List<Double> trainnum = new ArrayList() , result = new ArrayList() ;
			for(int j=0;j< pred.CROSS_NUM-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
				trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
			}
			adaboost maintree = new adaboost() ; 
			maintree.train(traindata, trainnum, classnum );
			maintree.estimate( list1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ] , result );
		
			System.out.println("------------");
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( num1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ].get(j).equals( result.get(j) ) )
				{
					accu ++ ;
					if(globalconfig.debug_result)System.out.println(num1[9].get(j)) ;
				}
				else
					if(globalconfig.debug_result)System.out.println( num1[9].get(j) +"  " + result.get(j) ) ;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
	}
	
	public static void gradient_boosting()
	{
		
		long start_time = System.currentTimeMillis();
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
		List<Double> num1[] = new ArrayList[ pred.CROSS_NUM ] ;
		for( int i=0;i<pred.CROSS_NUM ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		cross_fold( list[0] , num[0] , list1 , num1 , pred.CROSS_NUM ) ;
		
//		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
		int i = 0 ;
		{
			List<List<Double>> traindata = new ArrayList() ;
			List<Double> trainnum = new ArrayList() , result = new ArrayList() ;
			for(int j=0;j< pred.CROSS_NUM-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
				trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
			}
			gradientboost maintree = new gradientboost() ; 
			maintree.train(traindata, trainnum, classnum );
			maintree.estimate( list1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ] , result );
		
			System.out.println("------------");
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( num1[ (i+pred.CROSS_NUM-1)%pred.CROSS_NUM ].get(j).equals( result.get(j) ) )
				{
					accu ++ ;
					if(globalconfig.debug_result)System.out.println(num1[9].get(j)) ;
				}
				else
					if(globalconfig.debug_result)System.out.println( num1[9].get(j) +"  " + result.get(j) ) ;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
		
		
		long end_time = System.currentTimeMillis();
		System.out.println( (end_time-start_time) ) ;
	}
	
	static void cross_fold(List< List<Double>> list ,List num ,
			List< List<Double>>[] list1 , List[] num1 , int n )
	{
		for( int i=0;i<list.size() ; i ++ )
		{
			list1[ i%n ].add( list.get(i) ) ;
			num1[ i%n ].add( Double.parseDouble( num.get(i).toString()) ) ;
		}
		return ;
	}
	
}
