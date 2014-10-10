package homework2;

import java.util.ArrayList;
import java.util.List;

import dataMing.globalconfig;
import weka.core.Matrix;

public class gradientDescent {
	
	
	int featurenumber = 0 ;
	Double beita[] = null ; 
	Double partiald[] = null ;
	Double mu = new Double( 0.1  ) ;
	Double m = new Double( 0 ) ;
	Double maxb = new Double(  1 ) ;
	
	// beita init to 0 
	void init( int featurenum )
	{
		featurenumber = featurenum ;
		beita = new Double[featurenumber+1] ;
		partiald = new Double[featurenumber+1] ;
		for( int i = 0 ; i <= featurenumber ; i ++ )
		{
			beita[i] = new Double(0) ;
			partiald[i] = new Double(0) ;
		}
	}
	
	Double getp( List<Double> data , int c )
	{
		Double tmp = new Double( beita[0] );
		for( int i = 0 ; i < data.size() ; i ++ )
		{
			tmp += beita[i+1] * data.get(i) ;
		}
		Double exp = Math.exp( tmp ) ; 
		if( exp > 100000 )
		{
			//if(globalconfig.debug) System.out.println("+++++++ ") ;
		}
	//	System.out.println("----  " + exp ) ;
		if( exp > 100000 )
			if( c == 1 )
				return  1.0 ; // ( exp / (1+exp ) ) ;
			else
				return 0.0 ; //( 1 / ( 1+exp ) ) ;
		if( c == 1 )
			return ( exp / (1+exp ) ) ;
		else
			return ( 1 / ( 1+exp ) ) ;
	}
	
	// class1 mean 0 
	// class2 mean 1
	void trainclassifier( List< List<Double>> class1 ,
			List< List<Double>> class2 )
	{
		List<Double> l1 = null ;
		Double tmp = new Double( 0 ) ;
		int count = 0 ;
		int s1 = class1.size() , s2= class2.size() ;
		int total = class1.size() + class2.size() ;
		while( true )
		{
			boolean bool = true ;
			int cc = 0 ;
			
			for(int i = 0 ; i < class1.size() ; i ++ )
			{
				
			for( int j = 0 ; j < partiald.length ;j ++ )
				partiald[j] = 0.0 ;
				l1 = class1.get(i) ;
				tmp = this.getp(l1 ,  0) ;
				
				
				partiald[0] += 1 * ( 0 - tmp ) ;
				for( int j = 0 ; j < featurenumber ; j ++ )
					partiald[j+1] += l1.get(j) * ( 0 - tmp ) ;
				
				bool =true ;
				for( int j = 0 ; j <= featurenumber ; j ++ )
					if( partiald[j] > maxb )
						bool = false ;
				if( bool )
					cc ++ ;
				for( int j = 0 ; j <= featurenumber ; j ++ )
					beita[j] += mu * partiald[j] ;
			}

			
	
			for( int i = 0 ; i < class2.size() ; i ++ )
			{
				
		for( int j = 0 ; j < partiald.length ;j ++ )
				partiald[j] = 0.0 ;
				l1 = class2.get(i) ;
				tmp = this.getp(l1 ,  1) ;
				
				partiald[0] += 1 * ( 1 - tmp ) ;
				for( int j = 0 ; j < featurenumber ; j ++ )
					partiald[j+1] += l1.get(j) * ( 1 - tmp ) ;
				
				bool =true ;
				for( int j = 0 ; j <= featurenumber ; j ++ )
					if( partiald[j] > maxb )
						bool = false ;
				if( bool )
					cc++ ;
				for( int j = 0 ; j <= featurenumber ; j ++ )
					beita[j] += mu * partiald[j] ;
			}

//			System.out.println(count + "   " + cc) ;
			count ++ ;
			if( count > 10 )
				return ;
		}
	}
	
	List estimate( List<List<Double>> data )
	{
	//	List<Integer> result= new ArrayList<Integer>() ;
		List<Double> result= new ArrayList<Double>() ;
		Double tmp = new Double(0) ;
		List<Double> t = null ;
		for(int i=0;i<data.size(); i ++ )
		{
			t = data.get(i) ;
			tmp = new Double(0);
			tmp += 1 * beita[0] ;
			for( int j = 0 ; j < featurenumber ; j ++ )
			{
				tmp += beita[j+1] * t.get(j) ; 
			}
//			System.out.println(tmp);
//			if( tmp > 0.5 )
//				result.add(1);
//			else
//				result.add(0);
			result.add(tmp) ;
		}
		return result ;
	}
	
}
