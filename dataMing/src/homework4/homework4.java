package homework4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dataMing.globalconfig;
import dataMing.inputClass;
import dataMing.preProcessData;
import dataMing.predata;
import dataMing.readData;
import dataMing.tfAndtfidf;

public class homework4 {

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
//		tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
		tfidf.tf_append(featureset , pred.tokens , list , num , pred.CROSS_NUM ) ; 
		


		List<List<Double>>trainset = null ;
		List<Integer> trainnum = new ArrayList<Integer>() ;
		trainset = new ArrayList() ;
		
		
		Set<Integer> center = new HashSet<Integer>() , oldcenter = new HashSet<Integer>() ;
		Set<Integer> pointset[] = new HashSet[ classnum ] ;
		for(int i=0;i<classnum;i++)
			pointset[i] = new HashSet<Integer>() ;
		
		for( int p = 0 ; p < list[ 0].size() ; p ++ ) 
		{
			trainset.add( list[0].get(p) );
			trainnum.add( num[0].get(p) ) ;
		}
		
		Random r = new Random() ;
		while( center.size() != 10 )
		{
			center.add( r.nextInt( trainset.size() ) ) ;
		}
		oldcenter.addAll( center ) ;
		
		System.out.println(new Date());
		
		int cc = 0 ;
		while( true )
		{
			cc ++ ;
			
			Double dis , tmp ;
			int index = -1 , count ;
			for( int i=0 ; i < pointset.length ; i ++ )
				pointset[i].clear();
			
			for( int i = 0 ; i < trainset.size() ; i ++ )
			{
				Iterator<Integer> iter = center.iterator() ;
				dis = -1.0 ;
				count = 0 ;
				index = -1 ;
				while( iter.hasNext() )
				{
					int c = iter.next() ;
					tmp = distance( trainset.get(c) , trainset.get(i) , c, i ) ;
					if( index == -1 )
					{
						dis = tmp ;
						index = count ;
					}else{
						if( tmp < dis )
						{
							dis = tmp ;
							index = count ;
						}
					}
					count ++ ;
				}
				pointset[ index ].add( i ) ;
			}
			
			oldcenter = center ;

			center = getcenter( pointset , trainset ) ;
			if( center.equals( oldcenter ) )
				break ;
			else
			{
				Iterator iter = center.iterator() ;
				while( iter.hasNext() )
				{
					int c = (int) iter.next() ;
					if(  oldcenter.contains(c) )
						System.out.println( c + " " );
				}
			}
			if( cc > 10 )
				break ;
			System.out.println( "round   " + cc ) ;
		}
		System.out.println( "round   " + cc ) ;
		System.out.println(new Date());
		Double nmi = judge( pointset , trainnum , classnum ) ;
		System.out.println( "nmi  " + nmi ) ;
		
		
		

		// method 2 

		double dc = 0.007 ;
		double dis[][] = new double[trainset.size()][] ;
		for( int i=0 ; i < trainset.size() ; i ++ )
			dis[i] = new double[ trainset.size() ] ;
		
		int record[] = new int[ trainset.size() ] ;
		double sita[] = new double[ trainset.size() ] ;
		double mul[] = new double[ trainset.size() ] ;

		ArrayList<Double> xxx = new ArrayList<Double>() ;
		
		for( int i =0 ; i< trainset.size() ;i ++ )
		{	
			Double tmp ;
			for( int j = 0 ; j < i ; j++ )
			{
				tmp = distance( trainset.get(i) , trainset.get(j) , i , j ) ;
				dis[j][i] = dis[i][j] = tmp ;
				xxx.add(tmp) ;
			}
			dis[i][i] = 0 ;
		}
		Collections.sort( xxx );

