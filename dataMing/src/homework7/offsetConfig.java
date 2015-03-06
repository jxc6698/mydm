package homework7;


public class offsetConfig {
	
	
	static double DEFAULTRATE = 0.1;//0.169805;
	
// basic offset 
/*
 * time is 24, not considering date
 */
	/* Boff means every feature's value range */
	static int Boff[] = new int[]{0,0,24,7,7,4842,7912,26,9136,580,36,2895973,
		7338655,8303,5,4,2885,8,9,474,4,69,172,62
	};
	static int Boffset[] =new int[]{0,0,0,24,31,38,4880,12792,12818,21954,22534,22570,2918543,
	10257198,10265501,10265506,10265510,10268395,10268403,10268412,10268886,10268890,
	10268959,10269131,10269193};

	/*
	 * part 2
	 */
//	static int Boff2[] = new int[]{100,100,100,100,100,100,100,100,100,100,100,100};
	static int Boff2[] = new int[]{128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,
		128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,
		128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128,128};
	static int Boffset2[] = new int[65];
	
	

//	static double twoexp[] = new double[]{1.0,0.5,0.25,0.125,0,0625,0.03125,0.015625,0.0078125,
//		0.00390625};
	static double twoexp[] = new double[]{1.0,0.500000,0.250000,0.125000,0.062500,0.031250,0.015625,
		0.0078125,0.00390625,0.001953125,0.0009765625};

/* total feature number 
 * 
 */ 
	static int featurenum = 23;

	static String configFile = "../kaggle/config";

	
/*
 * cart tree
 */
	static int gbdtlabeldiffnum = 100000;
	static int treedepth = 3;
	
/*
 * 	gbdt
 */
	static int gbdttreenum = 30;
	static double leastRate = 0.02;
}




