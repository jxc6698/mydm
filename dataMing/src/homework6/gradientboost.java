package homework6;

import homework5.DT;
import homework5.cartTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class gradientboost {
	int classifier_num = 100 ;
	double delta[] = new double[classifier_num];
//	DT cla[][] = null ;
	cartTree cla[][] = null;
	int classnum = 0;
	double alpha = 0.01;
	
	public gradientboost(){
		for(int i=0;i<classifier_num;i++) {
			delta[i] = 0.01;
		}
	}
	
	public void setWeakClassifier( int n ){
		this.classifier_num = n;
		this.delta = new double[this.classifier_num];
	}
	
	public void train( List<List<Double>> set , 
			List<Double> num , int classnum )
	{
		if( this.cla == null ) {
//			this.cla = new DT[classnum][];
			this.cla = new cartTree[classnum][];
			for(int i=0;i<classnum;i++) {
//				cla[i] = new DT[ classifier_num ];
				cla[i] = new cartTree[ classifier_num ];
			}
		}
		this.classnum = classnum;

		int totalnum = set.size();
		
		List<Double> estnum = new ArrayList();
		List<Double> newnum = new ArrayList();
		Set<Integer> countset = new HashSet();
		for( int k=0;k<classnum;k++) {
			newnum.clear();estnum.clear();
			extract_class( num , newnum , k );
			for( int i=0;i<set.size();i++ ) {
				estnum.add(0.0);
			}
			System.out.println("class : " + k);
			for( int i =0;i<classifier_num ; i ++ ) {
				
				for(int j=0;j<set.size();j++) {
					newnum.set(j, newnum.get(j) - alpha * estnum.get(j) );
				}
				estnum.clear();
				
//				reflash_class( num , newnum , i ) ;
//				DT dt = new DT();
				cartTree dt = new cartTree();
				dt.train( set, newnum, classnum, 50, -1, 1);
				cla[k][i] = dt;
				dt.estimate(set, estnum);
				
			}
		}
		return ;
	}

	public void extract_class( List<Double> num , List<Double> newnum ,
			double classid )
	{
		for(int i=0;i<num.size();i++) {
			if( num.get(i).equals( classid ) ) {
				newnum.add( 1.0);
			} else {
				newnum.add( 0.0 );
			}
		}
		return;
	}
	
	
	public void estimate( List<List<Double>> set , 
			List<Double> num )
	{
		List<List<Double>> tmpres = new ArrayList();
		List<Double> tmpnum = new ArrayList<Double>();
		List<Double> resnum = null ;
		for( int k=0;k<classnum;k++) {
			resnum = new ArrayList<Double>();
			for( int j=0;j<set.size();j++) {
				resnum.add(0.0);
			}
			for( int i =0;i<classifier_num ; i ++ ) {
				tmpnum.clear();
				cla[k][i].estimate(set, tmpnum);
				for(int j=0;j<tmpnum.size();j++) {
					resnum.set(j, resnum.get(j) + alpha * tmpnum.get(j));
				}
			}
			tmpres.add(resnum);
		}
		for( int i=0;i<set.size();i++) {
			double max = -1;
			double index = -1;
			for(int j=0;j<classnum;j++) {
				
				double tmpval = tmpres.get(j).get(i);
				if( tmpval > max ) {
					max = tmpval;
					index = j;
				}
			}
			if( index == -1)
				System.out.println("error 2");
			num.add(index);
		}
		return ;
	}
	
}
