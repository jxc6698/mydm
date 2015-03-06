package testpackage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class statistics {
	public static void main(String argv[])
	{
	

		try {

			stat_tf();
//			stat_compact();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new Date());
		return;
	}
	
	
	static public void stat_compact() throws FileNotFoundException
	{
		System.out.println("statistics output file 0/1 test");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/submission.csv"));
		String head = null, line = null;
		
		
		out = new FileOutputStream("../kaggle/compacksubmission.csv");
		p = new PrintStream(out);
		
		System.out.println("part 1");

		int ttt = 0,tttT=0,tttF=0;

		p.println( input.next() );
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			
			double tt = Double.parseDouble(word[1]);
			if( tt > 0.99)
				tttT++;
			else
				tttF++;
			ttt++;
			if(tt > 0.90)
				p.println( line );
			else
				p.println(word[0]+","+ 0.01);
		}
		System.out.println("T : "+((double)tttT)/ttt  );
		
		input.close();
		p.close();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void stat_tf() throws FileNotFoundException
	{
		System.out.println("statistics output file 0/1 test");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/submission.csv"));
		String head = null, line = null;
		
		
		System.out.println("part 1");

		int ttt = 0,tttT=0,tttF=0;

		input.next();
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			
			double tt = Double.parseDouble(word[1]);
			if( tt > 0.5)
				tttT++;
			else
				tttF++;
			ttt++;
		}
		System.out.println("T : "+((double)tttT)/ttt  );
		
		input.close();
	}
}
