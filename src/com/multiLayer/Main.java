package com.multiLayer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class Main {
	public static void main(String[] args) throws IOException
	{
		//控制参量
		int nodeCount = 45000;
		int edgeCount = 2*nodeCount;
		int outEdge = nodeCount;//外部连边数
		double alpha = 0.0;
		double beta = 0.0;
		double theta = 0.0;
		int fileNum = 10;
		//设置输出目录结构
		String root="../data/MultiNetwork-CommAttack/nodeCount="+nodeCount+"/cascadeProcess/criticalProb/";	
		NumberFormat  nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(10);
		for(int i=0;i<fileNum;i++)
		{
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
			//	建立攻击策略
			AttackModel attack = new AttackModel();
			//测试运行时间		
			long begin = System.currentTimeMillis();
			while(theta<1.0){
				while (beta<1.0) {
					while(alpha<1.0){
						double highProb = 0.8;
						double lowProb = 0.1;
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
						alpha+=0.1;
					}
					beta+=0.1;
				}
				theta+=0.1;
			}
			long end = System.currentTimeMillis();
			System.out.println("USED TIME : "+(end-begin));
			bw_result.close();
		}
	}
}
