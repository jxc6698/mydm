package homework5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DT {
	
	treenode root = null ;
	
	void train( List<List<Double>> set , 
			List<Integer> num , int classnum ,
			int max_depth , int min_node  )
	{
		
		
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
//			if( root == null )
//				root = curt ;
			
			if( cur.node.depth == max_depth )
			{
				int c = get_classnum( cur.index , num ) ;
				curt.classid = c ;
//				System.out.println(c);
				continue ;
			}
			int c = is_one_class( cur.index , num ) ;
			if( c >= 0 )  /* contains only one class */
			{
				curt.classid = c ;
//				System.out.println(c); 
				continue ;
			}
			datacmp d = get_best_feature( set , cur.index , num , classnum ) ;
//			System.out.println( "   " + d.id + "   " + d.data ) ;
			
			int findex = d.id;
			double fval = d.data ;
			if( d.data == -1 )  /* with same feature but different classnum */
			{
				c = get_classnum( cur.index , num ) ;
				curt.classid = c ;
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
	
	void estimate( List<List<Double>> set , 
			List<Integer> num )
	{
		treenode curt = root ;
		for( int i= 0 ; i< set.size() ; i ++)
		{
//			System.out.println("+++++");
			curt = root ;
			while( curt != null )
			{
				if( curt.classid != -1 )
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
	
	int is_one_class( List<Integer> index , List<Integer> num )
	{
		if( index.size() == 0 )
		{
			System.out.println("error ----1");
			System.exit(-1);
		}
		int tmp = num.get( index.get(0) );
		int ret = tmp ;
		for( int i =0;i< index.size() ; i++)
		{
			if( num.get( index.get(i) ) == tmp )
				;
			else
				ret = -1 ;
		}
		return ret ;
	}
	int get_classnum( List<Integer> index , List<Integer> num )
	{
		
		List<Integer> count = new ArrayList<Integer>() ;
		for( int i =0; i<index.size() ; i++)
		{
			int tmp = num.get(index.get(i)) ;
			try{
				count.get( tmp ) ;
			}catch ( IndexOutOfBoundsException e ){
				for(int j = count.size() ; j <= tmp ; j ++)
				{
					count.add( 0 ) ;
				}
			}
			count.set( tmp , count.get( tmp )+1 ) ;
		}
		int tmp = 0 , max=0 ;
		for( int i =0;i< count.size() ; i++ )
		{
			if( count.get(i) > max )
			{
				tmp = i ;
				max = count.get(i) ;
			}
		}
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
			List<Integer> num , int classnum )  
	{
		int featurenum = fset.get(0).size() ;
		double max = -1 ;
		int maxindex = 0 ;
		int count[] = new int[classnum] ;
		int total[] = new int[classnum] ;
		for(int i=0;i<classnum;i++)
			total[i] = 0 ;
		for( int j = 0 ; j < index.size() ; j++ ){
			total[ num.get( index.get(j) ) ] ++ ;
		}
		
		double entropy1 =0 , entropy2 = 0 , mmentropy = 0 ;
		datacmp result = new datacmp( 0 , -1.0) ;
		
		for(int j=0;j<classnum ; j++)
		{
			double tmp = ((double)total[j]) / index.size() ;
			if( tmp != 0 )
				entropy1 += (-tmp)*Math.log(tmp) ;
		}
		
		for( int i =0 ; i < featurenum ; i++)
		{
			ArrayList<datacmp> tmp = new ArrayList<datacmp>() ;

			for(int j=0;j<classnum;j++)
				count[j] = 0 ;
			
			
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
					count[ num.get(tmp.get(j).id) ] ++  ;
				}
				else
				{
					entropy2 = 0 ;
					// check
					for( int k=0;k < classnum ; k ++ )
					{
						double tmp1 = ((double)count[k]) / j ; // j is the total number
						if( tmp1 != 0 )
							entropy2 += (-tmp1)*Math.log(tmp1) * j / index.size() ;
						tmp1 = ((double)(total[k]-count[k])) / ( index.size() - j ) ;
						if( tmp1 != 0 ) 
							entropy2 += (-tmp1)*Math.log(tmp1) * ( index.size() - j ) / index.size() ;
					}
					double split = 0 ;
					double tmp1 = ((double)j) / index.size() ;
					split += (-tmp1)*Math.log(tmp1) ;
					tmp1 = ((double)(index.size()-j)) / index.size() ;
					split += (-tmp1)*Math.log(tmp1) ;
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
