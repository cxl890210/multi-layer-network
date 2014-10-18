package com.multiLayer.ER;

import java.io.BufferedWriter;
import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

public class MultiCommMain {
	public static void main(String[] args) throws IOException
	{
		//控制参量
		int nodeCount = 18000;
		ArrayList<Integer> edgeCount = new ArrayList<Integer>();
		for(int i=2;i<5;i++) edgeCount.add(3*nodeCount);
		ArrayList<ArrayList<ArrayList<Double>>> param = new ArrayList<ArrayList<ArrayList<Double>>>();
		//从文件读入调控参数
		String paramFile = "./param.txt";
		BufferedReader paramReader = new BufferedReader(new FileReader(paramFile));
		String paramLine = paramReader.readLine();
		int netNum = 0;
		while(paramLine!=null)
		{
			String[] paramArray = paramLine.split("\t");
			if(paramLine.startsWith("net"))
			{
				netNum = Integer.parseInt(paramArray[1]);
				ArrayList<ArrayList<Double>> tuneParam = new ArrayList<>();
				param.add(tuneParam);
			}
			else {
				ArrayList<Double> paramList = new ArrayList<Double>();
				param.get(netNum).add(paramList);
				for(int i=0;i<paramArray.length;i++)
					paramList.add(Double.parseDouble(paramArray[i]));
			}
			paramLine = paramReader.readLine();
		}
		//设置输出目录结构
		String root="../data/MultiNetCommAttack/nodeCount="+nodeCount+"/cascadeProcess/criticalProb/";	
		NumberFormat  nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(10);
		//	建立攻击策略
		MultiCommAttackModel  attack = new MultiCommAttackModel();
//		for(int i=0;i<fileNum;i++)
		{
			ArrayList<Double> alpha = param.get(0).get(0);
			ArrayList<Double> beta = param.get(0).get(1);
//			alpha.clear();
//			String dir = root+i+"/";
			String result_dir = root+"result.txt";
			String log_dir = root+"log.txt";
			File file=new File(root);
			if(!file.exists())
				file.mkdirs();
			else
			{
				System.out.println("director exists !");
				System.exit(0);
			}
			BufferedWriter bw_result = new BufferedWriter(new FileWriter(result_dir));
			//测试运行时间		
			long begin = System.currentTimeMillis();
			while(alpha.get(0)<1.0){
				alpha.set(1, 1-alpha.get(0));
				beta.set(1, 1-alpha.get(0));
				double highProb = 0.8;
				double lowProb = 0.2;
				double midProb = (highProb+lowProb)/2;
				double eps = 0.01;
				double threshold = 0.1;
				double criticalProb;
				TripleCommModle cascadeModel;
				int giantIndex;
				int giantSize;
				double fract;
				//构建三层网络
				cascadeModel = new TripleCommModle(nodeCount,edgeCount,param.get(0),param.get(1),param.get(2),2);
				//模拟随机攻击，并取得最大连通分支尺度
				attack.attackProcess(cascadeModel.netWork1, cascadeModel.netWork2,cascadeModel.netWork3,lowProb);
				giantIndex = cascadeModel.netWork1.giantComponentIndex;
				giantSize = cascadeModel.netWork1.cluster_nodeList.get(giantIndex).size();
				fract = 1.0*giantSize/nodeCount;
				if(fract>threshold)
					criticalProb = 0;
				else {
					while(highProb-lowProb>eps)
					{
						//构建三层网络	
						cascadeModel = new TripleCommModle(nodeCount,edgeCount,param.get(0),param.get(1),param.get(2),2);
						//模拟随机攻击，并取得最大连通分支尺度
						attack.attackProcess(cascadeModel.netWork1, cascadeModel.netWork2,cascadeModel.netWork3,midProb);
						giantIndex = cascadeModel.netWork1.giantComponentIndex;
						giantSize = cascadeModel.netWork1.cluster_nodeList.get(giantIndex).size();
						fract = 1.0*giantSize/nodeCount;
						if(fract>threshold)
							highProb = midProb;
						else 
							lowProb = midProb;
						midProb = (highProb+lowProb)/2;
//						System.out.println("alpha: "+alpha+"\t"+"beta: "+beta+"\t"+"theta: "+theta+"\t"+"fract: "+fract);
						System.out.println("lowProb："+lowProb+"\t"+"highProb: "+highProb);
					}
					criticalProb = midProb;
				}
				System.out.println("alpha: "+alpha+"\t"+"criticalProb: "+criticalProb);
				bw_result.write(alpha.get(0)+"\t"+criticalProb+"\n");
				bw_result.flush();
				double alpha0 = alpha.get(0);
				if(alpha0<0.89)
					alpha0+=0.1;				
				else 
					alpha0+=0.02;
				alpha.set(0, alpha0);
				beta.set(0, alpha0);
			}
	long end = System.currentTimeMillis();
	System.out.println("USED TIME : "+(end-begin));
	bw_result.close();
	}
	}
}
