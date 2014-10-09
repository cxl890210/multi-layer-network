package com.multiLayer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

public class Main {

	public static void main(String[] args) throws IOException
	{
//		���Ʋ���
		int meanDegree = 4;
		int nodeCount = 16000;
		int edgeCount = 2*nodeCount;
		int outEdge = nodeCount;//�ⲿ������
		double attackProb = 0.10;//��ʼ�������������
		double alpha = 0.55;
		double beta = 0.95;
		double theta = 0.95;
		//�������Ŀ¼�ṹ
		String root="../data/MultiLayer-CommunityAttack/meanDegree="+meanDegree+"/cascadeProcess/alpha="+alpha+"/"
				+"beta="+beta+"/test2/";/***/	
		String dir = root+nodeCount+"/";
		String result_dir = dir+"result.txt";
		File file=new File(dir);
		if(!file.exists())
			file.mkdirs();
		else
		{
			System.out.println("director exists !");
//			System.exit(0);
		}
		BufferedWriter bw_result = new BufferedWriter(new FileWriter(result_dir));
		NumberFormat  nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(10);
		AttackModel attack = new AttackModel();
		long begin = System.currentTimeMillis();
		while(attackProb<=1.0)
		{
			model cascadeModel = new model(nodeCount,outEdge,edgeCount,alpha,beta,theta);
//			ģ�������������ȡ�������ͨ��֧�߶�
			attack.attackProcess(cascadeModel.netWork1, cascadeModel.netWork2,cascadeModel.netWork3,attackProb);
			int giantIndex = cascadeModel.netWork1.giantComponentIndex;
			int giantSize = cascadeModel.netWork1.cluster_nodeList.get(giantIndex).size();
			System.out.println("giantSize:"+giantSize);
			double fract = 1.0*giantSize/nodeCount;
			System.out.println("attackProb: "+attackProb+"\t"+"fract: "+fract);
			bw_result.write(attackProb+"\t"+nf.format(fract)+'\n');
			bw_result.flush();
			attackProb+=0.02;
		}
		long end = System.currentTimeMillis();
		System.out.println("USED TIME : "+(end-begin));
		bw_result.close();
	}
}
