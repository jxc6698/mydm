package dataMing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;


public class inputClass{
	String path = null ;
	File files[] = null ;
	readData readdata = null ;
	
	inputClass( String name )
	{
		readdata = new readData( 0 ) ;
		this.path = name ;
	}
	
	void getAllFiles()
	{
		this.files = new File(this.path).listFiles() ;
	}
	
	readData readAllFiles()
	{
		Scanner input = null ;
		readdata.setnum( files.length );
		for( int i = 0 ; i < files.length ; i ++ )
		{
			readdata.setfilename( i , files[i].getName() );
			try {
				input = new Scanner( files[i] ) ;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while( input.hasNextLine() )
			{
				readdata.append( i , input.nextLine() ) ;
			}
		}
		
		
		try {
			input = new Scanner( new File("stopwords.txt") ) ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( readdata.stopwords == null )
		{
			readdata.stopwords = new HashSet<String>() ;
		}
		while( input.hasNextLine() )
		{
//			System.out.println( input.nextLine() );
			readdata.stopwords.add( input.nextLine() ) ;
		}
		
		
		return readdata ;
	}
	
	String[] shownames()
	{
		String[] names = new String[ this.files.length ] ;
		for( int i = 0 ; i < files.length ; i ++ )
		{
			names[i] = new String( files[i].getName() ) ;
		}
		return names ;
	}
	
	void clearFiles() 
	{
		return ;
	}
	
	
	void debug_filenames()
	{
		String[] names = this.shownames() ;
		for( int i = 0 ; i < names.length ; i ++)
			System.out.println( names[i] ) ;
	}

	void debug_content()
	{
		for( int i = 0 ; i < readdata.line.length ;i ++ )
		{
			int num = readdata.line[i].size() ;
			for( int j = 0 ; j < num ; j ++ )
			{
				System.out.println( (String) readdata.line[i].get(j) ) ; 
			}
		}
	}
}


