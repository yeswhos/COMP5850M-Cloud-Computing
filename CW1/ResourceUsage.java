/**
 * VM Example class - Resourec usage
 *
 * @author Karim Djemame
 * @version 1.0 [2018-02-16]
 *
 */

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.util.*;
import java.util.ArrayList;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

import java.text.DecimalFormat;

public class ResourceUsage{

	private OneResponse rc; 
	private static DecimalFormat df2 = new DecimalFormat(".##");

	/**
	 * Prints out all the host information available. Along with adding the HOST IDs to a array for future use
	 * @param oneClient
	 */
	public void retrieveInformation(Client oneClient)
	{
		ArrayList <HOSTPERF> arrHost = new ArrayList<HOSTPERF>();
	
		try
		{
			HostPool pool = new HostPool( oneClient );
			pool.info();

			double cpuUsage, memUsage, diskUsage;
			for( Host host: pool)
			{
				rc = host.info();
				cpuUsage = (Double.parseDouble(host.xpath("/HOST/HOST_SHARE/CPU_USAGE"))/Double.parseDouble(host.xpath("/HOST/HOST_SHARE/MAX_CPU")))*100;
				memUsage = (Double.parseDouble(host.xpath("/HOST/HOST_SHARE/MEM_USAGE"))/Double.parseDouble(host.xpath("/HOST/HOST_SHARE/MAX_MEM")))*100;
				diskUsage = (Double.parseDouble(host.xpath("/HOST/HOST_SHARE/DISK_USAGE"))/Double.parseDouble(host.xpath("/HOST/HOST_SHARE/MAX_DISK")))*100;
				
				int numVM = Integer.parseInt(host.xpath("/HOST/HOST_SHARE/RUNNING_VMS"));
				
				arrHost.add(new HOSTPERF(Integer.parseInt(host.xpath("/HOST/ID")), (host.xpath("/HOST/NAME")).toString(), cpuUsage, memUsage, diskUsage, numVM));
	
			}
			
			// System.out.println("Physical Hosts with resource usage:");
			// System.out.println("HOSTID\tCPU Usage\tMem Usage\tDisk Usage\tVMs");
			double[] host1 = new double[5];
			double[] host2 = new double[5];
			double[] host3 = new double[5];
			double[] host4 = new double[5];

			for(HOSTPERF h: arrHost)
			{
				switch(h.HOSTID){
					case 6:
						host1 = new double[]{h.HOSTID, h.HostCpuUsage, h.HostMemUsage, h.HostDiskUsage, h.NumVM};
					case 7:
						host2 = new double[]{h.HOSTID, h.HostCpuUsage, h.HostMemUsage, h.HostDiskUsage, h.NumVM};
					case 9:
						host3 = new double[]{h.HOSTID, h.HostCpuUsage, h.HostMemUsage, h.HostDiskUsage, h.NumVM};
					case 21:
						host4 = new double[]{h.HOSTID, h.HostCpuUsage, h.HostMemUsage, h.HostDiskUsage, h.NumVM};
				}
			
				//System.out.println(h.HOSTID + "\t" + df2.format(h.HostCpuUsage) +"\t\t" + df2.format(h.HostMemUsage) + "\t\t" + h.HostDiskUsage + "\t\t" + h.NumVM);
				//System.out.println(arrHost + "\t");
			}
			double a = getSum(host1);
			double b = getSum(host2);
			double c = getSum(host3);
			double d = getSum(host4);
			double[] finall = new double[] {a, b, c, d};
			for(int i = 0; i < finall.length; i++){
				System.out.println(finall[i]);
			}
			int id = 0;
			switch(findBest(finall)){
				case 0:
					id = (int)host1[0];
					break;
				case 1:
					id = (int)host2[0];
					break;
				case 2:
					id = (int)host3[0];
					break;
				case 3:
					id = (int)host4[0];
					break;
					
			}
			System.out.println(id);
			// vmAllocation(oneClient, arrHostHigh, arrHostMed, arrHostLow);
			// arrHost.sort(Comparator.comparingDouble(HOSTPERF::getCpuUsage));
		}catch(Exception e){
			System.out.println("Error viewing all of the Host info");
			e.printStackTrace();
		}
	}
	public static double getSum(double[] a){
		double sum = 0;
	
		sum = a[1] + a[2] + a[3] + (a[4] * 0.5);
		return sum;
	}
	public static int findBest(double[] a){
		double min = 1000.0;
		int result = 0;
		
		for(int i = 0; i < a.length; i++){
			if(a[i] < min){
				min = a[i];
				result = i;
				System.out.println(min + "\t" + i);
			}
		}

		return result;
	}
		
	/*class of HOST*/
	public class HOSTPERF 
	{
		int HOSTID;
		String HOSTNAME;
		double HostCpuUsage;
		double HostMemUsage;
		double HostDiskUsage;
		int NumVM;
		
		public HOSTPERF(int _hostID, String _hostName, double _cpuUsage, double _memUsage, double _diskUsage, int _numVM)
		{
			HOSTID = _hostID;
			HOSTNAME = _hostName;
			HostCpuUsage = _cpuUsage;
			HostMemUsage = _memUsage;
			HostDiskUsage = _diskUsage;
			NumVM = _numVM;
		}
		
		public int getID(){
			return HOSTID;
		}
		public String getName(){
			return HOSTNAME;
		}
		public double getCpuUsage(){
			return HostCpuUsage;
		}
		public double getMemUsage(){
			return HostMemUsage;
		}
		public double getDiskUsage(){
			return HostDiskUsage;
		}
		public int getNumVM(){
			return NumVM;
		}
	}

	/**
	 * Logs into the cloud requesting the user's name and password
	 * @param oneClient
	 * @return
	 */
	public Client logIntoCloud() {

		String passwd;
		Client oneClient = null;
		System.out.println("Enter your password: ");
		String username = System.getProperty("user.name");
		passwd = new String(System.console().readPassword("[%s]", "Password:"));
		try
		{
			oneClient = new Client(username + ":" + passwd, "https://csgate1.leeds.ac.uk:2633/RPC2");
			System.out.println("Authentication successful ...");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Incorrect Password. Program Closing.");
			System.exit(1);
		}
		return oneClient;
	}
	
	public static void main(String[] args)
	{

		try
		{
		    //create the VMSample object to complete the program
			ResourceUsage VMSample = new ResourceUsage();
		    //log into the cloud and return the client
			Client oneClient = VMSample.logIntoCloud();
			VMSample.retrieveInformation(oneClient);
        }

		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

}