		double xxxx[] = new double[44] ;
		for( int i = 0 ; i < 40 ; i ++ )
			xxxx[i] =  xxx.get( (int)(0.01 *i * xxx.size()) ) ;
		double max = 0 ;
		for(int pp = 0 ; pp < 40 ; pp ++)
		{
			
			dc = xxxx[pp] ;
			for( int i = 0 ; i < trainset.size() ; i ++ )
				record[i] = 0 ;
			for( int i =0 ; i< trainset.size() ;i ++ )
			{	
				Double tmp ;
				for( int j = 0 ; j < i ; j++ )
				{
					if( dis[i][j] < dc )
					{
						record[ i ] ++ ;
						record[ j ] ++ ;
					}
				}
		//		record[i] ++ ;
			}
			
		for( int i = 0 ; i < trainset.size() ; i ++ )
		{
			double tmp = -2.0 ;
			for( int j = 0 ; j < trainset.size() ; j ++ )
			{
				if( record[i] < record[j] || ( record[i] == record[j] && i > j ) )
				{
					if( tmp == -2.0 )
					{
						tmp = dis[i][j] ;
					}
					else if( tmp > dis[i][j] )
					{
						tmp = dis[i][j] ;
					}
				}
			}
			if( tmp == -2.0 )
			{
				for( int j = 0 ; j < trainset.size() ; j ++ )
				{
					if( tmp == -2.0 )
						tmp = dis[i][j] ;
					else if( tmp < dis[i][j] )
						tmp = dis[i][j] ;
				}
			}
			sita[i] = tmp ;
			mul[i] = tmp * record[i] ;
		}
		
		for( double ppp = 0.75 ; ppp < 0.95 ; ppp ++ )
		{
		int frontk[] = new int[11] ;
		getfrontk( frontk , record , sita , mul , trainset.size()  , ppp ) ;
		
//		for( int i=0;i< 10 ; i ++ )
//			System.out.println( frontk[i] + "   " + trainnum.get(frontk[i]) + "   " 
//					+ sita[ frontk[i] ] + "   " + record[ frontk[i] ] );
//		System.out.println("-------");
		
		for( int i=0 ; i < pointset.length ; i ++ )
			pointset[i].clear();
		for(int i = 0 ; i< trainset.size() ;i ++)
		{
			double tmp =0 ;
			int index = -1 ;
			for( int j = 0 ; j < 10 ; j ++ )
			{
				if( index == -1)
				{
					index= j ;
					tmp = dis[i][ frontk[j]] ;
				}
				else
				{
					if( dis[i][ frontk[j] ] < tmp )
					{
						index = j ;
						tmp = dis[i][ frontk[j]] ;
					}
				}
			}
			pointset[ index ].add( i ) ;
		}

		
//		System.out.println("=========");
//		System.out.println(new Date());
		double nmi2 = judge( pointset , trainnum , classnum ) ;
//		System.out.println( "nmi  " + nmi2 ) ;
		if( nmi2 > max )
			max = nmi2 ;
		}   /// ppp
		} // pp
		System.out.println("max is " + max);

	}
	
	static void getfrontk( int[] frontk , int[] record , double[] sita , double[] mul , int size , double per )
	{
		int knum = 0 ;
		List tmplist = new ArrayList() ;
		for( int i = 0 ; i< size ; i ++ )
		{
			tmplist.add(record[i] ) ;
		}
		Collections.sort(tmplist);
		List<Integer> tmpset = new ArrayList() ;
		
		int di = (int)tmplist.get(  (int)( 0.80 * tmplist.size() ) ) ;
//		System.out.println(di);
		class stu{
			int id ;
			double sita ;
			public stu( int id , double sita )
			{
				this.id = id ;
				this.sita = sita ;
			}
		}
		
		Comparator<stu> comparator = new Comparator<stu>(){  
			   public int compare(stu s1, stu s2) {  
			    //先排年龄  
			    if(s1.sita > s2.sita){  
			     return 1 ;  
			    }
			    else if( s1.sita == s2.sita ){  
			    	return 0 ;
			    }else
			    	return -1 ;
			   }  
			  };  
		
		List<stu> tmpdis = new ArrayList() ;
		for( int i = 0 ; i < size ; i ++ )
		{
			if( record[i] >= di )
			{
				tmpset.add(i) ;
				tmpdis.add( new  stu( i , sita[i] ) ) ;
			}
		}

		Collections.sort(tmpdis , comparator );

		for( int i = 1 ; i < 11 ; i ++ )
		{
			frontk[i-1] = tmpdis.get( tmpdis.size() - i ).id ;
		}
		
	}
	
	static Double distance( List<Double> p1 , List<Double> p2 , int in1 , int in2 )
	{
		Double dis ;
		Double tmp , tmp1 , tmp2 ;		

		tmp = new Double(0) ; tmp1 = new Double(0) ; tmp2 = new Double(0) ;
		for(int j = 0 ; j < p1.size() ; j ++ )
		{
			tmp += ( p1.get(j) * p1.get(j) ) ;
			tmp1 += ( p1.get(j) * p2.get(j) ) ;
			tmp2 += ( p2.get(j) * p2.get(j) ) ;
		}
		if( tmp == 0 || tmp2 == 0 )
		{
			if( tmp == 0 )
			{
				tmp = 0.00000001 ;
			}
			if( tmp2 == 0 )
			{
				tmp2 = 0.00000001 ;
			}
		}
		dis = Math.acos( tmp1 / ( Math.sqrt(tmp) * Math.sqrt( tmp2 ) ) ) ;
		if( dis == 0 )
			return 0.00001 ;
		return dis ;
	}
	
	static Set getcenter( Set<Integer>[] pointset , List<List<Double>> data  )
	{
		int feature = data.get(0).size() , tmp , index ;
		Double dis , tmp1 ;
		Set<Integer> newset = new HashSet<Integer>() ;
		List<Double > centerp = new ArrayList<Double>() ;
		for(int i=0;i < pointset.length ; i ++ )
		{
			centerp.clear();
			Iterator iter = pointset[i].iterator() ;
			while( iter.hasNext() )
			{
				tmp = (int) iter.next() ;
				if( centerp.size() == 0 )
				{
					for( int k =0;k< feature ; k ++ )
						centerp.add( 0.0 ) ;
				}
				for( int k = 0 ; k< feature ; k++ )
				{
					 centerp.set(  k , centerp.get(k)+ data.get( tmp ).get(k) ) ;
				}
			}
			try{
				centerp.get(0) ;
			}catch( Exception e ) {
				for( int kk = 0 ; kk < pointset.length ; kk ++ )
				{
					System.out.println( pointset[i].size() );	
				}
				System.out.println( "go to poinset : " + i );
			}
			for( int k = 0 ; k < feature ; k ++ )
				centerp.set(k, (  centerp.get(k)) / pointset[i].size() ) ;
			dis = 0.0 ; //		for( int i=0;i< pointset.length ; i++ )
//			System.out.println( pointset[i].size() );
			index = -1 ;
			iter = pointset[i].iterator() ;
			int count = 0 ;
			while( iter.hasNext() )
			{
				tmp = (int) iter.next() ;
				tmp1 = distance( data.get( tmp ) , centerp , -1 , -1 ) ;
				if( index == -1 )
				{
					index = tmp ;
					dis = tmp1 ;
				}else if( tmp1 < dis )
				{
					index = tmp ;
					dis = tmp1 ;
				}
			}
			newset.add( index ) ;
		}
		return newset ;
	}
	
	static Double judge( Set<Integer>[] pointset ,
			List<Integer> trainnum , 
			int classnum )
	{
		
		Double h = 0.0 , h1 =0.0 , h2 = 0.0 ;
		int total = trainnum.size() ;
		// calculate h1  entropy about cluster
		Double pw[] = new Double[pointset.length] ;
		Double pc[] = new Double[ classnum ] ; 
		for(int i =0;i < pointset.length ; i ++ )
		{
			pw[i] = ((double)pointset[i].size()) / total ;
			h1 += -pw[i] * Math.log(pw[i]) ;
		}
		
		int[] csum = new int[ classnum ] ;

		for( int i=0;i < classnum ; i ++  )
		{
			csum[i] = 0 ;
		}
		// calculate h2   entropy about class
		for( int i=0;i < pointset.length ;i ++  )
		{
			Iterator iter = pointset[i].iterator() ;
			while( iter.hasNext() )
			{
				int c = (int)iter.next() ;
				csum[ trainnum.get( c ) ]++ ;
			}
		}
		for( int i=0 ; i < classnum ; i++ )
		{
			pc[i] = ((double)csum[i]) / total ;
			h2 += -pc[i] * Math.log(pc[i]) ;
		}
		
		//  calculate i 
		for( int i =0;i < pointset.length ; i ++ )
		{
			for( int j=0;j < classnum ; j ++  )
			{
				csum[j] = 0 ;
			}
			Iterator iter = pointset[i].iterator() ;
			while( iter.hasNext() )
			{
				int c = (int)iter.next() ;
				csum[ trainnum.get( c ) ]++ ;
			}
			for( int j=0 ; j < classnum ; j++ )
			{
				if( csum[j] != 0 )
				{
					Double p = ((double)csum[j]) / total ;
					h += p * Math.log( p/(pw[i]*pc[j] ) ) ;
				}
			}
		}
//		System.out.println("h1   : " + h1);
//		System.out.println("h2   : " + h2);
//		System.out.println("h   : " + h);
//		
		return h * 2 /( h1 + h2 );
	}
}