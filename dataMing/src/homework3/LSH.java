package homework3;

import java.util.ArrayList;
import java.util.List;

import weka.core.Debug.Random;

public class LSH {
	
	
	List<List<Double> > vec = new ArrayList() ; // new ArrayList<Double>() ; 
	List<Double> ssum = new ArrayList<Double>() ;
	/*
	 * num : feature number
	 * n   : hash vec number, also the hash function number
	 */
	void init(  int num , int n )
	{
		Random r = new Random() ;
		Double ss = null , tmp = null ;
		for(int i=0;i < n ; i ++)
		{
			vec.add( new ArrayList<Double>()  ) ;
			ss = new Double(0) ;
			for( int j=0 ; j < num ; j ++)
			{
				tmp = (double) (r.nextInt(2) - 1) ;
				vec.get(i).add( tmp ) ;
				ss += ( tmp * tmp ) ;
			}
			ssum.add( Math.sqrt( ss ) ) ;
		}
	}
	
	void lshhashset( List<List<Double>> set , 
			List<Integer> num , 
			List<List<Integer>> hashset )
	{
		Double tmp = null , tmp1 = null  ;
		for(int i =0;i < Math.pow(2, vec.size() ) ; i ++)
		{
			hashset.add( new ArrayList<Integer>() ) ;
		}
		for( int i = 0 ; i < set.size() ; i ++ )
		{
			int c = 0 ;
			for( int k = 0 ; k < vec.size() ; k ++ )
			{
				tmp = new Double(0) ; tmp1 = new Double(0) ;
				for(int j = 0 ; j < set.get(i).size() ; j ++ )
				{
					tmp += set.get(i).get(j) * set.get(i).get(j) ;
					tmp1 += set.get(i).get(j) * vec.get(k).get(j) ;
				}
				
				c=c*2 ;
//				System.out.println( Math.acos( tmp1 / ( Math.sqrt( tmp ) * ssum.get(k) ) ) ) ;
//				System.out.println( tmp ) ;
//				System.out.println(tmp1 ) ;
//				System.exit(0);
				if( Math.acos( tmp1 / ( Math.sqrt( tmp ) * ssum.get(k) ) ) < Math.PI / 2 )
				{
					c++ ;
				}
				else
				{
					;
				}
			}
			hashset.get(c).add(i) ;
		}
		return ;
	}
	
	int lshhash( List<Double> set )
	{
		Double tmp = null , tmp1 = null  ;
		int c = 0 ;
		for( int k = 0 ; k < vec.size() ; k ++ )
		{
			tmp = new Double(0) ; tmp1 = new Double(0) ;
			for(int j = 0 ; j < set.size() ; j ++ )
			{
				tmp += set.get(j) * set.get(j) ;
				tmp1 += set.get(j) * vec.get(k).get(j) ;
			}
			
			c=c*2 ;
			if( Math.acos( tmp1 / ( Math.sqrt( tmp ) * ssum.get(k) ) ) < Math.PI / 2 )
			{
				c++ ;
			}
			else
			{
				;
			}
		}
		return c ;
	}
	
}
