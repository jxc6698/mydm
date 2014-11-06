package homework6;

import homework5.DT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class randomForest {
	
	int forestsize = 10 ;
	DT dt[] = new DT[forestsize] ;
	
	public void train( List<List<Double>> set , 
			List<Double> num , int classnum ,
			int max_depth , int min_node , int c_r )
	{
		for(int i =0;i < this.forestsize ; i ++ )
		{
			dt[i] = new DT() ;
			dt[i].train(set, num, classnum, max_depth+10*i, min_node, c_r);
		}
		return ;
	}
	
	
	public void estimate( List<List<Double>> set , 
			List<Double> num )
	{
		List<Double> tmpnum[] = new List[ this.forestsize ] ;
		for(int i=0;i<this.forestsize;i++)
			tmpnum[i] = new ArrayList<Double>() ;
		for( int i =0;i<this.forestsize;i++)
		{
			dt[i].estimate(set, tmpnum[i]);
		}
		Map<Double,Integer> map = new HashMap<Double,Integer>() ;
		for( int j = 0 ; j < set.size() ;j ++)
		{
			map.clear();
			for( int i =0;i<this.forestsize;i++)
			{
				Double tmpval = tmpnum[i].get(j) ;
				if( map.containsKey(tmpval) )
					map.put(tmpval,  map.get(tmpval)+1 ) ;
				else
					map.put(tmpval, 1);
			}
			Iterator<Double> iter = map.keySet().iterator();
			Double val = 0.0 ;
			int maxcount = -1 ;
			while(iter.hasNext())
			{
				Double tmp = iter.next() ;
				if( map.get(tmp) > maxcount )
				{
					val = tmp ;
					maxcount = map.get(tmp);
				}
			}
			num.add(val);
		}
		return ;
	}
}
