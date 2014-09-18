package dataMing;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class run {
	public static void main(String[] args )
	{

		String Dir = "lily" ;
		preProcessData preprocessdata = new preProcessData() ;
		
		inputClass inputclass = new inputClass( Dir ) ;
		inputclass.getAllFiles();

		readData readdata = inputclass.readAllFiles() ;

		
	}
	
	
	

	
	
}



