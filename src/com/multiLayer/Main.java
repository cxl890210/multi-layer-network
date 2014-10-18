package com.multiLayer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws IOException
	{
		//控制参量
		int nodeCount = 50000;
//		int meanDegree1 = 8;
//		int meanDegree2 = 6;
//		int meanDegree3 = 4;
//		int edgeCount = 4*nodeCount;//
		ArrayList<Integer> edgeCount = new ArrayList<Integer>();
		for(int i=2;i<5;i++)
		{
			edgeCount.add(3*nodeCount);
		}
		for(Integer j:edgeCount)
			System.out.println(j);
		int outEdge = nodeCount;//外部连边数
		int fileNum = 10;
		double beta = 0.95;
		double theta = 0.95;
		//设置输出目录结构
		String root="../data/MultiNetwork-CommAttack/multiMeanDegree/nodeCount="+nodeCount+"/cascadeProcess/criticalProb/";	
		NumberFormat  nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(10);
		//	建立攻击策略
		AttackModel attack = new AttackModel();
		for(int i=0;i<fileNum;i++)
		{
			double alpha = 0.0;
//			double theta = 0.0;
			System.out.println("MODEL: "+i);
			String dir = root+i+"/";
			String result_dir = dir+"result.txt";
			File file=new File(dir);
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
//			while(theta<1.0){
//				beta = 0;
//				while (theta<1.0) 
				{
//					alpha = 0;
					while(alpha<1.0){
						double highProb = 0.8;
						double lowProb = 0.2;
						double midProb = (highProb+lowProb)/2;
						double eps = 0.01;
						double threshold = 0.1;
						double criticalProb;
						model cascadeModel;
						int giantIndex;
						int giantSize;
						double fract;
						//构建三层网络
						cascadeModel = new model(nodeCount,outEdge,edgeCount,alpha,beta,theta);
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
								cascadeModel = new model(nodeCount,outEdge,edgeCount,alpha,beta,theta);
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
								System.out.println("alpha: "+alpha+"\t"+"beta: "+beta+"\t"+"theta: "+theta+"\t"+"fract: "+fract);
								System.out.println("lowProb："+lowProb+"\t"+"highProb: "+highProb);
							}
							criticalProb = midProb;
						}
						System.out.println("alpha: "+alpha+"\t"+"beta: "+beta+"\t"+"theta: "+theta+"\t"+"criticalProb: "+criticalProb);
						System.out.println("lowProb："+lowProb+"\t"+"highProb: "+highProb);
						bw_result.write(alpha+"\t"+beta+'\t'+theta+"\t"+criticalProb+"\n");
						bw_result.flush();
						if(alpha<0.89)
						alpha+=0.1;
						else 
							alpha+=0.02;
					}
//					if(theta<0.89)
//					theta+=0.1;
//					else 
//						theta+=0.04;
				}
//				if(theta<0.89)
//					theta+=0.3;
//				else 
//				theta+=0.04;
//			}
			long end = System.currentTimeMillis();
			System.out.println("USED TIME : "+(end-begin));
			bw_result.close();
		}
	}
}
