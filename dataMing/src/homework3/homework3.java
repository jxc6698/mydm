package homework3;

import homework2.gradientDescent;

import java.util.ArrayList;
import java.util.Date;
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

public class homework3 {

	
	static boolean debug = globalconfig.debug ;
	static boolean cycle_debug = globalconfig.cycle_debug ;
	
	public static void main( String argc[] ){
		
		
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
	//	tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
		tfidf.tf_append(featureset , pred.tokens , list , num , pred.CROSS_NUM ) ; 
		

		List< List<Integer> > hashset = new ArrayList() ;
		List<List<Double>>trainset = null , estset = null ;
		List<Integer> trainnum = new ArrayList<Integer>() , estnum = new ArrayList<Integer>() ;
		trainset = new ArrayList() ;
		
		estset = new ArrayList() ;
		
		for( int p = 0 ; p < list[ 0].size() ; p ++ ) 
		{
			trainset.add( list[0].get(p) );
			trainnum.add( num[0].get(p) ) ;
		}
		
		for( int i=0 ; i< 200 ; i++ )
		{
			int round = (int) ( Math.random() * trainset.size() ) ;
			estset.add( trainset.get(round) ) ;
			estnum.add( trainnum.get(round) ) ;
			
			trainset.remove( round );
			trainnum.remove( round ) ;
			
		}
		
		LSH lsh = new LSH() ;
		lsh.init(featurenum, 1  );
		lsh.lshhashset(trainset, trainnum, hashset);
		
//		for( int pp = 0 ; pp < hashset.size() ; pp ++)
//			System.out.println( hashset.get(pp).size() );

		long start_time = System.currentTimeMillis();
		
		for( int k =10 ; k <= 50 ; k += 10)
//		int k = 10 ; 
		{
			int totalaccu = 0 ;
			int knum = 0 ;
			double mean[] = new double[estset.size()] ;
			double tt[] = new double[estset.size()] ;
			Integer maxk[] = new Integer[ k+1 ] ;
			Double maxkv[] = new Double[ k+1 ] ;
			
			for( int i =0 ; i < estset.size() ; i ++ )
//			int i = 0;
			{
				long time1 = System.currentTimeMillis();
				int c = lsh.lshhash(  estset.get(i) ) ;
				
				if( k > hashset.get(c).size() )
				{
					System.out.println("hehehehehe");
					System.exit(0);
				}
				knum = 0 ;
				for( int j = 0 ; j<maxk.length ; j ++)
				{
					maxk[j] = 0 ;
					maxkv[j] = 1000000.0 ;
				}
				for(int j = 0 ; j < hashset.get(c).size() ; j ++ )
				{
					int tmp = knum ;
					Double dis = distance( trainset.get( hashset.get(c).get(j) ) , estset.get(i) ) ;
					for( int m=0; m < knum ; m ++)
					{
						if( dis < maxkv[m] )
						{
							tmp = m ;
							break ;
						}
					}
					if( tmp == knum && knum < k )
					{
						maxk[knum] = hashset.get(c).get(j) ;
						maxkv[knum] = dis ;
						knum ++ ;
					}
					else if( tmp == knum )
						continue ;
					else  // tmp != knum
					{
						for( int n=knum-1 ; n >= tmp ; n --)
						{
							maxk[n+1] = maxk[n] ;
							maxkv[n+1] = maxkv[n] ;
						}
						maxk[tmp] = hashset.get(c).get(j) ;
						maxkv[tmp] = dis ;
						if( knum < k )
							knum ++ ;
					}
					
				}
				int accu = 0 ;
			
				for( int j= 0 ; j < k ; j ++ )
				{	
					if( trainnum.get( maxk[j] ) == estnum.get( i ) )
					{
						accu ++ ;
					}
				}
				totalaccu += accu ;
				long time2 = System.currentTimeMillis();
				tt[i] = time2 - time1 ;
				mean[i] = ((double)accu) / k  ;
			}

			double totalmean = 0 ;
			double meantt = 0 ;
			double totalstd = 0 ;
			double stdtt = 0 ;
			for( int i =0;i< estset.size() ; i++ )
			{
				totalmean += mean[i] ;
				meantt += tt[i] ;
			}
			totalmean = totalmean / estset.size() ;
			meantt = meantt / estset.size() ;
			for( int i =0 ; i<  estset.size() ;i ++)
			{
				totalstd += ( mean[i] -totalmean ) * ( mean[i] -totalmean ) ;
				stdtt += ( tt[i] - meantt ) * ( tt[i] - meantt ) ;
			}
			totalstd = totalstd / estset.size() ;
			stdtt = stdtt / estset.size() ;
			
			System.out.println(k);
			System.out.println( totalmean + "    " + Math.sqrt( totalstd) );
			System.out.println( meantt + "     " + Math.sqrt(stdtt) );
			System.out.println(k + "    " + totalaccu );
		}
		

/*   brute force  */		
		for( int k =10 ; k <= 50 ; k += 10)
		{
			int knum = 0 ;
			int totalaccu = 0 ;
			double tt[] = new double[estset.size()] ;
			
			Integer maxk[] = new Integer[ k+1 ] ;
			Double maxkv[] = new Double[ k+1 ] ;
			
			for( int i =0 ; i < estset.size() ; i ++ )
			{
				long time1 = System.currentTimeMillis();
				knum = 0 ;
				for( int j = 0 ; j<maxk.length ; j ++)
				{
					maxk[j] = 0 ;
					maxkv[j] = 1000000.0 ;
				}
				for(int j = 0 ; j < trainset.size() ; j ++ )
				{
					int tmp = knum ;
					Double dis = distance( trainset.get( j ) , estset.get(i) ) ;
					for( int m=0; m < knum ; m ++)
					{
						if( dis < maxkv[m] )
						{
							tmp = m ;
							break ;
						}
					}
					if( tmp == knum && knum < k )
					{
						maxk[knum] = j ;
						maxkv[knum] = dis ;
						knum ++ ;
					}
					else if( tmp == knum )
						continue ;
					else  // tmp != knum
					{
						for( int n=knum-1 ; n >= tmp ; n --)
						{
							maxk[n+1] = maxk[n] ;
							maxkv[n+1] = maxkv[n] ;
						}
						maxk[tmp] = j ;
						maxkv[tmp] = dis ;
						if( knum < k )
							knum ++ ;
					}
					
				}
				int accu = 0 ;
			
				for( int j= 0 ; j < k ; j ++ )
				{	
					if( trainnum.get( maxk[j] ) == estnum.get( i ) )
					{
						accu ++ ;
					}
				}
			
				totalaccu += accu ;
				long time2 = System.currentTimeMillis();	
				tt[i] = time2 - time1 ;
			}

			double meantt = 0 ;
			double stdtt = 0 ;
			for( int i =0;i< estset.size() ; i++ )
			{
;
				meantt += tt[i] ;
			}
			meantt = meantt / estset.size() ;
			for( int i =0 ; i<  estset.size() ;i ++)
			{
				stdtt += ( tt[i] - meantt ) * ( tt[i] - meantt ) ;
			}
			stdtt = stdtt / estset.size() ;
			
			System.out.println(k);
			System.out.println( meantt + "     " + stdtt );
			System.out.println(k + "    " + totalaccu );
		}
		
		long start_time3 = System.currentTimeMillis();
		
		return ;
	}
	
	 static Double distance( List<Double> p1 , List<Double> p2 )
	 {
		Double dis = new Double( 0) ;
		
		
//		for(int i = 0 ; i < p1.size() ; i ++ )
//		{
//			dis += Math.pow( p1.get(i) - p2.get(i) , 2 ) ;
//	//		dis += Math.abs( p1.get(i) - p2.get(i) ) ;
//		}
//	//	return  Math.sqrt( dis ) ;
		Double tmp , tmp1 , tmp2 ;
		

		tmp = new Double(0) ; tmp1 = new Double(0) ; tmp2 = new Double(0) ;
		for(int j = 0 ; j < p1.size() ; j ++ )
		{
			tmp += p1.get(j) * p1.get(j) ;
			tmp1 += p1.get(j) * p2.get(j) ;
			tmp2 += p2.get(j) * p2.get(j) ;
		}
		dis = Math.acos( tmp1 / ( Math.sqrt(tmp) * Math.sqrt( tmp2 ) ) ) ;
//		dis = 1 - (dis/Math.PI) ;
		
		
		return dis ;
	}
	 
	
}
