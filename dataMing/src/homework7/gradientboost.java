package homework7;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class gradientboost {
	int classifier_num = offsetConfig.gbdttreenum;
	double delta[] = new double[classifier_num];
//	DT cla[][] = null ;
	cartTree cla[] = null;
	int classnum = 0;
//	double alpha = 0.4;
	
	public gradientboost(){
		for(int i=0;i<classifier_num;i++) {
			delta[i] = 0;
		}
	}
	
	public void setWeakClassifier( int n ){
		this.classifier_num = n;
		this.delta = new double[this.classifier_num];
	}
	
	// for locality, [feature][instance]
	public void train( short[][] set, int fsparse[][],
			double[] num , int classnum, int max_depth, int samplenumber )
	{
		if( this.cla == null ) {
			this.cla = new cartTree[classifier_num];
		}
		this.classnum = classnum;
		// init weight 
		
		
		int totalnum = samplenumber;
		
		double estnum[] = new double[samplenumber];
		double newnum[] = new double[samplenumber];

		for(int i=0;i < samplenumber; i ++) {
			newnum[i] = num[i];
//			newnum[i] = (num[i]-0.5)*2;
		}
		
		for( int i =0;i<classifier_num ; i ++ ) {

			cartTree dt = new cartTree();
			dt.train( set,fsparse, newnum, max_depth, i, 1);
			cla[i] = dt;
			System.out.println("tree "+i+" train finished " + new Date() );
			
			dt.estimate(set,fsparse, estnum);
			
			double tt = 0,ttf;
			for(int k=0;k<samplenumber;k++) {
				if(estnum[k] != 0) {
					tt = newnum[k] / estnum[k];
					ttf = Math.abs(tt);
					delta[i] = (ttf<delta[i])?ttf:delta[i];
				}
			}
			double lRate = offsetConfig.leastRate;
			delta[i] = (lRate>delta[i])?lRate:delta[i];
			delta[i] = (2<delta[i])?2:delta[i];
			System.out.println("delta "+i+ " is "+delta[i] );
			for(int k=0;k<samplenumber;k++) {
				newnum[k] = newnum[k] - delta[i] * estnum[k];
			}

			System.out.println("tree "+i+" estimate finished" + new Date());
			System.gc();
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
	
	
	public void estimate( short[][] set, int fsparse[][],
			double[] num )
	{
		double[] tmpnum = new double[num.length];
		double[] resnum = new double[num.length];
			
		for( int i =0;i<classifier_num ; i ++ ) {
			// no need
			for(int j=0;j<tmpnum.length;j++)
				tmpnum[i]=0;
			
			cla[i].estimate(set, fsparse, tmpnum);
			for(int j=0;j<tmpnum.length;j++) {
				resnum[j] = resnum[j] + delta[i] * tmpnum[j];
			}
		}
		for( int i=0;i<num.length;i++) {
			double max = -1;
			double index = -1;
			if( resnum[i] > 0.5 )
//			if( resnum[i] > 0 )
				num[i] = 1;
			else
				num[i] = 0;
		}
		return ;
	}
	
	public void estimatefeature( short[][] set, int fsparse[][],
			int num[][] )
	{
		// num.length == set[0].length
		int[] tmpnum = new int[num.length];
		for(int k=0;k<num.length;k++) {
			num[k] = new int[ classifier_num ];
		}
		for( int i =0;i<classifier_num ; i ++ ) {
			// no need
			tmpnum = new int[num.length];
			
			cla[i].estimatefeature(set, fsparse, tmpnum);
			for(int j=0;j<tmpnum.length;j++) {
				num[j][i] = tmpnum[j];
			}
		}
		return ;
	}
	
}