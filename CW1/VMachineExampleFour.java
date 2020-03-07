/**
 * VM Example class to introduce OpenNebula Cloud API (OCA)
 *
 * Example 4
 * @author Karim Djemame
 * @version 1.0 [2017-02-16]
 *
 */

import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

public class VMachineExampleFour{


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
        // Let's try some of the OpenNebula Cloud API functionality for VMs.

		System.out.println("Example 4");

		try
		{
		    //create the VMSample object to complete the coursework
			VMachineExampleFour VMSample = new VMachineExampleFour();

		    //log into the cloud and return the client
			Client oneClient = VMSample.logIntoCloud();

            // We will try to create a new virtual machine. The first thing we
            // need is an OpenNebula virtual machine template.

            String vmTemplate =
"CPU=\"0.1\"\n"
+ "SCHED_DS_REQUIREMENTS=\"ID=101\"\n"
+ "NIC=[\n"
+ "\tNETWORK_UNAME=\"oneadmin\",\n" 
+ "\tNETWORK=\"vnet1\" ]\n"
+ "LOGO=\"images/logos/linux.png\"\n"
+ "DESCRIPTION=\"A ttylinux instance with VNC and network context scripts, available for testing purposes. In raw format.\"\n"
+ "DISK=[\n"
+ "\tIMAGE_UNAME=\"oneadmin\",\n"
+ "\tIMAGE=\"ttylinux Base\" ]\n"
+ "SUNSTONE_NETWORK_SELECT=\"YES\"\n"
+ "SUNSTONE_CAPACITY_SELECT=\"YES\"\n"
+ "MEMORY=\"128\"\n"
+ "HYPERVISOR=\"kvm\"\n"
+ "GRAPHICS=[\n"
+ "\tLISTEN=\"0.0.0.0\",\n"
+ "\tTYPE=\"VNC\" ]\n"; 

            System.out.println("Virtual Machine Template:\n" + vmTemplate);
            System.out.println();

            System.out.print("Trying to allocate the virtual machine... ");

            OneResponse rc = VirtualMachine.allocate(oneClient, vmTemplate);

            if( rc.isError() )
            {
                System.out.println( "failed!");
                throw new Exception( rc.getErrorMessage() );
            }

            // The response message is the new VM's ID
            int newVMID = Integer.parseInt(rc.getMessage());
            System.out.println("OK ... VM ID " + newVMID + ".");

            // We can create a representation for the new VM, using the returned
            // VM-ID
            VirtualMachine vm = new VirtualMachine(newVMID, oneClient);

            // Let's hold the VM, so the scheduler won't try to deploy it
            System.out.print("Trying to hold the new VM... ");
            rc = vm.hold();

            if(rc.isError())
            {
                System.out.println("failed!");
                throw new Exception( rc.getErrorMessage() );
            }
            else
                System.out.println("OK ... VM held");

            // And now we can request its information.
            rc = vm.info();

            if(rc.isError())
                throw new Exception( rc.getErrorMessage() );

            System.out.println();
            System.out.println(
                    "This is the information OpenNebula stores for the new VM:");
            System.out.println(rc.getMessage() + "\n");

            // This VirtualMachine object has some helpers, so we can access its
            // attributes easily (remember to load the data first using the info
            // method).
            System.out.println("The new VM " +
                    vm.getName() + " has status: " + vm.status());

            // And we can also use xpath expressions
	    System.out.println("The path of the disk is: " + vm.xpath("TEMPLATE/DISK/SOURCE"));

            // Let's delete the VirtualMachine object.
            vm = null;

            // The reference is lost, but we can ask OpenNebula about the VM
            // again. This time however, we are going to use the VM pool
            VirtualMachinePool vmPool = new VirtualMachinePool(oneClient);

            // Remember that we have to ask the pool to retrieve the information
            // from OpenNebula
            rc = vmPool.info();

            if(rc.isError())
                throw new Exception( rc.getErrorMessage() );

            System.out.println(
                    "\nThese are all the Virtual Machines in the pool:");
            for ( VirtualMachine vmachine : vmPool )
            {
                System.out.println("\tID :" + vmachine.getId() +
                                   ", Name :" + vmachine.getName() );

                // Check if we have found the VM we are looking for
                if ( vmachine.getId().equals( ""+newVMID ) )
                {
                    vm = vmachine;
                }
            }

	   System.out.println("Example 4 Complete");
        }

        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

}

}
