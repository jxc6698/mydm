package homework5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DT {
	
	treenode root = null ;
	int c_r  = -1; // = 0 , classify , 1 regression
	
	public void train( List<List<Double>> set , 
			List<Double> num , int classnum ,
			int max_depth , int min_node , int c_r )
	{
		this.c_r = c_r ;
		int depth = 0 ;
		root = null ;
		treenode curt = null ;
		Stackitem cur = null , nextl , nextr ;
		List<Stackitem> stack = new ArrayList<Stackitem>() ;
		Stackitem item = new Stackitem() ;
		for( int i=0;i<set.size();i++)
			item.index.add(i) ;
		item.node.depth = 1 ;
		stack.add(item);
		int ccc = 0 ;
		root = item.node ;
		for( ; true ; )
		{
			if( stack.size() == 0 )
				break ;
			cur = stack.get(stack.size()-1) ;
			stack.remove( stack.size() - 1 ) ;
			
			curt = cur.node ;
			
			if( cur.node.depth == max_depth )
			{
				double c = get_classnum( cur.index , num ) ;
				curt.classid = c ;
				continue ;
			}
			datacmp c = is_one_class( cur.index , num ) ;
			if( c.id == 1 )  /* contains only one class */
			{
				curt.classid = c.data ;
				continue ;
			}
			datacmp d = get_best_feature( set , cur.index , num , classnum ) ;
//			System.out.println( "   " + d.id + "   " + d.data ) ;
			
			int findex = d.id;
			double fval = d.data ;
			if( d.data == -1 )  /* with same feature but different classnum */
			{
				double tmp = get_classnum( cur.index , num ) ;
				curt.classid = tmp ;
				continue ;
			}
			nextl = new Stackitem() ;
			nextr = new Stackitem() ;
			for(int i=0;i<cur.index.size() ; i++ )
			{
				if( set.get( cur.index.get(i) ).get( findex ) <= fval )
					nextl.index.add( cur.index.get(i) ) ;
				else
					nextr.index.add( cur.index.get(i) ) ;
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
//			System.out.println(nextl.index.size() + "    "+ nextr.index.size());
		}
		return ;
	}
	
	public void estimate( List<List<Double>> set , 
			List<Double> num )
	{
		treenode curt = root ;
//		System.out.println(root.classid);
		for( int i= 0 ; i< set.size() ; i ++)
		{
//			System.out.println("+++++");
			curt = root ;
			while( curt != null )
			{
				if( curt.featureid == -1 )
				{
					num.add(curt.classid) ;
					break ;
				}
				if( set.get(i).get(curt.featureid) <= curt.value )
				{
					curt = curt.left ;
//					System.out.println("left");
				}else
				{
					curt = curt.right ;
//					System.out.println("right");
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
		List<Integer> index ;
		treenode node ;
		Stackitem(){
			index = new ArrayList<Integer>() ;
			node = new treenode() ;
		}
	}
	
	class datacmp{
		int id ;
		double data ;
		public datacmp( int id , double data )
		{
			this.id = id ;
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
	
	datacmp is_one_class( List<Integer> index , List<Double> num )
	{
		if( index.size() == 0 )
		{
			System.out.println("error ----1");
			System.exit(-1);
		}
		double tmp = num.get( index.get(0) );
		double ret = tmp ;
		int mark = 1 ;
		for( int i =0;i< index.size() ; i++)
		{
			if( num.get( index.get(i) ) == tmp )
				;
			else
				mark = 0 ;
		}
		return new datacmp( mark , ret ) ;
	}
	
	double get_classnum( List<Integer> index , List<Double> num )
	{
		if( this.c_r == 0 )
			return get_classnum_c( index , num ) ;
		else if( this.c_r == 1 )
			return get_classnum_r( index , num ) ;
		else
		{
			System.out.println("error 4") ;
			return 0 ;
		} 
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
	double get_classnum_r( List<Integer> index , List<Double> num )
	{
		
//		Map<Double,Integer> count = new HashMap<Double,Integer>() ;
		int count = index.size() ;
		double total = 0.0 ;
		for( int i =0; i<index.size() ; i++)
		{
			count ++ ;
			double tmp = num.get(index.get(i)) ;
			total += tmp ;
		}
		double tmp = 0 ;int max=0 ;
		tmp = total/count ;
		return tmp ;
	}
	/**
	 *   make sure fset.size() > 1  , more than one class
	 * @param fset
	 * @param index
	 * @param num
	 * @param classnum
	 * @return
	 */
	datacmp get_best_feature( List<List<Double>> fset , List<Integer> index ,
			List<Double> num , int classnum )  
	{
		int featurenum = fset.get(0).size() ;
		double max = -1 ;
		int maxindex = 0 ;
//		int count[] = new int[classnum] ;
//		int total[] = new int[classnum] ;
		Map<Double,Integer> count = new HashMap<Double,Integer>() ;
		Map<Double,Integer> total = new HashMap<Double,Integer>() ;
		for( int j = 0 ; j < index.size() ; j++ ){
			Double key = num.get( index.get(j) ) ;
			if( total.containsKey( num.get( index.get(j) ) )){
				total.put( key , total.get(key)+1);
			}else{
				total.put( key , 1 );
			}
		}
		
		double entropy1 =0 , entropy2 = 0 , mmentropy = 0 ;
		datacmp result = new datacmp( 0 , -1.0) ;
		
		Iterator iter = total.keySet().iterator();
		while(iter.hasNext())
		{
			double tmpval = (double) iter.next() ;
			double tmp = ((double)total.get(tmpval)) / index.size() ;
			if( tmp != 0 )
				entropy1 += (-tmp)*Math.log(tmp) ;
		}
		
		for( int i =0 ; i < featurenum ; i++)
		{
			ArrayList<datacmp> tmp = new ArrayList<datacmp>() ;
			count.clear();
			iter = total.keySet().iterator() ;
			while( iter.hasNext() ){
				count.put((Double) iter.next(), 0);
			}
			for( int j = 0 ; j < index.size() ; j++ )
			{
				Double val = fset.get( index.get(j)).get(i) ;
				tmp.add( new datacmp( index.get(j) , val ) ) ;
			}
			Collections.sort( tmp , comparator );

			boolean val_bool = false ;
			double val = -1 , maxentropy = -1.0 , maxval = 0 ;
			int maxid = 0 ;
			for( int j= 0 ; j < tmp.size() ; j ++ )
			{
				if( !val_bool )
				{
					val = tmp.get(j).data ;
					val_bool = true ;
				}
				if( val == tmp.get(j).data )  // do not cal the last 
				{
					double tmpval = num.get(tmp.get(j).id ) ;
					if( count.containsKey(tmpval) ){
						count.put(tmpval, count.get(tmpval)+1 ) ;
					}else{
						count.put(tmpval, 1) ;
						System.out.println("error 3");
					}
				}
				else
				{
					entropy2 = 0 ;
					// check
					iter = count.keySet().iterator() ;
					while(iter.hasNext()){
						double tmpval = (double) iter.next();
						double tmp1 = ((double)count.get(tmpval) ) / j ; // j is the total number
						if( tmp1 != 0 )
							entropy2 += (-tmp1)*Math.log(tmp1) * j / index.size() ;
					}
					iter = total.keySet().iterator() ;
					while(iter.hasNext()){
						double tmpval = (double)iter.next() ;
						double tmp1 = ((double)(total.get(tmpval)-count.get(tmpval))) / ( index.size() - j ) ;
						if( tmp1 != 0 ) 
							entropy2 += (-tmp1)*Math.log(tmp1) * ( index.size() - j ) / index.size() ;
					}
//					double split = 0 ;
//					double tmp1 = ((double)j) / index.size() ;
//					split += (-tmp1)*Math.log(tmp1) ;
//					tmp1 = ((double)(index.size()-j)) / index.size() ;
//					split += (-tmp1)*Math.log(tmp1) ;
//					System.out.println( entropy1 +"  "+entropy2);
					if( entropy1 - entropy2 > maxentropy ) /* ID3 */
//					if( (entropy1 - entropy2 )/split > maxentropy ) /* C4.5 */
					{
						maxentropy = entropy1 - entropy2 ;
						maxid = i ;
						maxval = val ;
					}
					val_bool = false ;
					j-- ;
				}
			}
			if( mmentropy < maxentropy )
			{
				result.id = maxid ;
				result.data = maxval ;
				mmentropy = maxentropy ;
			}
		}
		return result ;
	}
}
