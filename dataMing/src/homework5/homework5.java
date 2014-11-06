package homework5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dataMing.globalconfig;
import dataMing.inputClass;
import dataMing.preProcessData;
import dataMing.predata;
import dataMing.readData;
import dataMing.tfAndtfidf;

public class homework5 {

	
	public static void main( String argc[] ){
		
		globalconfig.debug_result = false ;
//		task1() ;
//		task2345() ;
//		sdt_task1() ;
		sdt_task2345() ;

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
			num1[ i%n ].add( Double.parseDouble( num.get(i).toString()) ) ;
		}
		return ;
	}
	static void muti_group(List< List<Double>> list ,List num ,
			List< List<Double>>[] list1 , List[] num1 , int n )
	{
		Random r = new Random() ;
		double par = 0.5 ;
		for( int i=0;i<list.size() ; i ++ )
		{
			for( int j=0;j< n ; j++)
			{
				double tmp = r.nextDouble() ;
				if( tmp < par )
				{
					list1[ j ].add( list.get(i) ) ;
					num1[ j ].add( Double.parseDouble( num.get(i).toString()) ) ;
				}
			}
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
			DT maintree = new DT() ; 
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
	
	
	static void task2345()
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
//		taskclassify( list[0] , num[0] , pred.CROSS_NUM , classnum , 0 ) ;
//		taskclassify( list[1] , num[1] , pred.CROSS_NUM , classnum , 0 ) ;
		taskclassify( list[2] , num[2] , pred.CROSS_NUM , classnum , 1 ) ;
		taskclassify( list[3] , num[3] , pred.CROSS_NUM , classnum , 1 ) ;
	}
	
	
	static void taskclassify( List< List<Double> > list ,
	List<Double> num , int cross_num , int classnum , int classify_regression )
	{
		/* get 10 cross */
		List< List<Double> >[] list1 = new ArrayList[ cross_num ] ;
		List<Double> num1[] = new ArrayList[ cross_num ] ;
		for( int i=0;i<cross_num ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		eraseNaN( list , num ) ;
		cross_fold( list , num , list1 , num1 , cross_num ) ;
		
		
//		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 		
			int i = 0 ;
			{
				List<List<Double>> traindata = new ArrayList() ;
				List<Double> trainnum = new ArrayList() , result = new ArrayList() ;
				for(int j=0;j< cross_num-1 ;j++)
				{
					traindata.addAll( list1[ (i + j)%cross_num ] ) ;
					trainnum.addAll( num1[ (i + j)%cross_num] ) ;
				}
				DT maintree = new DT() ; 
				maintree.train(traindata, trainnum, classnum , 500, -1 , classify_regression );
				maintree.estimate( list1[ (i+cross_num-1)%cross_num ] , result );
			
				System.out.println("------------");
				int accu = 0 ;
				for( int j=0 ; j<result.size() ; j ++ )
				{
					if( num1[ (i+cross_num-1)%cross_num ].get(j).equals( result.get(j)) )
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
	
	
	
	
	static int  extractdata( readData readdata , List<List<Double>>[] data ,
			List<Double> num[] )
	{
		int featurenum = 0 ;
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
				featurenum = da.length-1 ;
				for( int k=0;k<da.length-1 ; k ++)
				{
					newlist.add( Double.parseDouble(da[k])) ;
				}
				data[i].add(newlist) ;
				num[i].add(Double.parseDouble( da[da.length-1]));
			}
		}
		return featurenum ;
	}
	
	static void eraseNaN( List< List<Double>> list ,List<Double> num)
	{
		if( list.size() == 0)
			return ;
		double count[] = new double[list.get(0).size()] ;
		double val[] = new double[list.get(0).size()] ;
		for( int i=0;i<list.get(0).size() ; i++ )
		{
			count[i] = 0 ;
			val[i] = 0 ;
		}
		
		for( int i=0;i<list.size() ; i++)
		{
			for( int j=0;j<list.get(i).size() ; j++)
			{
				if( list.get(i).get(j).isNaN() )
					;
				else
				{
					val[j]+= list.get(i).get(j);
					count[j] += 1 ;
				}
			}
		}
		for( int i=0;i<list.get(0).size() ; i++ )
		{
			val[i] /= count[i] ;
		}
		for( int i=0;i<list.size() ; i++)
		{
			for( int j=0;j<list.get(i).size() ; j++)
			{
				if( list.get(i).get(j).isNaN() )
					list.get(i).set(j, val[j]);
			}
		}
	}
	
	
	
	static void sdt_task1()
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
			List<List<Double>> traindata = new ArrayList() , estdata= null ;
			List<Double> trainnum = new ArrayList() , result = new ArrayList() , estnum = null ;
			for(int j=0;j< pred.CROSS_NUM-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%pred.CROSS_NUM ] ) ;
				trainnum.addAll( num1[ (i + j)%pred.CROSS_NUM] ) ;
			}
			
			List< List<Double> >[] listd = new ArrayList[ 10 ] ;
			List<Double> numy[] = new ArrayList[ 10 ] ;
			for( int j=0;j<10 ;j++)
			{
				listd[j] = new ArrayList() ;
				numy[j] = new ArrayList() ;
			}
			
			List<List<Double>> newlist = new ArrayList() ;
			List<List<Double>> newtmplist = new ArrayList() ;
			List<Double> newnum = new ArrayList() ;
		
			// 10 groups 
//			muti_group( traindata , trainnum , listd , numy , 10) ;
			cross_fold( traindata , trainnum , listd , numy , 10) ;
			for( int j=0; j < 10 ; j ++ )
			{
				newtmplist.clear();
				result.clear();
				for(int m=0;m<listd[j].size() ; m++)
				{
					newtmplist.add(new ArrayList()) ;
				}
				// 10 trees
				for( int k=0; k < 10 ; k ++ )
				{
					DT maintree = new DT() ; 
					maintree.train( listd[j] , numy[j], classnum , 10*k+10, -1 , 0 );
					maintree.estimate( listd[j] , result );
					
					for( int m= 0 ; m < listd[j].size() ; m ++ )
					{
						newtmplist.get(m).add(result.get(m)) ;
					}
				}
				newlist.addAll(newtmplist) ;
				newnum.addAll( numy[j] ) ;
				newtmplist.clear();
			}
			
			result.clear();
			DT metaDT = new DT() ;
			System.out.println(newlist.size() +"  "+newnum.size());
			metaDT.train( newlist , newnum, 10 , 100, -1 , 0 );
			
			DT dt[] = new DT[10] ; // base classifier
			for( int j=0;j<10;j++)
			{
				dt[i] = new DT() ;
				dt[i].train(traindata, trainnum, 10, 10*j+10, -1, 0 );
			}
			
			
			// estimate
			estdata = list1[ (i + pred.CROSS_NUM-1 )%pred.CROSS_NUM ] ;
			estnum = num1[ (i + pred.CROSS_NUM-1 )%pred.CROSS_NUM ] ;
			newlist.clear();
			for(int m=0;m<estdata.size() ; m++)
			{
				newlist.add(new ArrayList()) ;
			}
			for( int j = 0 ; j<10 ; j ++ )  //base classifier
			{
				dt[i].estimate(estdata, result);
				for( int m= 0 ; m < result.size() ; m ++ )
				{
					newlist.get(m).add(result.get(m)) ;
				}
				result.clear();
			}
			result.clear();
			metaDT.estimate(newlist, result);
			
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( estnum.get(j).equals( result.get(j) ) )
				{
					accu ++ ;
					System.out.println(estnum.get(j)) ;
				}
				else
//					System.out.println( estnum.get(j) +"  " + result.get(j) ) ;
					;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
	}
	
	
	static void sdt_task2345(){
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
		taskclassify( list[0] , num[0] , pred.CROSS_NUM , classnum , 0 ) ;
		taskclassify( list[1] , num[1] , pred.CROSS_NUM , classnum , 0 ) ;
//		taskclassify( list[2] , num[2] , pred.CROSS_NUM , classnum , 1 ) ;
//		taskclassify( list[3] , num[3] , pred.CROSS_NUM , classnum , 1 ) ;
		
		return ;
	}
	
	static void sdt_taskclassify( List< List<Double> > list ,
			List<Double> num , int cross_num , int classnum ,
			int classify_regression ) {
		
		/* get 10 cross */
		List< List<Double> >[] list1 = new ArrayList[ cross_num ] ;
		List<Double> num1[] = new ArrayList[ cross_num ] ;
		for( int i=0;i<cross_num ;i++)
		{
			list1[i] = new ArrayList() ;
			num1[i] = new ArrayList() ;
		}
		eraseNaN( list , num ) ;
		cross_fold( list , num , list1 , num1 , cross_num ) ;
		
		
//		for( int i=0;i< pred.CROSS_NUM ; i ++ ) 
		int i = 0 ;
		{
			List<List<Double>> traindata = new ArrayList() , estdata= null ;
			List<Double> trainnum = new ArrayList() , result = new ArrayList() , estnum = null ;
			for(int j=0;j< cross_num-1 ;j++)
			{
				traindata.addAll( list1[ (i + j)%cross_num ] ) ;
				trainnum.addAll( num1[ (i + j)%cross_num] ) ;
			}
			
			List< List<Double> >[] listd = new ArrayList[ 10 ] ;
			List<Double> numy[] = new ArrayList[ 10 ] ;
			for( int j=0;j<10 ;j++)
			{
				listd[j] = new ArrayList() ;
				numy[j] = new ArrayList() ;
			}
			
			List<List<Double>> newlist = new ArrayList() ;
			List<List<Double>> newtmplist = new ArrayList() ;
			List<Double> newnum = new ArrayList() ;
		
			// 10 groups 
			muti_group( traindata , trainnum , listd , numy , 10) ;
//			cross_fold( traindata , trainnum , listd , numy , 10) ;
			for( int j=0; j < 10 ; j ++ )
			{
				newtmplist.clear();
				result.clear();
				for(int m=0;m<listd[j].size() ; m++)
				{
					newtmplist.add(new ArrayList()) ;
				}
				// 10 trees
				for( int k=0; k < 10 ; k ++ )
				{
					DT maintree = new DT() ; 
					maintree.train( listd[j] , numy[j], classnum , 10*k+10, -1 , classify_regression );
					maintree.estimate( listd[j] , result );
					
					for( int m= 0 ; m < listd[j].size() ; m ++ )
					{
						newtmplist.get(m).add(result.get(m)) ;
					}
				}
				newlist.addAll(newtmplist) ;
				newnum.addAll( numy[j] ) ;
				newtmplist.clear();
			}
			
			result.clear();
			DT metaDT = new DT() ;
			System.out.println(newlist.size() +"  "+newnum.size());
			metaDT.train( newlist , newnum, 10 , 100, -1 , classify_regression );
			
			DT dt[] = new DT[10] ; // base classifier
			for( int j=0;j<10;j++)
			{
				dt[i] = new DT() ;
				dt[i].train(traindata, trainnum, 10, 10*j+10, -1, classify_regression );
			}
			
			
			// estimate
			estdata = list1[ (i + cross_num-1 )%cross_num ] ;
			estnum = num1[ (i + cross_num-1 )%cross_num ] ;
			newlist.clear();
			for(int m=0;m<estdata.size() ; m++)
			{
				newlist.add(new ArrayList()) ;
			}
			for( int j = 0 ; j<10 ; j ++ )  //base classifier
			{
				dt[i].estimate(estdata, result);
				for( int m= 0 ; m < result.size() ; m ++ )
				{
					newlist.get(m).add(result.get(m)) ;
				}
				result.clear();
			}
			result.clear();
			metaDT.estimate(newlist, result);
			
			int accu = 0 ;
			for( int j=0 ; j<result.size() ; j ++ )
			{
				if( estnum.get(j).equals( result.get(j) ) )
				{
					accu ++ ;
					if(globalconfig.debug_result)System.out.println(estnum.get(j)) ;
				}
				else
					if(globalconfig.debug_result)System.out.println( estnum.get(j) +"  " + result.get(j) ) ;
			}
			System.out.println( "total  "+result.size() +"  accu  "+accu );
		}
		return ;
	}
	
	
}
