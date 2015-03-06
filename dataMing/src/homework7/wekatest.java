package homework7;
import weka.classifiers.*;
import weka.core.converters.ConverterUtils.DataSource;

import weka.classifiers.rules.DTNB;
import weka.core.Attribute;
import weka.core.FastVector; 
import weka.core.Instance; 
import weka.core.Instances;


public class wekatest {
	public static void main(String argv[])
	{
		
		Attribute Attribute1 = new Attribute("f1");
		Attribute Attribute2 = new Attribute("f2");
		
		FastVector fvn = new FastVector(3);
		fvn.addElement("blue");
		fvn.addElement("gray");
		fvn.addElement("black");
		Attribute attribute3 = new Attribute("f3", fvn);
		
		FastVector fres = new FastVector(2);
		fres.addElement("positive");
		fres.addElement("negative");
		Attribute classattr = new Attribute("theClass", fres);
		
		FastVector fvw = new FastVector(4);
		fvw.addElement(Attribute1);
		fvw.addElement(Attribute2);
		fvw.addElement(attribute3);
		fvw.addElement(classattr);
		
		
		Instances isTrainingSet = new Instances("Rel", fvw , 10);
		
		isTrainingSet.setClassIndex(3);
		
		Instance iExample = new Instance(4);
		iExample.setValue((Attribute)fvw.elementAt(0), 1.0);
		iExample.setValue((Attribute)fvw.elementAt(1), 0.5);
		iExample.setValue((Attribute)fvw.elementAt(2), "gray");
		iExample.setValue((Attribute)fvw.elementAt(3), "positive");
		
		isTrainingSet.add(iExample);
		Classifier cModel = (Classifier)new DTNB();
		try {
			cModel.buildClassifier(isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Evaluation eTest = null;
		try {
			eTest = new Evaluation(isTrainingSet);
			eTest.evaluateModel(cModel, isTrainingSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);
		
		double[][] cmMatrix = eTest.confusionMatrix();
		for(int row_i=0; row_i<cmMatrix.length; row_i++) {
			for(int col_i=0; col_i < cmMatrix.length; col_i++) {
				System.out.print(cmMatrix[row_i][col_i]);
				System.out.print("|");
			}
			System.out.println();
		}
		
		
		return;
	}
}
