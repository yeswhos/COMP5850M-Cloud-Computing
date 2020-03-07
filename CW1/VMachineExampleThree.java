/**
 * VM Example class to introduce OpenNebula Cloud API (OCA)
 *
 * Example 3
 * @author Karim Djemame
 * @version 1.0 [2017-02-16]
 *
 */

import java.util.ArrayList;

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.user.User;
import org.opennebula.client.user.UserPool;

public class VMachineExampleThree{

	private OneResponse rc; 

	public void viewUserPool (Client oneClient)
    	{
        	// Create a user pool and query some information.
        	// The info method retrieves and saves internally the information from OpenNebula

	        UserPool    userpool = new UserPool(oneClient);
        	OneResponse rc       = userpool.info();

	        // The response can be an error, in which case we have access to a
        	// human-readable error message.
        	if (rc.isError())
        	{
            	System.out.println(rc.getErrorMessage());
            	return;
        	}

	        // Let's find out the current state of the users pool
        	printUserPool(userpool);
    	}

	    public static void printUserPool (UserPool up)
    	    {
        	System.out.println("--------------------------------------------");
        	System.out.println("Number of users: " + up.getLength());
        	System.out.println("User ID\t\tName\t\tEnabled");

	        // You can use the for-each loops with the OpenNebula pools
        	for( User user : up )
		{
	            	String id   = user.getId();
        	    	String name = user.getName();
        	    	String enab = user.xpath("enabled");

			System.out.println(id+"\t\t"+name+"\t\t"+enab);
        	}

        	System.out.println("--------------------------------------------");
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

		System.out.println("Example 3");

		try
		{
		    //create the VMSample object to complete the coursework
			VMachineExampleThree VMSample = new VMachineExampleThree();

		    //log into the cloud and return the client
			Client oneClient = VMSample.logIntoCloud();

		   // Diplay User pool
			VMSample.viewUserPool(oneClient);

			System.out.println("Example 3 Complete");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}


	}





}
