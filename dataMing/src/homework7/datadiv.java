package homework7;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class datadiv {
	/* weathre keep class name line */
	static boolean weather_class_name = false;

	static int start=0,end=23 ;
	static Map<String,Integer> stat[] = new HashMap[30];
//	wrong stat
	static Map<String,Integer> wstat[] = new HashMap[30];
	static Map<String,Integer> allstat[] = new HashMap[30];
	
//  click rate of each id,dev_id ... stat 
	static Map<String,Double> ratestat[] = new HashMap[30];
	
//	gbdt 
//	40428967
	static int samplenumber = 40428967;  
	static int samplenumberT = 40000000;
	static int samplenumberF = 40000000;
	
	public static void main(String argv[])
	{


		System.out.println(new Date());
		for(int i=start; i<end+1 ;i++) {
			stat[i] = new HashMap();
			wstat[i] = new HashMap();
			allstat[i] = new HashMap();
		}
		
		try {

//			gbdttest();
			
//			reformat();
//			calRate();
//			averagerate();
//			gbdtFeature();
			
			// one-hot feature for all, appearance more than one million
//			reformatforgbdt();
			gbdtfeature_id_c();
			
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
	

	public static void gbdttest() throws IOException 
	{
		
		int testsamplenumber = 200000,testsamplenumberT = 100000,testsamplenumberF = 100000;
		System.out.println("gdbt feature test");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/train_data2"));
		String head = null, line = null;
		
		System.out.println("part 1");
		double result[] = new double[testsamplenumber];
//		List<double[]> dataarray = new ArrayList();
		int ttt = 0,tttT=0,tttF=0;
		short dataarray[][] = new short[12][];
		for(int i=0;i<dataarray.length;i++)
			dataarray[i] = new short[testsamplenumber];
		// need init to make memory alloc tegether how much time to init 
		
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			result[ttt] = word[0].equals("1")?1.0:0;

			if(result[ttt] == 1.0 && tttT<testsamplenumberT ) {
				tttT++;
				ttt ++;
				if( ttt % 400000 == 0)
					System.out.println("already : "+ttt/4000000);
			}
			else if(result[ttt] == 0.0 && tttF<testsamplenumberF) {
				tttF++;
				ttt ++;
				if( ttt % 400000 == 0)
					System.out.println("already : "+ttt/4000000);
			} else
				continue;

			for( int i=1;i < word.length;i++)
				dataarray[i-1][ttt-1] = Short.parseShort(word[i-1]);
			if( ttt == testsamplenumber )
				break;
			
		}
		input.close();
		
		
		System.out.println("part 2");
		gradientboost maintree = new gradientboost() ; 
		maintree.train(dataarray, null, result, 2 , 7, testsamplenumber );
		int maxnode = 1;
		for(int i=0; i<7 ;i++)
			maxnode *= 2;
//		dataarray=null;
		
		System.out.println("part 3 "+new Date());
		double num[] = new double[testsamplenumber];
		maintree.estimate(dataarray, null, num);
		
		int accu = 0;
		int accut =0,accuf =0;
		int acc=0;
		int fnum=0;
		for(int i =0;i<num.length;i++) {
			if( num[i] == result[i] ) {
				accu ++;
				if(num[i] == 1.0)
					accut ++;
				else
					accuf ++;
			}
			if( num[i] == 0)
				acc++;
			if( result[i] == 0)
				fnum ++;
		}
		System.out.println("arrc is "+ accu);
		System.out.println("accut "+accut + "  accuf "+accuf);
		System.out.println("acc  "+acc);
		System.out.println("fnum  " + fnum);
	}
	
	/*
	 * to train_data5, test_data5
	 */
	public static void gbdtfeature_id_c() throws IOException 
	{
		System.out.println("gdbt one-hot id and probability feature");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/train_data2"));
		Scanner input1 = new Scanner(new File("../kaggle/train_data4"));
		String head = null, line = null;
		
		System.out.println("part 1");
		double result[] = new double[samplenumber];
//		List<double[]> dataarray = new ArrayList();
		int ttt = 0,tttT=0,tttF=0;
		short dataarray[][] = new short[12][];
		for(int i=0;i<dataarray.length;i++)
			dataarray[i] = new short[samplenumber];
		// need init to make memory alloc tegether how much time to init 
		
		Map<Integer,Integer> insindex = new HashMap();
		int tcount[] = new int[100];
		PrintStream tp[] = new PrintStream[100];
		for(int i=0;i<tcount.length;i++)
			tcount[i] = 0;
		for(int i=0;i<tp.length;i++) {
			out = new FileOutputStream("tindex"+i);
			tp[i] = new PrintStream(out);
		}
		dataarray=null;
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			line = input1.next();

			result[ttt] = word[0].equals("1")?1.0:0;

			if(result[ttt] == 1.0 && tttT<samplenumberT ) {
				tttT++;
				ttt ++;
			}else if(result[ttt] == 0.0 && tttF<samplenumberF) {
				tttF++;
				ttt ++;
			}else
				continue;
			if( dataarray != null) {
				for( int i=1;i < word.length;i++)
					dataarray[i-1][ttt-1] = Short.parseShort(word[i]);
			}

			// handle sparse feature
			word = line.split(",");
			for( int i=0;i<word.length;i++) {
				int t = Integer.parseInt(word[i]);
				int tt = -1;
				if( insindex.containsKey(t) )
					tt = insindex.get(t);
				else {
					tt = insindex.size();
					insindex.put(t, insindex.size() );
//					System.out.println(t);
				}
				tcount[tt]++;
				tp[tt].println(ttt-1);
			}
			
			if( ttt == samplenumber )
				break;
		}
		input.close(); input1.close();
		for(int i=0;i<tp.length;i++)
			tp[i].close();
		
		int fsparse[][] = new int[ insindex.size() ][];
		for(int i=0;i<fsparse.length;i++) {
			fsparse[i] = new int[tcount[i]];
			input = new Scanner(new File("tindex"+i));
			for(int j=0;j<fsparse[i].length;j++) {
				fsparse[i][j] = Integer.parseInt(input.next());
			}
		}
		tcount = null;
		
		System.out.println("part 2 "+ new Date());
		gradientboost maintree = new gradientboost();
		
		maintree.train( dataarray, fsparse, result, 2 , offsetConfig.treedepth, samplenumber );
		int maxnode = 1;
		for(int i=0; i<offsetConfig.treedepth ;i++)
			maxnode *= 2;
		dataarray=null;
		result = null;
		tcount = null;
		
		System.out.println("part 3  "+new Date());
		input = new Scanner(new File("../kaggle/train_data2"));
		input1 = new Scanner(new File("../kaggle/train_data4"));
		out = new FileOutputStream("../kaggle/train_data5");
		p = new PrintStream(out);

		dataarray = new short[12][];
		for(int i=0;i<dataarray.length;i++)
			dataarray[i] = new short[1];
		int[][] gbdtfeature = new int[1][];
		for(int i=0;i<fsparse.length;i++)
			fsparse[i] = new int[1];
		
		dataarray=null;
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			if( dataarray != null) {
				for( int i=1;i < word.length;i++)
					dataarray[i-1][0] = Short.parseShort(word[i]);
			}
			
			// handle sparse feature
			line = input1.next();
			for(int i=0;i<fsparse.length;i++)
				fsparse[i][0] = -1;
			word = line.split(",");
			for( int i=0;i<word.length;i++) {
				int t = Integer.parseInt(word[i]);
				int tt = -1;
				if( insindex.containsKey(t) )
					tt = insindex.get(t);
				else
					System.out.println("error: new feature index");
				fsparse[tt][0] = 0;
			}
			
			//dataarray
			maintree.estimatefeature(dataarray, fsparse, gbdtfeature);
			for(int i=0;i< gbdtfeature[0].length;i++) {
				if(i ==0)
					p.printf("%d", gbdtfeature[0][i] + i*maxnode);
				else
					p.printf(",%d", gbdtfeature[0][i] + i*maxnode);
			}
			p.println();;
		}
		input.close(); input1.close();
		p.close(); out.close();
		
		
		
		System.out.println("part 4  new test data "+new Date());
		input = new Scanner(new File("../kaggle/test_data2"));
		input1 = new Scanner(new File("../kaggle/test_data4"));
		out = new FileOutputStream("../kaggle/test_data5");
		p = new PrintStream(out);
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			if( dataarray != null) {
				for( int i=0;i < word.length;i++)
					dataarray[i][0] = Short.parseShort(word[i]);
			}
			
			// handle sparse feature
			line = input1.next();
			for(int i=0;i<fsparse.length;i++)
				fsparse[i][0] = -1;
			word = line.split(",");
			for( int i=0;i<word.length;i++) {
				int t = Integer.parseInt(word[i]);
				int tt = -1;
				if( insindex.containsKey(t) )
					tt = insindex.get(t);
				else
					System.out.println("error: new feature index");
				fsparse[tt][0] = 0;
			}
			
			maintree.estimatefeature(dataarray, fsparse, gbdtfeature);
			for(int i=0;i< gbdtfeature[0].length;i++) {
				if(i ==0)
					p.printf("%d", gbdtfeature[0][i] + i*maxnode);
				else
					p.printf(",%d", gbdtfeature[0][i] + i*maxnode);
			}
			p.println();
		}
		input.close(); input1.close();
		p.close(); out.close();
	
		return;
	}
	
	
	public static void gbdtFeature() throws IOException 
	{
		System.out.println("gdbt probability feature");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/train_data2"));
		String head = null, line = null;
		
		System.out.println("part 1");
		double result[] = new double[samplenumber];
//		List<double[]> dataarray = new ArrayList();
		int ttt = 0,tttT=0,tttF=0;
		short dataarray[][] = new short[7][];
		for(int i=0;i<dataarray.length;i++)
			dataarray[i] = new short[samplenumber];
		// need init to make memory alloc tegether how much time to init 
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			result[ttt] = word[0].equals("1")?1.0:0;

			if(result[ttt] == 1.0 && tttT<samplenumberT ) {
				tttT++;
				ttt ++;
			}else if(result[ttt] == 0.0 && tttF<samplenumberF) {
				tttF++;
				ttt ++;
			}else
				continue;

//			for( int i=1;i < word.length;i++)
//				dataarray[i-1][ttt-1] = Short.parseShort(word[i]);
			dataarray[0][ttt-1] = Short.parseShort(word[1]);
			dataarray[1][ttt-1] = Short.parseShort(word[4]);
			dataarray[2][ttt-1] = Short.parseShort(word[6]);
			dataarray[3][ttt-1] = Short.parseShort(word[7]);
			dataarray[4][ttt-1] = Short.parseShort(word[11]);
			dataarray[5][ttt-1] = Short.parseShort(word[12]);
			dataarray[6][ttt-1] = Short.parseShort(word[10]);
			
			if( ttt == samplenumber )
				break;
		}
		input.close();
		
		System.out.println("part 2 "+ new Date());
		gradientboost maintree = new gradientboost() ;
		maintree.train(dataarray,null, result, 2 , offsetConfig.treedepth, samplenumber );
		int maxnode = 1;
		for(int i=0; i<offsetConfig.treedepth ;i++)
			maxnode *= 2;
		dataarray=null;
		result = null;
		
		System.out.println("part 3  "+new Date());
		input = new Scanner(new File("../kaggle/train_data2"));
		out = new FileOutputStream("../kaggle/train_data3");
		p = new PrintStream(out);

		dataarray = new short[7][];
		for(int i=0;i<dataarray.length;i++)
			dataarray[i] = new short[1];
		int[][] gbdtfeature = new int[1][];
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
//			for( int i=1;i < word.length;i++)
//				dataarray[i-1][0] = Short.parseShort(word[i]);

			dataarray[0][0] = Short.parseShort(word[1]);
			dataarray[1][0] = Short.parseShort(word[4]);
			dataarray[2][0] = Short.parseShort(word[6]);
			dataarray[3][0] = Short.parseShort(word[7]);
			dataarray[4][0] = Short.parseShort(word[11]);
			dataarray[5][0] = Short.parseShort(word[12]);
			dataarray[6][0] = Short.parseShort(word[10]);
			
			maintree.estimatefeature( dataarray, null, gbdtfeature);
			for(int i=0;i< gbdtfeature[0].length;i++) {
				if(i ==0)
					p.printf("%d", gbdtfeature[0][i] + i*maxnode);
				else
					p.printf(",%d", gbdtfeature[0][i] + i*maxnode);
			}
			p.println();;
		}
		input.close();
		p.close();
		out.close();
		
		System.out.println("part 4  "+new Date());
		input = new Scanner(new File("../kaggle/test_data2"));
		out = new FileOutputStream("../kaggle/test_data3");
		p = new PrintStream(out);
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
//			for( int i=0;i < word.length;i++)
//				dataarray[i][0] = Short.parseShort(word[i]);

			dataarray[0][0] = Short.parseShort(word[0]);
			dataarray[1][0] = Short.parseShort(word[3]);
			dataarray[2][0] = Short.parseShort(word[5]);
			dataarray[3][0] = Short.parseShort(word[6]);
			dataarray[4][0] = Short.parseShort(word[10]);
			dataarray[5][0] = Short.parseShort(word[11]);
			dataarray[6][0] = Short.parseShort(word[9]);
			
			maintree.estimatefeature( dataarray, null, gbdtfeature);
			for(int i=0;i< gbdtfeature[0].length;i++) {
				if(i ==0)
					p.printf("%d", gbdtfeature[0][i] + i*maxnode);
				else
					p.printf(",%d", gbdtfeature[0][i] + i*maxnode);
			}
			p.println();
		}
		input.close();
		p.close();
		out.close();
	
		return;
	}
	

	public static void averagerate() throws FileNotFoundException 
	{
		System.out.println("output rate");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/train_data1"));
		String head = null, line = null;
		
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=4; i<16 ;i++) {
				if(word[1].equals("1")) {
					if(stat[i].containsKey(word[i]))
						stat[i].put(word[i],stat[i].get(word[i]) + 1 );
					else
						stat[i].put(word[i], 1);
				} else {
					if(wstat[i].containsKey(word[i]))
						wstat[i].put(word[i],wstat[i].get(word[i]) + 1 );
					else
						wstat[i].put(word[i], 1);
				}
			}
		}
		input.close();
		
		
		double average[] = new double[30];
		input = new Scanner(new File("../kaggle/train_data1"));
		int count =0;
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			count ++;
			for(int i=4; i<16 ;i++) {

				String tword = word[i];
				int right=0,wrong=0;
				if(stat[i].containsKey(tword))
					right=stat[i].get(word[i]);
				if(wstat[i].containsKey(tword))
					wrong=wstat[i].get(word[i]);
				double rate = 0;//offsetConfig.DEFAULTRATE;
				if(right+wrong!=0)
					rate = ((double)right)/(right+wrong);
				else
					System.err.println("error 1");
				average[i] += rate;
			}
		}

		
		for(int i=4;i<16;i++)
			average[i] /= count;
		
		for(int i=4;i<16;i++)
			System.out.println(average[i]);
	}
	
	public static void calRate() throws IOException
	{
		
		System.out.println("calRate");
		
		FileOutputStream out = null;
		PrintStream p = null;
		Scanner input = new Scanner(new File("../kaggle/train_data1"));
		String head = null, line = null;
		String[] column = null;
		
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=4; i<16 ;i++) {
				if(word[1].equals("1")) {
					if(stat[i].containsKey(word[i]))
						stat[i].put(word[i],stat[i].get(word[i]) + 1 );
					else
						stat[i].put(word[i], 1);
				} else {
					if(wstat[i].containsKey(word[i]))
						wstat[i].put(word[i],wstat[i].get(word[i]) + 1 );
					else
						wstat[i].put(word[i], 1);
				}
			}
		}
		input.close();
		
		
		offsetConfig.Boffset2[0] = 0;
		for(int i=1;i<offsetConfig.Boff2.length;i++) {
			offsetConfig.Boffset2[i] = offsetConfig.Boffset2[i-1] + offsetConfig.Boff2[i-1];
		}
		
		System.out.println("part2");
		input = new Scanner(new File("../kaggle/train_data1"));		
		out = new FileOutputStream("../kaggle/train_data2");
		p = new PrintStream(out);
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			
			p.print(word[1]);
			for(int i=4; i<16 ;i++) {
				String tword = word[i];
				int right=0,wrong=0;
				if(stat[i].containsKey(tword))
					right=stat[i].get(word[i]);
				if(wstat[i].containsKey(tword))
					wrong=wstat[i].get(word[i]);
				double rate = offsetConfig.DEFAULTRATE;
				if(right+wrong!=0)
					rate = ((double)right)/(right+wrong);
				
				short st = (short) (rate * 10000);
				p.printf(",%d",st);

			}
			p.println();
		}
		input.close();
		p.close();
		out.close();
		
		input = new Scanner(new File("../kaggle/test_data1"));
		out = new FileOutputStream("../kaggle/test_data2");
		p = new PrintStream(out);
		
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			boolean comma = true;
			for(int i=4; i<16 ;i++) {
				String tword = word[i-1];
				int right=0,wrong=0;
				if(stat[i].containsKey(tword))
					right=stat[i].get(word[i-1]);
				if(wstat[i].containsKey(tword))
					wrong=wstat[i].get(word[i-1]);
				double rate = offsetConfig.DEFAULTRATE;
				if(right+wrong!=0)
					rate = ((double)right)/(right+wrong);
				
				short st = (short) (rate * 10000);
				if(!comma)
					p.printf(",%d",st);
				else {
					comma = false;
					p.printf("%d",st);
				}
			}
			p.println();
		}
		input.close();
		p.close();
		out.close();
		
		System.out.println("finished");
		return;
	}
	
	// gbdt keep id, and Cid which more than 1 million
	// appearance only consider train data(train_rev2)
	public static void reformatforgbdt() throws IOException 
	{
		System.out.println("reformat for gbdt");
		
		Scanner input = new Scanner(new File("../kaggle/train_rev2"));
		String head = null, line = null;
		String[] column = null;
		
		FileOutputStream outc = new FileOutputStream("../kaggle/config");
		PrintStream pc = new PrintStream(outc);
		
		head = input.next();
		if(!weather_class_name) {
			column = head.split(",");
		}
		
		Map<String,Integer> totalstat[] = new HashMap[30];
		for(int i=0;i<25;i++)
			totalstat[i] = new HashMap();
		// part 1 : cal total number
		System.out.println("part 1");
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=3; i<24 ;i++) {
				if(totalstat[i].containsKey(word[i]))
					totalstat[i].put(word[i], totalstat[i].get(word[i])+1);
				else
					totalstat[i].put(word[i], 1);
			}
		}
		input.close();
			
		input = new Scanner(new File("../kaggle/test_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=3; i<24 ;i++) {
				if(totalstat[i].containsKey(word[i-1]))
					;
				else
					totalstat[i].put(word[i-1], 1);
			}
		}
		input.close();

		
		
		// part 2 cal index
		System.out.println("part 2");
		// init offsetConfig data
		offsetConfig.Boff = new int[24];
		offsetConfig.Boff[0]=0;offsetConfig.Boff[1]=0;offsetConfig.Boff[2]=24;
		
		int tttt = 24;
		Map<String,Integer> totalrev[] = new HashMap[30];
		pc.print("0,0,24");
		int tcc=0;
		for(int i=3;i<24;i++) {
			totalrev[i] = new HashMap();
			Iterator<Entry<String,Integer>> iter = totalstat[i].entrySet().iterator();
			int count = 0;
			while(iter.hasNext()) {
				Entry<String,Integer> entry = iter.next();
				totalrev[i].put(entry.getKey(),count );
				
				if( entry.getValue() > 1000000 ) {
//					System.out.println( "  "+i+"  "+(count+tttt) );
					tcc ++;
				}
				count ++;
			}
			pc.printf(",%d",count);
			offsetConfig.Boff[i] = count;
			tttt += count;
		}
		System.out.println("appearance more than 1 million: "+tcc);
		pc.println();
		pc.close();
		
		int tmp = offsetConfig.Boff[0];
		offsetConfig.Boffset = new int[24];
		offsetConfig.Boffset[0] = 0;
		for(int i=1;i<24;i++) {
			offsetConfig.Boffset[i] = tmp;
			tmp += offsetConfig.Boff[i];
		}
		
		// part 3 reformat
		System.out.println("part 3");
		FileOutputStream out = new FileOutputStream("../kaggle/train_data4");
		PrintStream p = new PrintStream(out);
		input = new Scanner(new File("../kaggle/train_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			boolean comma = true;
			for(int i=4;i<16;i++) {
				if( totalstat[i].get(word[i]) > 1000000 ) {
					if(comma) {
						comma= false;
						p.print(totalrev[i].get(word[i])+offsetConfig.Boffset[i]);
					}
					else
						p.print(","+(totalrev[i].get(word[i])+offsetConfig.Boffset[i]));
				}
				
			}
			if( totalstat[3].get(word[3]) > 1000000 ) {
				if(comma) {
					comma= false;
					p.print( (totalrev[3].get(word[3]) + offsetConfig.Boffset[3]) );	
				} else
					p.print(","+(totalrev[3].get(word[3]) + offsetConfig.Boffset[3]));
			}
			for(int i=16;i<24;i++) {
				if( totalstat[i].get(word[i]) > 1000000 ) {
					if(comma) {
						comma= false;
						p.print( totalrev[i].get(word[i]) + offsetConfig.Boffset[i] );
					}
					else
						p.print(","+(totalrev[i].get(word[i]) + offsetConfig.Boffset[i]));
					
				}
			}
			p.println();
		}
		input.close();
		p.close();
		
		out = new FileOutputStream("../kaggle/test_data4");
		p = new PrintStream(out);
		input = new Scanner(new File("../kaggle/test_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			boolean comma = true;
			for(int i=4;i<16;i++) {
				if( totalstat[i].get(word[i-1]) > 1000000 ) {
					if(comma) {
						comma= false;
						p.print(  totalrev[i].get(word[i-1])+offsetConfig.Boffset[i]);
					} else
						p.print(","+(totalrev[i].get(word[i-1])+offsetConfig.Boffset[i]));
				}
			}
			if( totalstat[3].get(word[2]) > 1000000 ) {
				if(comma) {
					comma= false;
					p.print( (totalrev[3].get(word[2]) + offsetConfig.Boffset[3]) );
				} else
					p.print(","+(totalrev[3].get(word[2]) + offsetConfig.Boffset[3]));
			}
			for(int i=16;i<24;i++) {
				if( totalstat[i].get(word[i-1]) > 1000000 ) {
					if(comma) {
						comma= false;
						p.print( (totalrev[i].get(word[i-1]) + offsetConfig.Boffset[i]) );
					} else
						p.print(","+(totalrev[i].get(word[i-1]) + offsetConfig.Boffset[i]));
				}
			}
			p.println();
		}
		input.close();
		p.close();
		
		System.out.println("finished");
		return;
		
	}
	
	
	public static void reformat() throws FileNotFoundException
	{
		System.out.println("reformat");
		
		Scanner input = new Scanner(new File("../kaggle/train_rev2"));
		String head = null, line = null;
		String[] column = null;
		
		FileOutputStream outc = new FileOutputStream("../kaggle/config");
		PrintStream pc = new PrintStream(outc);
		
		head = input.next();
		if(!weather_class_name) {
			column = head.split(",");
		}
		
		Map<String,Integer> totalstat[] = new HashMap[30];
		for(int i=0;i<25;i++)
			totalstat[i] = new HashMap();
		// part 1 : cal total number
		System.out.println("part 1");
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=3; i<24 ;i++) {
				if(totalstat[i].containsKey(word[i]))
					;
				else
					totalstat[i].put(word[i], 1);
			}
		}
		input.close();
			
		input = new Scanner(new File("../kaggle/test_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=3; i<24 ;i++) {
				if(totalstat[i].containsKey(word[i-1]))
					;
				else
					totalstat[i].put(word[i-1], 1);
			}
		}
		input.close();

		
		
		// part 2 cal index
		System.out.println("part 2");
		// init offsetConfig data
		offsetConfig.Boff = new int[24];
		offsetConfig.Boff[0]=0;offsetConfig.Boff[1]=0;offsetConfig.Boff[2]=24;
		
		
		Map<String,Integer> totalrev[] = new HashMap[30];
		pc.print("0,0,24");
		for(int i=3;i<24;i++) {
			totalrev[i] = new HashMap();
			Iterator<Entry<String,Integer>> iter = totalstat[i].entrySet().iterator();
			int count = 0;
			while(iter.hasNext()) {
				Entry<String,Integer> entry = iter.next();
				totalrev[i].put(entry.getKey(),count );
				count ++;
			}
			pc.printf(",%d",count);
			offsetConfig.Boff[i] = count;
			/* release memory */
			totalstat[i] = null;
		}
		pc.println();
		pc.close();
		
		int tmp = 0;
		offsetConfig.Boffset = new int[24];
		offsetConfig.Boffset[0] = 0;
		for(int i=1;i<24;i++) {
			offsetConfig.Boffset[i] = tmp;
			tmp += offsetConfig.Boff[i];
		}
		
		
		// part 3 reformat
		System.out.println("part 3");
		FileOutputStream out = new FileOutputStream("../kaggle/train_data1");
		PrintStream p = new PrintStream(out);
		input = new Scanner(new File("../kaggle/train_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			p.print(word[0]); p.print(",");
			p.print(word[1]); p.print(",");
			p.print(word[2].substring(4, 8)); p.print(",");
			for(int i=3;i<24;i++) {
				p.print(totalrev[i].get(word[i])+offsetConfig.Boffset[i]);
				if(i!=23)
					p.print(",");
			}
			p.println();
		}
		input.close();
		p.close();
		
		out = new FileOutputStream("../kaggle/test_data1");
		p = new PrintStream(out);
		input = new Scanner(new File("../kaggle/test_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");
			p.print(word[0]); p.print(",");
			p.print(word[1].substring(4, 8)); p.print(",");
			for(int i=3;i<24;i++) {
				p.print(totalrev[i].get(word[i-1])+offsetConfig.Boffset[i]);
				if(i!=23)
					p.print(",");
			}
			p.println();
		}
		input.close();
		p.close();
		
		System.out.println("finished");
		return;
	}
	
	public static void caloffset() throws FileNotFoundException
	{
		Scanner input = new Scanner(new File("../kaggle/train_rev2"));
		String head = null, line = null;
		String[] column = null;
		
		head = input.next();
		if(!weather_class_name) {
			column = head.split(",");
		}
		
		Map<String,Integer> totalstat[] = new HashMap[30];
		for(int i=0;i<25;i++)
			totalstat[i] = new HashMap();
		
		int count = 0;
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=start; i<end+1 ;i++) {
				if(totalstat[i].containsKey(word[i]))
					totalstat[i].put(word[i],totalstat[i].get(word[i]) );
				else
					totalstat[i].put(word[i], 1);
			}
		}
		input.close();
		
//		cal test data		
		input = new Scanner(new File("../kaggle/test_rev2"));
		head = input.next();
		while( input.hasNext() ) {
			line = input.next();
			String[] word = line.split(",");

			for(int i=start; i<end+1 ;i++) {
				if(totalstat[i].containsKey(word[i-1]))
					totalstat[i].put(word[i-1],totalstat[i].get(word[i-1]) );
				else
					totalstat[i].put(word[i-1], 1);
			}
		}
		input.close();
		
		System.out.println("finished");
		for(int i=start; i<end+1 ;i++)
			System.out.println("total "+ column[i] +" size " +totalstat[i].size());
		
		return;
	}
}
