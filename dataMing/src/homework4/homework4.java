package homework4;

import java.util.ArrayList;
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
	//	tfidf.tfidf( featureset , pred.tokens , pred.total , list , num , pred.CROSS_NUM ) ; 
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
			for( int i = 0 ; i < trainset.size() ; i ++ )
			{
				Iterator<Integer> iter = center.iterator() ;
				dis = -1.0 ;
				count = 0 ;
				index = -1 ;
				while( iter.hasNext() )
				{	
					int c = iter.next() ;
					tmp = distance( trainset.get(c) , trainset.get(i) ) ;
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
			if( center.equals( oldcenter ))
				break ;
			System.out.println( "round   " + cc ) ;
		}
		System.out.println( "round   " + cc ) ;
		System.out.println(new Date());
		System.out.println("end") ;
		Double nmi = judge( pointset , trainnum , classnum ) ;
		System.out.println( "nmi  " + nmi ) ;
	}
	
	
	static Double distance( List<Double> p1 , List<Double> p2 )
	{
		Double dis = new Double( 0) ;
		
		
		for(int i = 0 ; i < p1.size() ; i ++ )
		{
			dis += Math.pow( p1.get(i) - p2.get(i) , 2 ) ;
		}
		return  Math.sqrt( dis ) ;
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
			}
			for( int k = 0 ; k < feature ; k ++ )
				centerp.set(k, ( (double)( centerp.get(k))) / pointset[i].size() ) ;
			dis = 0.0 ; 
			index = -1 ;
			iter = pointset[i].iterator() ;
			int count = 0 ;
			while( iter.hasNext() )
			{
				tmp = (int) iter.next() ;
				tmp1 = distance( data.get( tmp ) , centerp ) ;
				if( index == -1 )
				{
					index = count ;
					dis = tmp1 ;
				}else if( tmp1 < dis )
				{
					index = count ;
					dis = tmp1 ;
				}
				count ++ ;
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
		// calculate h1  entropy about cluster
		Double pw[] = new Double[pointset.length] ;
		Double pc[] = new Double[ classnum ] ; 
		for(int i =0;i < pointset.length ; i ++ )
		{
			pw[i] = ((double)pointset[i].size()) / trainnum.size() ;
			h1 += -pw[i] * Math.log(pw[i]) ;
		}
		
		int[] csum = new int[ classnum ] ;
		int total = 0 ;
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
				total ++ ;
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
				Double p = ((double)csum[i]) / pointset[i].size() ;
//				System.out.println( "p " + p);
				if( p != 0 )
					h += p * Math.log( p/(pw[i]*pc[j] ) ) ;
//				System.out.println( "h " + h );
			}
		}
		
		System.out.println( "h1  " + h1 ) ;
		System.out.println( "h2  " + h2 ) ;
		System.out.println( "h  " + h ) ;
		

		return h / 2 /( h1 + h2 );
	}
	
	
}
