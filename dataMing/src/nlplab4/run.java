package nlplab4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class run {
	static public void main(String argv[]) {
		readdata();
		return;
	}
	
	static public void readdata() {
		Scanner inputch = null, inputen = null, inputa = null;
		FileOutputStream out = null;
		PrintStream p = null;		
		
		try {
			inputch = new Scanner(new File("../smt/test.ch"));
			inputen = new Scanner(new File("../smt/test.en"));
			inputa = new Scanner(new File("../smt/test.align"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			out = new FileOutputStream("../smt/result");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		p = new PrintStream(out);
		
		int array[][] = new int[100][];
		for(int i=0;i<100;i++)
			array[i] = new int[100];
		
		while(inputch.hasNext()) {
			String linech = inputch.nextLine(), lineen = inputen.nextLine(), linea = inputa.nextLine();
			String wordch[] = linech.split(" "), worden[] = lineen.split(" "), worda[] = linea.split(" ");
			
//			System.out.println("ch     " + wordch.length);
//			System.out.println("en     " + worden.length);
//			System.out.println("align  " + worda.length);
//			
//			for(int i=0;i<wordch.length;i++)
//				System.out.println(wordch[i]);
			
			for(int i=0;i<array.length;i++)
				for(int j=0;j<array[i].length;j++)
					array[i][j] = 0;
			
			for(int i=0;i<worda.length;i++) {
				String tmp[] = worda[i].split("-");
				int t1 = Integer.parseInt(tmp[0]);
				int t2 = Integer.parseInt(tmp[1]);
				
				array[t1][t2] = 1;
			}
			
			int left,right;
			int leftmax, rightmax;
			for(int i=0;i<array.length;i++) {
				left = right = -1;
				for(int j=0;j<array[i].length;j++)
					if(array[i][j]>0) {
						left = i;
						right = j;
						break;
					}
				
				if(left == -1 && right == -1)
					continue;
				
				rightmax = right;
				
				for(int j=right+1;j<array[left].length;j++) {
					if(array[left][j] > 0){
						rightmax = j;
					} else 
						break;
				}
				
				if(right != rightmax) {
					p.printf("%s ", wordch[left]);
					p.printf(" ||| ");
					for(int j=right;j<=rightmax;j++) {
						p.printf("%s ", worden[j]);
					}
					p.printf(" ||| ");
					p.println();
					continue;
				}
				
				leftmax = left;
				
				for(int j=left+1;j<array.length;j++) {
					if(array[j][right] > 0) {
						leftmax = j;
					} else
						break;
				}
				
				for(int j=left;j<=leftmax;j++) {
					p.printf("%s ", wordch[j]);
				}
				p.printf(" ||| ");
				p.printf("%s ", worden[right]);
				p.printf(" ||| ");
				p.println();
				i=leftmax;
			}
			
		}
		
		
		
		inputch.close();
		inputen.close();
		inputa.close();
		p.close();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
