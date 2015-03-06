package homework6;

import homework5.DT;
import homework5.DT;
import homework5.cartTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class adaboost {

	
	int weak_classifier = 100 ;
	double delta[] = new double[weak_classifier];
//	DT[] cla = new DT[weak_classifier];
	cartTree cla[] = new cartTree[weak_classifier];
	int classnum = 0;
	
	public void setWeakClassifier( int n ){
		this.weak_classifier = n;
		this.delta = new double[this.weak_classifier];
	}
	
	public void train( List<List<Double>> set , 
			List<Double> num , int classnum )
	{
		this.classnum = classnum;
		List<Double> weight = new ArrayList<Double>() ;
		for( int i=0;i < set.size() ; i++ ) {
			weight.add( 1.0/set.size() );
		}
		int totalnum = set.size();
		
		List<List<Double>> newset = new ArrayList() ;
		List<Integer> newindex = new ArrayList();
		List<Double> estnum = new ArrayList();
		List<Double> newnum = new ArrayList();
		for( int i =0;i<weak_classifier ; i ++ ) {
//			System.out.println(i);
			newset.clear();
			estnum.clear();
			newindex.clear();
			newnum.clear();
			getnewset( set , weight , newset , num , newnum , newindex );
			
//			DT dt = new DT();
			cartTree dt = new cartTree();
			dt.train(newset, newnum, classnum, 30, -1, 0);
			cla[i] = dt;
			dt.estimate(set, estnum);
			
//			for( int j=0;j< set.size();j++) {
//				System.out.println( newnum.get(j) + "  "+estnum.get(j) );
//			}
//			if( i== 0)
//				break ;
			
			double tmpw = 0;
			int totaln = 0 ;
			for( int j=0;j<estnum.size();j++) {
				if( !num.get(j).equals( estnum.get(j) ) )
					tmpw += weight.get(j);
				else 
					totaln ++;
			}
			System.out.println(totaln + "  " + tmpw );
			if( tmpw == 0 )
				System.out.println("error 3");
			if( tmpw > 0.99 )
				System.out.println("error 1");
			if( tmpw > 0.5 ) {
				System.out.println("error 4  " + i );
//				weak_classifier = i;
//				break;
//				i -- ;
//				continue;
			}
			delta[i] = 1.0/2 * Math.log( (1-tmpw)/tmpw );
			System.out.println(delta[i]);
			for( int j=0;j<set.size();j++) {
				if( num.get(j).equals( estnum.get(j) ) ) {
					weight.set( j, weight.get(j) / Math.exp(delta[i]) );
				} else {
					weight.set( j, weight.get(j) * Math.exp(delta[i]) );
				}
			}
			double totalw = 0;
			for( int j=0;j<num.size();j++)
				totalw += weight.get(j);

			for( int j=0;j<num.size();j++) {
				weight.set(j, weight.get(j)/totalw );
			}
		}
		return ;
	}

	public void getnewset( List<List<Double>> set,
			List<Double> weight,
			List<List<Double>> newset, 
			List<Double> num , List<Double> newnum ,
			List<Integer> newindex )
	{
		Random r = new Random();
		while( newset.size() < set.size() ){
			double tmp = r.nextDouble();
			int c = 0;
			double tmpt = 0;
			while(true){
//				System.out.println("tmpt  " + tmpt);
				tmpt += weight.get(c);
				if( tmp <= tmpt) {
					newset.add( set.get(c) );
					newnum.add( num.get(c));
					newindex.add(c);
					break;
				}
				c++;
			}
		}
		return;
	}

	
	public void estimate( List<List<Double>> set , 
			List<Double> num )
	{
		List<Map<Double,Double>> tmpres = new ArrayList();
		for(int i=0;i<set.size();i++) {
			tmpres.add(new HashMap<Double,Double>());
		}
		List<Double> tmpnum = new ArrayList<Double>();
		for( int i =0;i<weak_classifier ; i ++ ) {
			tmpnum.clear();
			cla[i].estimate(set, tmpnum);
			Map<Double,Double> array = null ;
			for( int j=0;j<tmpnum.size();j++) {
				array = tmpres.get(j);
				double classres = tmpnum.get(j);
				if( array.containsKey(classres) ) {
					array.put(classres , array.get(classres)+delta[i]*1 );
				} else {
					array.put(classres , delta[i]*1 );
				}
			}
		}
		for( int i=0;i<set.size();i++) {
			Iterator iter = tmpres.get(i).entrySet().iterator();
			double max = -1;
			double index = -1;
			while(iter.hasNext()) {
				Map.Entry<Double, Double> entry = (Entry<Double, Double>) iter.next();
				double tmpval = entry.getValue();
				if( tmpval > max ) {
					max = tmpval;
					index = entry.getKey();
				}
			}
			if( index == -1)
				System.out.println("error 2");
			num.add(index);
		}
		return ;
	}
	
}
