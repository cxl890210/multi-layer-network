package com.multiLayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class NumIteration {

	public static ArrayList<Double> iterSolution(int[] comMeanDegreeA,int[] comMeanDegreeB,double prob,double[] u,double[] v
			,double alpha,double beta)
	{
		int eps = 20;
		int step = 0;
		double[] currentU,lastU;
		double[] currentV,lastV;
		currentU=lastU=u;
		currentV=lastV=v;
		int len = comMeanDegreeA.length;
		ArrayList<Double> result = new ArrayList<Double>();
		while(step<eps)
		{
			for(int i=0;i<len;i++)
			{
				currentU[i] = Math.exp(-alpha*prob*comMeanDegreeA[i]*(lastU[i]-1)*(lastV[i]-1)-(1-alpha)*prob*comMeanDegreeA[i]*
						())
			}
			
		}
		return result;
	
	}
}
