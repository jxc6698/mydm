package homework5;

public class treenode {
	int classid ;
	treenode left , right ;
	int depth ;
	double value ;
	int featureid ;
	treenode()
	{
		left = right = null ;
		this.classid = -1 ;
	}
}
