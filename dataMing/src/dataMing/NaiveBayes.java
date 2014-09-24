package dataMing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NaiveBayes {
	
	
	int featurenumber ;
	int classnumber ;
	
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
	Map<Double, Double> classifier[][] = null ;
	Double[][] total = null ;
	
	void clear()
	{
		number = null ;
		classifier = null ;
		this.featurenumber = 0 ;
		classnumber = 0 ;
	}
	
	void train( List< List<Double>> traindata , List<Integer> num , int classnum , int featurenum )
	{
		if( traindata.size() == 0 )
			return ;
		this.featurenumber = featurenum ;
		this.classnumber = classnum ;
		if( classifier == null )
		{
			classifier = new HashMap[classnum][] ;
			for( int i =0;i<classnum ; i ++)
				classifier[i] = new HashMap[ featurenum ] ;
			this.number = new Integer[ classnum ] ;
			
			for(int i =0; i < number.length ; i ++)
				number[i] = new Integer(0) ;
			total = new Double[classnum][];
			for( int i = 0 ; i < classnum ; i ++ )
			{
				total[i] = new Double[featurenum] ;
				for( int j = 0 ; j < featurenum ; j ++ )
					total[i][j] = new Double(0) ;
			}
			
			
		}
		for( int i =0 ; i <num.size() ; i ++ )
		{
			number[ num.get(i) ] ++ ;
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				if( classifier[ num.get(i) ][j] == null )
					classifier[ num.get(i) ][j] = new HashMap<Double,Double>() ;
				
				if( classifier[ num.get(i) ][j].get( traindata.get(i).get( j ) ) == null )
				{
					classifier[ num.get(i) ][j].put( traindata.get(i).get( j ) , 1.0 ) ;
				}
				else
				{
					classifier[ num.get(i) ][j].put( traindata.get(i).get( j ) , classifier[ num.get(i) ][j].get( traindata.get(i).get( j ) ) + 1 ) ;
				}
				total[num.get(i)][j] += 1 ;
			}	
		}

	} 
	
	Integer[] estimate( List< List<Double>> esdata ,
			int featurenum )
	{
		double delta = 0.05 ;
		Integer result[] = new Integer[ esdata.size() ] ;
		Double probability[] = new Double[ this.number.length] ;
		for(int i =0; i < probability.length ; i++)
			probability[i] = new Double(1) ;
		for( int i = 0 ; i < esdata.size() ; i ++ )
		{
			for(int k =0; k < probability.length ; k++)
				probability[k] = new Double(1);
			// j is feature id 
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				// k is class id
				for( int k = 0 ; k < this.number.length ; k ++ )
				{
					if( classifier[ k ][j].get( esdata.get(i).get(j) ) == null )
					{
						probability[k] *= delta / ( total[k][j] + classifier[ k ][j].size() * delta ) ;
					//	break ;
					}
					else
					{
						// in here shouble be  *= ( * number[k] / number[k] ) ;
					//	probability[k] *= new Double(classifier[ k ][j].get( esdata.get(i).get(j) ) ) ;
						probability[k] *= (delta +  new Double(classifier[ k ][j].get( esdata.get(i).get(j) ) ) ) /( total[k][j] + classifier[ k ][j].size() *delta ) ;
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
	
	
	
	
	//  tfidf  method
	
	List<Double>[] gTotal = null ;
	List<Double>[] gsquare = null ;
	Integer[] gnum = null ;
	
	List<Double>[] gmean = null ;
	List<Double>[] gVariance = null ;
	
	void gclear()
	{
		gTotal = null ;
		gsquare = null ;
		gnum = null ;
		gmean = null ;
		gVariance = null ;
	}
	
	
	
	void gtrain( List< List<Double>> traindata , List<Integer> num , int classnum , int featurenum )
	{
		this.featurenumber = featurenum ;
		this.classnumber = classnum ;
		if( gTotal == null )
		{
			gnum = new Integer[classnum] ;
			gTotal = new ArrayList[classnum ] ;
			gsquare = new ArrayList[classnum ] ;
			for( int i=0;i<classnum; i ++)
			{
				gnum[i] = new Integer(0) ;
				gTotal[i] = new ArrayList() ;
				gsquare[i] = new ArrayList() ;
				for( int j = 0 ; j < featurenum ; j ++ )
				{
					gTotal[i].add( new Double(0) ) ;
					gsquare[i].add( new Double(0) ) ;
				}
			}
		}
		
		for( int i =0 ; i < num.size() ; i ++ )
		{
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				Double tmp = gTotal[num.get(i)].get(j) ;
			//	System.out.println( gTotal[num.get(i)].get(j) );
				tmp += traindata.get(i).get( j ) ;
				gTotal[num.get(i)].set(j, tmp ) ;
			//	System.out.println( gTotal[num.get(i)].get(j) );
				tmp = gsquare[num.get(i)].get(j) ;
				tmp += traindata.get(i).get( j ) * traindata.get(i).get( j ) ;
				gsquare[num.get(i)].set(j, tmp) ;
//				if( traindata.get(i).get( j ) != 0.0 )
					gnum[num.get(i)] ++ ;
			}
		}
		
	}
	
	void gendtrain()
	{
		int classnum = gTotal.length ;
		int featurenum = gTotal[0].size() ;
		gmean = new ArrayList[classnum ] ;
		gVariance = new ArrayList[classnum ] ;
		for( int i=0;i<classnum; i ++)
		{
			gmean[i] = new ArrayList() ;
			gVariance[i] = new ArrayList() ;
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				gmean[i].add( new Double( gTotal[i].get(j) / gnum[i] ) ) ;
				gVariance[i].add( new Double( ( gsquare[i].get(j) ) / gnum[i] - gmean[i].get(j) * gmean[i].get(j) ) ) ;
			}
		}
	}
	
	
	
	Integer[] gestimate( List< List<Double>> esdata , int featurenum )
	{
		Integer result[] = new Integer[ esdata.size() ] ;
		Double probability[] = new Double[ this.classnumber ] ;
		for( int i = 0 ; i < esdata.size() ; i ++ )
		{
			for(int k =0; k < probability.length ; k++)
				probability[k] = new Double(1);
			// j is feature id 
			for( int j = 0 ; j < featurenum ; j ++ )
			{
				// k is class id
				for( int k = 0 ; k < this.classnumber ; k ++ )
				{
					// in here shouble be  *= ( * number[k] / number[k] ) ;
					Double d = Math.sqrt( gVariance[k].get(j) ) ;
					Double m = esdata.get(i).get(j) - gmean[k].get(j) ;
					d += 0.00000001 ;	
					m += 0.00000001 ;
					probability[k] +=( ( -1 * ( m*m ) / (2*d*d) ) - Math.log( d ) ) ;
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
