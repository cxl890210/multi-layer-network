package com.multiLayer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class model {
	public int nodeCount;//ϵͳ�߶�
//	˫������
	public NetWork netWork1;
	public NetWork netWork2;
	public NetWork netWork3;
	public int lager_size;
	public int smaller_size;
//	������������
	public model(int nodeCount,int outEdge,int edgeCount,double alpha,double beta,double theta) throws IOException
	{
		this.nodeCount = nodeCount;
		//��ʼ����������
		netWork1 = new NetWork(nodeCount,alpha,1);
		netWork2 = new NetWork(nodeCount,beta,2);
		netWork3 = new NetWork(nodeCount,theta,3);
		produceNetWork(netWork1,edgeCount);
		System.out.println("Construct Network Finish!");
		produceNetWork(netWork2,edgeCount);
		System.out.println("Construct Network Finish!");
		produceNetWork(netWork3, edgeCount);
		System.out.println("Construct Network Finish!");
	}
//	����֮���һһ����(ȫ���)
	public void interConnctFunction(NetWork netWork1,NetWork netWork2) throws IOException
	{
		for(int i=0;i<nodeCount;i++)
		{
			Point net1Point = netWork1.points[i];
			Point net2Point = netWork2.points[i];
			netWork1.interConnect.put(i,i);
			netWork2.interConnect.put(i, i);
			netWork1.outLinkNodes.add(net1Point);
			netWork2.outLinkNodes.add(net2Point);
		}
	}
	public void produceNetWork(NetWork netWork,int edgeCount) throws IOException
	{
		add(netWork,edgeCount);
	}
	//�������
	public  void add(NetWork netWork,int edgeCount) throws IOException
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

	public Edge produceEdge(NetWork netWork) throws IOException
	{
		Edge edge=null;
		Point[] points = netWork.points;
		double alpha = netWork.alpha;
		Random random = new Random();
		int domainNode;//���ڵ���
		int secondNode;//�ӽڵ���
		domainNode=random.nextInt(nodeCount);
		Point domainPoint = points[domainNode];
		Point secondPoint;
		int communityNum = domainPoint.communityNumber;
//		�ڶ����ڵ���alpha�����Լ�����,�Ը���1-alpha����������
		double r = random.nextDouble();
		if(r<alpha)
		{//�����Լ�����
			int beginCount = netWork.community[communityNum].beginCount;
			int communitySize = netWork.community[communityNum].communitySize;
			while(true)
			{
				secondNode = random.nextInt(communitySize)+beginCount;
				secondPoint = points[secondNode];
				if(secondNode!=domainNode&&!domainPoint.adjVector.contains(secondPoint))
					break;
			}
		}
		else
		{//�����ⲿ����
			int beginCount = netWork.community[1-communityNum].beginCount;
			int communitySize = netWork.community[1-communityNum].communitySize;
			while(true)
			{
				secondNode = random.nextInt(communitySize)+beginCount;
				secondPoint = points[secondNode];
				if(!domainPoint.adjVector.contains(secondPoint))
					break;
			}
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
	public void merge(Point point1,Point point2,NetWork netWork) throws IOException
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

