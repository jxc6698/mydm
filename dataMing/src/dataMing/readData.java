package dataMing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class readData {
	List line[] = null ;
	String  filenames[] = null ;
	
	Set<String> stopwords = null ;
	
	public readData( int n )
	{
		if( n == 0 )
			return ;
		
		this.line = new List[ n ] ;
		this.filenames = new String[n] ;
		for( int i = 0 ; i < n ; i ++)
		{
			line[i] = new ArrayList() ;
		}
		
		if( this.stopwords == null)
			this.stopwords = new HashSet<String>() ;
		else
			System.out.println("sssssss");
	}
	void setnum( int n )
	{
		this.line = new List[ n ] ;
		this.filenames = new String[n] ;
		for( int i = 0 ; i < n ; i ++)
			line[i] = new ArrayList() ;
	}
	
	void setfilename( int n , String filename )
	{
		this.filenames[n] = new String( filename ) ;
	}
	
	void append( int i , String content )
	{
		line[i].add( content ) ;
	}
	
}