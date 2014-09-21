package dataMing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NaiveBayes {
	
	/*
	 * class num
	 * feature num
	 * feature value
	 * frenquency of the value
	 */
	
	
	// number of each class
	Integer number[] ;
	
	// array index is class id , feature id
	// feature 1 , feature 2 ......   feature max
	// key is feature value , value is frequency
	Map<Double, Integer> classifier[][] = null ;
	
	void clear()
	{
		number = null ;
		classifier = null ;
	}
	
	void train( List< List<Double>> traindata , List<Integer> num , int classnum , int featurenum )
	{
		
		if( traindata.size() == 0 )
			return ;
		classifier = new HashMap[classnum][] ;
		for( int i =0;i<classnum ; i ++)
			classifier[i] = new HashMap[ featurenum ] ;
		this.number = new Integer[ classnum ] ;
		for(int i =0; i < number.length ; i ++)
			number[i] = new Integer(0) ;
		
		for( int i =0 ; i <num.size() ; i ++ )
		{
			number[ num.get(i) ] ++ ;
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				if( classifier[ num.get(i) ][j] == null )
					classifier[ num.get(i) ][j] = new HashMap<Double,Integer>() ;
				
				if( classifier[ num.get(i) ][j].get( traindata.get(i).get( j ) ) == null )
					classifier[ num.get(i) ][j].put( traindata.get(i).get( j ) , 1 ) ;
				else
					classifier[ num.get(i) ][j].put( traindata.get(i).get( j ) , classifier[ num.get(i) ][j].get( traindata.get(i).get( j ) ) + 1 ) ;
			}
				
		}

	} 
	
	Integer[] estimate( List< List<Double>> esdata , int featurenum )
	{
		Integer result[] = new Integer[ esdata.size() ] ;
		Double probability[] = new Double[ this.number.length] ;
		for(int i =0; i < probability.length ; i++)
			probability[i] = new Double(1) ;
		for( int i = 0 ; i < esdata.size() ; i ++ )
		{
			// j is feature id 
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				// k is class id
				for( int k = 0 ; k < this.number.length ; k ++ )
				{
					if( classifier[ k ][j].get( esdata.get(i).get(j) ) == null )
					{
						probability[k] = 0.0 ;
						break ;
					}
					else
					{
						// in here shouble be  *= ( * number[k] / number[k] ) ;
						probability[k] *= ( ( new Double(classifier[ k ][j].get( esdata.get(i).get(j) ) ) ) ) ;
					}
				}
			}
			
			Double max = null ;
			int maxn = -1 ;
			for( int j= 0 ; j < probability.length ; j ++ )
			{
				if( maxn == -1 )
				{
					maxn = 0 ;
					max = probability[0] ;
				}
				else if( probability[j] > max )
				{
					maxn = j ;
					max = probability[j] ;
				}
			}
			result[i] = maxn ;
		}

		return result ;
	}
	
	
	
	
	
	
	
	
	/*
	 * output the classifer out the file whose name is by filename
	 * argument 
	 */
	void outputclassifier( String filename )
	{
		return ;
	}
	
	
	
	
}
