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

		globalconfig.whether_cross_check = true ;
		globalconfig.tfbool = true ;
		globalconfig.nlp_ngram = false;
		
		String Dir = "lily" ;
		
		inputClass inputclass = new inputClass( Dir ) ;
		inputclass.getAllFiles();

		readData readdata = inputclass.readAllFiles() ;
		
		preProcessData preprocessdata = new preProcessData( readdata ) ;
		
		preprocessdata.nbd_start();
		preprocessdata.nbcd_start() ;
		preprocessdata.nbcg_start() ;
		
	}
	
	

}



