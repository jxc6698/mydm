package dataMing;

import java.util.ArrayList;
import java.util.List;

public class readData {
	List line[] = null ;
	readData( int n )
	{
		if( n == 0 )
			return ;
		
		this.line = new List[ n ] ;
		for( int i = 0 ; i < n ; i ++)
			line[i] = new ArrayList() ;
	}
	void setnum( int n )
	{
		this.line = new List[ n ] ;
		for( int i = 0 ; i < n ; i ++)
			line[i] = new ArrayList() ;
	}
	void append( int i , String content )
	{
		line[i].add( content ) ;
	}
	
}