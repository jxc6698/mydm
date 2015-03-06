package homework7;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class cartTree {
	
	treenode root = null ;
	int c_r  = -1; // = 0 , classify , 1 regression
	int fsplit =-1;
	
	public class treenode {
		double classid ;
		int index;  // 
		treenode left , right ;
		int depth ;
		double value ;
		int featureid ;
		treenode()
		{
			left = right = null ;
			this.classid = -1 ;
			this.featureid = -1;
		}
	}
	
	public void train( short[][] set, int fsparse[][],
			double[] num,
			int max_depth, int treeindex, int c_r )
	{
		this.c_r = c_r ;
		int depth = 0;
		int indexcount =0, savecount=0;
		root = null ;
		treenode curt = null ;
		Stackitem cur = null , nextl , nextr ;
		List<Stackitem> stack = new ArrayList<Stackitem>() ;
		Stackitem item = new Stackitem();
		item.id = savecount;
		savecount++;
		item.writeopen();
		for(int i=0;i<num.length;i++)
			item.write(i);
		item.writeclose();
		
		if( set == null)
			fsplit = 0;
		else
			fsplit = set[0].length;
		item.node.depth = 0 ;
		stack.add(item);
		int ccc = 0;
		root = item.node ;
		for( ; true ; )
		{
			if( stack.size() == 0 )
				break ;
			cur = stack.get(stack.size()-1);
			stack.remove( stack.size() - 1 );
//			System.out.println("tree "+treeindex+" node "+cur.id+" "+new Date());
			cur.openread();
			curt = cur.node ;
			
			if( cur.node.depth == max_depth )
			{
				double c = get_classnum( cur.index , num ) ;
				curt.classid = c ;
				curt.index = indexcount;
				indexcount ++;
				continue ;
			}
			resultdata c = is_one_class( cur.index , num ) ;
			if( c.id == 1 )  /* contains only one class */
			{
				curt.classid = c.data ;
				curt.index = indexcount;
				indexcount ++;
				continue ;
			}

			resultdata d = get_best_feature( set, fsparse, cur.index , num );
//			System.out.println( "   " + d.id + "   " + d.data ) ;
			
			int findex = d.id;
			double fval = d.data ;
			if( d.data == -1 )  /* with same feature but different classnum */
			{
				double tmp = get_classnum( cur.index , num ) ;
				curt.classid = tmp ;
				curt.index = indexcount;
				indexcount ++;
				continue ;
			}
			nextl = new Stackitem() ;
			nextr = new Stackitem() ;
			nextl.id = savecount;
			savecount ++;
			nextr.id = savecount;
			savecount ++;
			nextl.writeopen();nextr.writeopen();
			
			if( findex < fsplit) {
				for(int i=0;i<cur.index.length ; i++ ) {
					if( set[findex][cur.index[i]] <= fval )
						nextl.write( cur.index[i]);
					else
						nextr.write( cur.index[i]);
				}
			} else { // fval should always be 1
				for(int i=0;i<cur.index.length ; i++ ) {
					if( Arrays.binarySearch( fsparse[ findex-fsplit ], cur.index[i]) < 0)
						nextl.write( cur.index[i]);
					else
						nextr.write( cur.index[i]);
				}
			}
			curt.value = fval ;
			curt.featureid = findex ;
			curt.left = nextl.node ;
			curt.right = nextr.node ;
			curt.left.depth = curt.depth + 1 ;
			curt.right.depth = curt.depth + 1 ;
			stack.add( nextl ) ;
			stack.add( nextr ) ;
			/* clean data for gc */
			cur.index = null ;
			ccc ++ ;
		}
		this.count = null;
		this.total = null;
		this.insindex = null;
		
		return ;
	}
	
	public void estimate( short[][] set, int [][]fsparse,
			double[] num)
	{
		treenode curt = root ;
		for( int i= 0 ; i< num.length ; i ++)
		{
			curt = root ;
			while( curt != null )
			{
				if( curt.featureid == -1 )
				{
					num[i] = curt.classid;
					break ;
				}
				if( curt.featureid < this.fsplit) {
					if( set[curt.featureid][i] <= curt.value )
						curt = curt.left ;
					else
						curt = curt.right;
				} else {
					if( Arrays.binarySearch(fsparse[curt.featureid-fsplit], i) < 0)
						curt = curt.left ;
					else
						curt = curt.right;
				}
			}
			if( curt == null )
			{
				System.out.println("-----------------------");
			}
		}
		return ;
	}
	
	public void estimatefeature( short[][] set, int [][]fsparse,
			int[] num )
	{
		treenode curt = root ;
		for( int i= 0 ; i< num.length ; i ++)
		{
			curt = root ;
			while( curt != null )
			{
				if( curt.featureid == -1 )
				{
					num[i] = curt.index;
					break ;
				}
				if( curt.featureid < this.fsplit) {
					if( set[curt.featureid][i] <= curt.value )
						curt = curt.left ;
					else
						curt = curt.right;
				} else {
					if( Arrays.binarySearch(fsparse[curt.featureid-fsplit], i) < 0)
						curt = curt.left ;
					else
						curt = curt.right;
				}
			}
			if( curt == null )
			{
				System.out.println("-----------------------");
			}
		}
		return ;
	}
	
	
	class Stackitem{
		int[] index;
		int size;
		int id; // for save filename: is tsave[id]
		treenode node;
		Stackitem(){
			index = null;
			node = new treenode() ;
		}
		FileOutputStream out = null;
		PrintStream p = null;
		public void openread() {
			Scanner input=null;
			try {
				input = new Scanner(new File("tsave"+id));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			index = new int[size];
			int count = 0;
			while(input.hasNext()) {
				String word = input.next();
				index[count] = Integer.parseInt(word);
				count ++;
			}
			input.close();
		}

		public void writeopen() {
			size =0;
			try {
				out = new FileOutputStream("tsave"+id);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p = new PrintStream(out);
		}
		public void write(int x) {
			p.println(x);
			size ++;
		}
		
		public void writeclose() {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			p.close();
		}
	}
	
	class resultdata{
		int id;
		double data;
		public resultdata( int id , double data )
		{
			this.id = id ;
			this.data = data ;
		}
	}
	
	class datacmp{
		int[] count;
		short data;
		public datacmp( int num , short data )
		{
			this.count = new int[num] ;
			this.data = data ;
		}
	}
	
	Comparator<datacmp> comparator = new Comparator<datacmp>(){  
		public int compare(datacmp s1, datacmp s2) {
			if(s1.data>s2.data)
				return 1 ;
			else if( s1.data == s2.data )
				return 0 ;
			else
				return -1 ;
			}  
	};
	
	resultdata is_one_class( int[] index , double[] num )
	{
		if( index.length == 0 )
		{
			System.out.println("error ----1");
			System.exit(-1);
		}
		double tmp = num[index[0]];
		double ret = tmp ;
		int mark = 1 ;
		for( int i =0;i< index.length ; i++)
		{
			if( num[index[i]]== tmp )
				;
			else
				mark = 0 ;
		}
		return new resultdata( mark , ret ) ;
	}
	
	double get_classnum( int[] index , double[] num )
	{
		if( this.c_r == 0 )
			System.err.println("error 4:" + "enter get_classnum_c");
//			return get_classnum_c( index , num ) ;
		else if( this.c_r == 1 )
			return get_classnum_r( index , num ) ;
		else
		{
			System.out.println("error 4") ;
			return 0 ;
		} 
		return 0;
	}
	double get_classnum_c( List<Integer> index , List<Double> num )
	{
		
		Map<Double,Integer> count = new HashMap<Double,Integer>() ;
		for( int i =0; i<index.size() ; i++)
		{
			double tmp = num.get(index.get(i)) ;
			if( count.containsKey(tmp))
				count.put(tmp, count.get(tmp) + 1 ) ;
			else
				count.put(tmp, 1) ;
		}
		double tmp = 0 ;int max=0 ;
		Iterator iter = count.keySet().iterator() ;
//		for( int i =0;i< count.size() ; i++ )
		while(iter.hasNext())
		{
			double key = (double) iter.next() ;
			if( count.get(key) > max )
			{
				tmp = key ;
				max = count.get(key) ;
			}
		}
		return tmp ;
	}
	double get_classnum_r( int[] index , double[] num )
	{
		
//		Map<Double,Integer> count = new HashMap<Double,Integer>() ;
		int count = index.length ;
		double total = 0.0 ;
		for( int i =0; i<index.length ; i++)
		{
			count ++ ;
			double tmp = num[index[i]] ;
			total += tmp ;
		}
		double tmp = 0 ;int max=0 ;
		tmp = total/count ;
		return tmp ;
	}
	/**
	 *   make sure fset.size() > 1  , more than one class
	 * @param fset[][]:    [featureid][instanceid]
	 * @param index
	 * @param num
	 * @param fsparse[][]: [featureid][instanceid with fvalue = 1]
	 * @return
	 */
	int TOTALVALUE = offsetConfig.gbdtlabeldiffnum;
	int count[] = new int[ TOTALVALUE ];
	int total[] = new int[ TOTALVALUE ];
	List<Integer> datacmpindex = new ArrayList(); ;
	Map<Double,Integer> insindex = new HashMap<Double,Integer>();
	
	resultdata get_best_feature( short[][] fset, int fsparse[][],
			int[] index, double[] num)
	{
		int featurenum = (fset==null)?0:fset.length;
		double max = -1;
		int maxindex = 0;
		insindex.clear();
		
		for( int j = 0 ; j < index.length ; j++ ){
			Double key = num[index[j]];
			if( insindex.containsKey( num[index[j]] ))
				;
			else
				insindex.put( key, insindex.size());
			total[insindex.get(key)] ++;
		}
		if( insindex.size() > 100000 )
			System.out.println("insindex size "+insindex.size());
		
		double entropy1 =0, entropy2 = 0, mmentropy = 0;
		resultdata result = new resultdata( 0 , -1.0) ;
		
		for( int i=0;i<insindex.size();i++) {
			double tmp = ((double)total[i])/index.length;
			if( tmp != 0 )
				entropy1 += (tmp * tmp);
		}
		entropy1 = 1 - entropy1;
		
		datacmp[] tmp = new datacmp[10001];
		/*  for dense feature */
		for( int i =0 ; i < featurenum ; i++) {
			int tmax= -1;
			for(int j=0;j<insindex.size();j++)
				count[j] =0;
			datacmpindex.clear();
			for( int j = 0 ; j < index.length ; j++ ) {
				short val = fset[i][index[j]];
				if(tmp[val] == null) {
					tmp[val] = new datacmp( TOTALVALUE , val );
					if( tmax < val )
						tmax = val;
					datacmpindex.add((int)val);
				}
				tmp[val].count[insindex.get( num[index[j]] ) ] ++;
			}
			// sort 200 item
//			Arrays.sort(datacmpindex);
			Collections.sort(datacmpindex);
			
			int val = -1;
			double maxentropy = -1.0 , maxval = 0 ;
			int maxid = 0, tcount=0; /* tcount mean first part instance number */
			for( int jj= 0 ; jj < tmp.length ; jj ++ ) {
				if( tmp[jj] == null )
					continue;
				if( jj == tmax ) {
					tmp[jj] = null;
					break;
				}
				if(tmp[jj]== null) {
					System.out.println("error 5: datacmpindex get null pointer");
				}
				val = tmp[jj].data ;
				
				for(int k=0;k<insindex.size();k++) { 
					count[k] += tmp[jj].count[k];
					tcount += tmp[jj].count[k];
				}
				tmp[jj] = null;
				int j = tcount;
				entropy2 = 0;
				// gini
				for(int k=0;k<insindex.size();k++) {
					double tmp1 = ((double)count[k] ) / j ; // j is the total number
					if( tmp1 != 0 )
						entropy2 += tmp1 * tmp1 * j / index.length ;
				}
				for(int k=0;k<insindex.size();k++) {
					double tmp1 = ((double)(total[k]-count[k])) / ( index.length - j ) ;
					if( tmp1 != 0 ) 
						entropy2 += tmp1 * tmp1 * ( index.length - j ) / index.length ;
				}
				entropy2 = 1 - entropy2;
//					System.out.println( entropy1 +"  "+entropy2);
				if( entropy1 - entropy2 > maxentropy ) /* ID3 */
				{
					maxentropy = entropy1 - entropy2 ;
					maxid = i ;
					maxval = val ;
				}
			}
			if( mmentropy < maxentropy )
			{
				result.id = maxid ;
				result.data = maxval ;
				mmentropy = maxentropy ;
			}
		}
		datacmpindex.clear();
		tmp = null;
		
		if( fsparse == null )
			return result;
		
		/* for sparse feature */
		for(int i=0;i< fsparse.length;i++) {
			for(int j=0;j<insindex.size();j++)
				count[j] =0;
			/* for index.length or fsparse[i].length ? 
			 * which is faster
			 * both index(added in turn when train function) and
			 *  fsparse(added in turn when input) is sorted
			 */
			int tcount =0;
			for(int j=0;j<index.length;j++) {
				if( Arrays.binarySearch(fsparse[i], index[j] ) >= 0) {
					count[ insindex.get( num[index[j] ] )] ++;
					tcount ++;
				}
			}
			
			int j= tcount;
			entropy2 = 0;
			// gini
			for(int k=0;k<insindex.size();k++) {
				double tmp1 = ((double)count[k] ) / j ; // j is the total number
				if( tmp1 != 0 )
					entropy2 += tmp1 * tmp1 * j / index.length ;
			}
			for(int k=0;k<insindex.size();k++) {
				double tmp1 = ((double)(total[k]-count[k])) / ( index.length - j ) ;
				if( tmp1 != 0 ) 
					entropy2 += tmp1 * tmp1 * ( index.length - j ) / index.length ;
			}
			entropy2 = 1 - entropy2;
		
			if( mmentropy < entropy1 - entropy2 )
			{
				result.id = this.fsplit + i ;
				result.data = 0;
				mmentropy = entropy1 - entropy2 ;
			}
		}
		
		return result ;
	}
}
