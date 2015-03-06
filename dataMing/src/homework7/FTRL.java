package homework7;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FTRL {
	static int TOTAL_TRAIN = 40428967;
	static int TOTAL_TEST = 4577464;

	
	static int W0OFFSET = 1;
	static int PART1OFFSET = 0;
	static int PART2OFFSET = 0;
	static int TOTALOFFSET = W0OFFSET + PART1OFFSET + PART2OFFSET;
	
	static String train = null;
	static String test = null;
	static String submission = "../kaggle/submission.csv";
	static String trainpath = "../kaggle/train_data1";
	static String testpath = "../kaggle/test_data1";	
	static String trainpath2 = "../kaggle/train_data5";
	static String testpath2 = "../kaggle/test_data5";
	
	static String trainpath3 = "../kaggle/train_data2";
	static String testpath3 = "../kaggle/test_data2";
	
	static int epoch = 6;
	static double alpha = 0.1;
	static double beta = 1.0;
	static double L1 = 0.9;
	static double L2 = 1.0;
	
	double n[] = null;
	double z[] = null;
	
	
	Map<Integer,Double> w = new HashMap();
	double getw(int x) {
		if(w.containsKey(x))
			return w.get(x);
		else 
			return 0.0;
	}
	double setw(int x,double y) {
		w.put(x, y);
		return 0;
	}
	void reneww() {
		w.clear();
	}																																																																									
	
	
	static public void main(String argv[]) 
	{
		System.out.println(new Date());
		try {
			new FTRL().run();
//			new FTRL().run_prob();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(new Date());
		return;
	}
	
	double logloss(double p, double y) {
		double tt = Math.pow(10, -14);
		double tmp = (p>1-tt)?(1-tt):p;
		tmp = (tmp>tt)?tmp:tt;
		if( y == 1.0 )
			return -Math.log(p);
		else
			return -Math.log(1.0-p);
	}

	public void run() throws FileNotFoundException
	{
		this.reflashconfig();
		

		FileOutputStream out = new FileOutputStream(submission);
		PrintStream p = new PrintStream(out);
		readClass rC = null,rC2 = null;
			
		for(int i=0;i<epoch;i++) {
			double loss =0.0;
			int count = 0;
			Date starttime = new Date();
			
			rC = new readClass(); rC2 = new readClass();
			rC.open(trainpath); rC2.open(trainpath2);
		
			for(int j=0;j<TOTAL_TRAIN;j++) {
				/* read*/
				reneww();
				String tword[] = rC.readline().split(",");
				String tword2[] = rC2.readline().split(",");
				
				int[] tx = new int[tword.length-2+1 +tword2.length];
				tx[0] = 0;
				// extract date and hour, format  DDHH
				tx[1] = Integer.parseInt(tword[2]);
				int date = tx[1]/100;
				tx[1] %= 100;
				tx[1] = 0;
				// -2 because first 2 is id,click
				for(int k=1;k<tword.length-2;k++) {
					// +1 because the first feature is always 1
					tx[k+1] = Integer.parseInt(tword[k+2]) + FTRL.W0OFFSET;
				}
				for(int k=0; k<tword2.length ; k++) {
					int tbo = tword.length-1;
					tx[k+tbo] = Integer.parseInt(tword2[k]) + FTRL.PART1OFFSET ;
				}
				
				double est = predict(tx);
				double res = Integer.parseInt(tword[1]);
				if( date > 30 || (j% 100 ==0) ){ 
					loss += logloss(est,res);
					count ++;
				}
				else
					update(tx,est,res);
			}
			if(null != rC.readline()) {
				System.out.println("error1: 1");
			}
			rC = null; rC2 = null;
			
			if( count == 0 )
				System.out.printf("not cal loss %s\n", new Date().compareTo(starttime));
			else
				System.out.printf("Epoch %d finished, validation logloss: %f, elapsed time: %s\n",i,loss/count,new Date().compareTo(starttime));
		}
		

		System.out.println("train finished");
		
		p.println("id,click");
		rC = new readClass(); rC2 = new readClass();
		rC.open(testpath);  rC2.open(testpath2);
		
		for(int i=0;i<TOTAL_TEST;i++) {
			/* read*/
			reneww();
			String tword[] = rC.readline().split(",");
			String tword2[] = rC2.readline().split(",");
			int[] tx = new int[tword.length + tword2.length];
			tx[0] = 0;
			tx[1] = Integer.parseInt(tword[1]);
			int date = tx[1]/100;
			tx[1] %= 100;
			tx[1] = 0;
			// -1 because first 1 is id
			for(int k=1;k<tword.length-1;k++) {
				// +1 because the first feature is always 1
				tx[k+1] = Integer.parseInt(tword[k+1]) +FTRL.W0OFFSET;
			}
			for(int k=0; k<tword2.length ; k++) {
				int tbo = tword.length;
				tx[k+tbo] = Integer.parseInt(tword2[k]) + FTRL.PART1OFFSET;
			}
			
			double est = predict(tx);
			// output
			p.print(tword[0]); p.print(",");
			p.print(est); p.println();
		}
		if(null != rC.readline()) {
			System.out.println("error1: 2");
		}
		p.close();
	}
	
	
	double predict(int[] x)
	{
		double wTx = 0.0;
		int tsign = 0;
		for(int i=1;i<x.length;i++) {
			int index = x[i];
			tsign = z[index]<0?-1:1;
			if( tsign*z[index] <= L1)
				setw(index,0);
			else
				setw(index,(tsign*L1-z[index])/((beta+Math.sqrt(n[index]))/alpha + L2));
			wTx	+= getw(index);
		}
		
		wTx = (wTx>35)?35:wTx;
		wTx = (wTx<-35)?-35:wTx;
		return 1.0/(1.0+ Math.exp(-wTx));
	}
	
	void update(int[] x, double p, double y)
	{
		double g = p-y;
		for(int i=1;i<x.length;i++) {
			int index = x[i];
			double sigma = (Math.sqrt(n[index] + g*g) - Math.sqrt(n[index])) / alpha;
			z[index] += ( g - sigma * getw(index) );
			n[index] += ( g * g );
		}
	}
	
	class readClass {
		Scanner input = null ;
		
		public boolean open(String path) {
			try {
				input = new Scanner(new File(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		public String readline() {
			if( input == null )
				return null;
			if( input.hasNextLine() ) {
				return input.nextLine();
			}
			input.close();
			input = null;
			return null;
		}
		
	}
	
	
	/* add probability
	 * 
	 *  prob is a int value [0,10000] / 10000
	 */
	public void run_prob() throws FileNotFoundException
	{
		this.reflashconfig();
		

		FileOutputStream out = new FileOutputStream(submission);
		PrintStream p = new PrintStream(out);
		readClass rC = null,rC2 = null;
		
		int kk = 0;
		for(int i=0;i<epoch;i++) {
			double loss =0.0;
			int count = 0;
			Date starttime = new Date();
			
			rC = new readClass(); rC2 = new readClass();
			rC.open(trainpath); rC2.open(trainpath3);
		
			for(int j=0;j<TOTAL_TRAIN;j++) {
				/* read*/
				reneww();
				String tword[] = rC.readline().split(",");
				String tword2[] = rC2.readline().split(",");
//				tword2 = new String[0];
				
				int[] tx = new int[tword.length-2+1 ];// tword2.length-1];
				tx[0] = 0;
				// extract date and hour, format  DDHH
				tx[1] = Integer.parseInt(tword[2]);
				int date = tx[1]/100;
				tx[1] %= 100;
				tx[1] = 0;
				// -2 because first 2 is id,click
				for(int k=1;k<tword.length-2;k++) {
					// +1 because the first feature is always 1
					tx[k+1] = Integer.parseInt(tword[k+2]) + FTRL.W0OFFSET;
				}
				// need k-1, first is click
//				for(int k=kk+1; k<=kk+1 ;k++) { // tword2.length ; k++) {
//					int tbo = tword.length-1;
//					tx[tbo] = Integer.parseInt(tword2[k]);// + FTRL.PART1OFFSET ;
//				}
				
				double est = predict_prob(tx, tword.length-1, FTRL.PART1OFFSET);
				double res = Integer.parseInt(tword[1]);
				if( date >= 30 || (j% 100 ==0) ) { 
					loss += logloss(est,res);
					count ++;
				}
				else
					update_prob(tx, tword.length-1, FTRL.PART1OFFSET, est,res);
			}
			if(null != rC.readline()) {
				System.out.println("error1: 1");
			}
			rC = null; rC2 = null;
			
			if( count == 0 )
				System.out.printf("not cal loss %s\n", new Date().compareTo(starttime));
			else
				System.out.printf("Epoch %d finished, validation logloss: %f, elapsed time: %s\n",i,loss/count,new Date().compareTo(starttime));
		}
		

		System.out.println("train finished");
		
		p.println("id,click");
		rC = new readClass(); rC2 = new readClass();
		rC.open(testpath);  rC2.open(testpath3);
		
		for(int i=0;i<TOTAL_TEST;i++) {
			/* read*/
			reneww();
			String tword[] = rC.readline().split(",");
			String tword2[] = rC2.readline().split(",");
//			tword2 = new String[0];
			
			int[] tx = new int[tword.length ];// tword2.length];
			tx[0] = 0;
			tx[1] = Integer.parseInt(tword[1]);
			int date = tx[1]/100;
			tx[1] %= 100;
			tx[1] = 0;
			// -1 because first 1 is id
			for(int k=1;k<tword.length-1;k++) {
				// +1 because the first feature is always 1
				tx[k+1] = Integer.parseInt(tword[k+1]) +FTRL.W0OFFSET;
			}
//			for(int k=kk; k<=kk ; k ++) { //tword2.length ; k++) {
//				int tbo = tword.length;
//				tx[tbo] = Integer.parseInt(tword2[k]); //+ FTRL.PART1OFFSET;
//			}
			
			double est = predict_prob(tx, tword.length, FTRL.PART1OFFSET);
			// output
			p.print(tword[0]); p.print(",");
			p.print(est); p.println();
		}
		if(null != rC.readline()) {
			System.out.println("error1: 2");
		}
		p.close();
	}
	
	/*
	 * split is x array index split [ index , value ]
	 * part1: total number of part1 feature
	 */
	double predict_prob(int[] x, int split, int part1)
	{
		double wTx = 0.0;
		int tsign = 0;
		
		for(int i=1;i<split;i++) {
			int index = x[i];
			tsign = z[index]<0?-1:1;
			if( tsign*z[index] <= L1)
				setw(index,0);
			else
				setw(index,(tsign*L1-z[index])/((beta+Math.sqrt(n[index]))/alpha + L2));
			wTx	+= getw(index);
		}
		double LL1 = 1,LL2 = 1;
		for(int i=split;i<x.length;i++) {
			int index = part1 + i - split;
			tsign = z[index]<0?-1:1;
			if( tsign*z[index] <= LL1)
				setw(index,0);
			else
				setw(index,(tsign*LL1-z[index])/((beta+Math.sqrt(n[index]))/alpha + LL2));
			wTx	+= getw(index)*((double)x[i]/10000);
		}
		
		wTx = (wTx>35)?35:wTx;
		wTx = (wTx<-35)?-35:wTx;
		return 1.0/(1.0+ Math.exp(-wTx));
	}
	
	void update_prob(int[] x, int split, int part1, double p, double y)
	{
		double g = p-y;
		for(int i=1;i<split;i++) {
			int index = x[i];
			double sigma = (Math.sqrt(n[index] + g*g) - Math.sqrt(n[index])) / alpha;
			z[index] += ( g - sigma * getw(index) );
			n[index] += ( g * g );
		}
		for(int i=split;i<x.length;i++) {
			int index = part1 + i - split;
			g = (p-y)*((double)x[i]/10000);
			double sigma = (Math.sqrt(n[index] + g*g) - Math.sqrt(n[index])) / alpha;
			z[index] += ( g - sigma * getw(index) );
			n[index] += ( g * g );
		}
	}
	

	
	
	
	void reflashconfig() {
		String path = offsetConfig.configFile;
		Scanner input = null;
		try {
			input = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FTRL.W0OFFSET = 1;
		String line = null;
		String[] tsp = null;
		// line1 
		line = input.nextLine();
		tsp = line.split(",");
		offsetConfig.Boff = new int[tsp.length];
		for(int i=0;i<tsp.length;i++) {
			offsetConfig.Boff[i] = Integer.parseInt( tsp[i]);
		}
		int tmp = 0;
		offsetConfig.Boffset = new int[tsp.length+1];
		offsetConfig.Boffset[0] = 0;
		for(int i=0;i<tsp.length;i++) {
			offsetConfig.Boffset[i+1] = tmp;
			tmp += offsetConfig.Boff[i];
		}
		FTRL.PART1OFFSET = tmp;
		
		
		// line 2
		if( input.hasNext() ) {
			// no line 2
			tsp = line.split(",");
			for(int i=0;i<tsp.length;i++) {
				;
			}
		}
		tmp = 0;
		offsetConfig.Boffset2[0] = 0;
		tmp += offsetConfig.Boff2[0];
		for(int i=1;i<offsetConfig.Boff2.length;i++) {
			offsetConfig.Boffset2[i] = tmp;
			tmp += offsetConfig.Boff2[i];
		}
		
		FTRL.PART2OFFSET = tmp;
		input.close();
		
		FTRL.TOTALOFFSET = FTRL.W0OFFSET + FTRL.PART1OFFSET + FTRL.PART2OFFSET;
		this.n = new double[FTRL.TOTALOFFSET];
		this.z = new double[FTRL.TOTALOFFSET];
		
		return;
	}
}
