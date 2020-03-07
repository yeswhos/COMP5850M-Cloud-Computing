/**
 * VM Example class to introduce OpenNebula Cloud API (OCA)
 *
 * Example 2
 * @author Karim Djemame
 * @version 1.0 [2017-02-16]
 *
 */


import java.util.ArrayList;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.host.Host;
import org.opennebula.client.host.HostPool;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;


public class VMachineExampleTwo{

	private OneResponse rc; 
	private ArrayList<Integer> allHostID = new ArrayList<Integer>();

	/**
	 * Prints out all the host information available. Along with adding the HOST IDs to a array for future use
	 * @param oneClient
	 */
	public void viewAllAvailableHostDetails(Client oneClient){
		try{

			System.out.println("************ All Available Host Information *************");
			HostPool pool = new HostPool( oneClient );
			pool.info();
			for( Host host: pool){
				host.info();
				System.out.println("Host Name: " + host.getName());
				System.out.println("Host ID: " + host.xpath("/HOST/ID"));
				this.allHostID.add(Integer.parseInt(host.xpath("/HOST/ID")));
			}
		}catch(Exception e){
			System.out.println("Error viewing all of the Host info");
			e.printStackTrace();

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

		System.out.println("Example 2");

		try
		{
		    //create the VMSample object to complete the coursework
			VMachineExampleTwo VMSample = new VMachineExampleTwo();
		    //log into the cloud and return the client
			Client oneClient = VMSample.logIntoCloud();
		   // Diplay Host pool
			VMSample.viewAllAvailableHostDetails(oneClient);

			System.out.println("Example 2 Complete");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}


	}





}
