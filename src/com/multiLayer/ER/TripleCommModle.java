package com.multiLayer.ER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class TripleCommModle {

	public int nodeCount;//ϵͳ�߶�
//	�������
	public TripleCommNet netWork1;
	public TripleCommNet netWork2;
	public TripleCommNet netWork3;
	public int lager_size;
	public int smaller_size;
//	���ɶ������
	public TripleCommModle(int nodeCount,ArrayList<Integer> edgeCount,ArrayList<ArrayList<Double>> param1,
			ArrayList<ArrayList<Double>> param2,
			ArrayList<ArrayList<Double>> param3,int commNum) throws IOException
	{
		this.nodeCount = nodeCount;
		//��ʼ����������
		netWork1 = new TripleCommNet(nodeCount,param1,1,commNum);
		netWork2 = new TripleCommNet(nodeCount,param2,2,commNum);
		netWork3 = new TripleCommNet(nodeCount,param3,3,commNum);
		produceNetWork(netWork1,edgeCount.get(0));
		produceNetWork(netWork2,edgeCount.get(1));
		produceNetWork(netWork3, edgeCount.get(2));
		System.out.println("Construct Network Finish!");
	}
	public void produceNetWork(TripleCommNet netWork,int edgeCount) throws IOException
	{
		add(netWork,edgeCount);
	}
	//�������
	public  void add(TripleCommNet netWork,int edgeCount) throws IOException
	{
		int step=0;
		while(step<=edgeCount)
		{
			Edge addedge=null;
			addedge = produceEdge(netWork);
			if(addedge.inter==true)//����Ǽ��ż����������������ں�
			{   
				Point point1 = addedge.getPoint1();
				Point point2 = addedge.getPoint2();
				merge(point1,point2,netWork);
			}
			step++;
		}
	}

	public Edge produceEdge(TripleCommNet netWork) throws IOException
	{
		Edge edge=null;
		Point[] points = netWork.points;
		ArrayList<ArrayList<Double>> param = netWork.param;//����֮�����ϲ���
		Random random = new Random();
		int domainNode;//���ڵ���
		int secondNode;//�ӽڵ���
		domainNode=random.nextInt(nodeCount);
		Point domainPoint = points[domainNode];
		Point secondPoint;
		int communityNum = domainPoint.communityNumber;
		ArrayList<Double> paramList = param.get(communityNum);
		//	�ڶ����ڵ���alpha�����Լ�����,�Ը���1-alpha����������
		double r = random.nextDouble();
		double cum =0.0;
		int sampleIntern = 0;
		for(int i=0;i<paramList.size();i++)
		{
			cum+=paramList.get(i);
			if (r<=cum) {sampleIntern=i;break;}
		}
		int beginCount = netWork.community.get(sampleIntern).beginCount;
		int communitySize = netWork.community.get(sampleIntern).communitySize;
		while(true)
		{
			secondNode = random.nextInt(communitySize)+beginCount;
			secondPoint = points[secondNode];
			if(secondNode!=domainNode&&!domainPoint.adjVector.contains(secondPoint))
				break;
		}
		secondPoint = points[secondNode];
		Boolean inter=true;//�Ƿ��ż�����
//		�໥��������
		domainPoint.adjVector.add(secondPoint);
		secondPoint.adjVector.add(domainPoint);
		int branch_number1=domainPoint.branchNumber;
		int branch_number2=secondPoint.branchNumber;
		if(branch_number1==branch_number2)inter=false;
		edge=new Edge(domainPoint,secondPoint,inter);	
		return edge;
	}	
	public void merge(Point point1,Point point2,TripleCommNet netWork) throws IOException
	{
		int lager_branch=0;//��¼�ϴ���ͨ��֧��
		int smaller_branch=0;
		ArrayList<Integer> lager_cluster;//����
		ArrayList<Integer> smaller_cluster;//С����
		int clusterSize1=netWork.cluster_nodeList.get(point1.branchNumber).size();
		int clusterSize2=netWork.cluster_nodeList.get(point2.branchNumber).size();
		if(clusterSize1<=clusterSize2)
		{lager_branch=point2.branchNumber;
		 smaller_branch=point1.branchNumber;
		 lager_size=clusterSize2;
		 smaller_size=clusterSize1;
		}
		else{ 
			lager_branch=point1.branchNumber;
			smaller_branch=point2.branchNumber;
			lager_size=clusterSize1;
			smaller_size=clusterSize2;
		}
		lager_cluster=netWork.cluster_nodeList.get(lager_branch);
		smaller_cluster=netWork.cluster_nodeList.get(smaller_branch);
		//����С��֧����������֧���
		for(Integer index:smaller_cluster)
		{
			netWork.points[index].branchNumber=lager_branch;
		}
		lager_cluster.addAll(smaller_cluster);
		netWork.cluster_nodeList.put(lager_branch, lager_cluster);
		netWork.cluster_nodeList.remove(smaller_branch);//ɾ�����ű��Ϊsmaller_branch�ļ���	
	}
}
