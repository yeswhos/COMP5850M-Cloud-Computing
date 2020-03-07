/*******************************************************************************
 * VM Example class - Coursework 1
 *
 * @author Karim Djemame
 * @version 1.0 [2020-02-21]
 *
 *******************************************************************************/


import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.*;
import org.opennebula.client.host.*;
import java.util.concurrent.*;

public class VMachineSample{
    public static String getType(Object object){
        return object.getClass().toString();
    }

    public static void main(String[] args)
    {
        // Let's try some of the OpenNebula Cloud API functionality for VMs.

        // First of all, a Client object has to be created.
        Client oneClient;
        String passwd;

        String username = System.getProperty("user.name");
        System.out.println(username);
        passwd = "Vivalavida0902";
        //passwd = new String(System.console().readPassword("[%s]", "Password:"));

        // First of all, a Client object has to be created.
        // Here the client will try to connect to OpenNebula using the default

        try
        {
            oneClient = new Client(username + ":" + passwd, "https://csgate1.leeds.ac.uk:2633/RPC2");

            // We will try to create a new virtual machine. The first thing we
            // need is an OpenNebula virtual machine template.
            VMachineSample VMsample = new VMachineSample();
            //Client oneClient = VMsample.logIntoCloud();

            // This VM template is a valid one, but it will probably fail to run
            // if we try to deploy it; the path for the image is unlikely to
            // exist.
            String vmTemplate =
                    "CPU=\"0.1\"\n"
                            + "SCHED_DS_REQUIREMENTS=\"ID = 104\"\n" //DS DataStore
                            + "NIC=[\n"
                            + "\tNETWORK_UNAME=\"oneadmin\",\n"
                            + "\tNETWORK=\"vnet1\" ]\n"
                            + "LOGO=\"images/logos/debian.png\"\n"
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
                            + "\tTYPE=\"vnc\" ]\n";

            System.out.println("Virtual Machine Template:\n" + vmTemplate);
            System.out.println();

            System.out.print("Trying to allocate the virtual machine... ");
            //Starting counting VM instantiation
            long startTime = System.currentTimeMillis();

            OneResponse rc = VirtualMachine.allocate(oneClient, vmTemplate);

            if( rc.isError() )
            {
                System.out.println( "failed!");
                throw new Exception( rc.getErrorMessage() );
            }

            // The response message is the new VM's ID
            int newVMID = Integer.parseInt(rc.getMessage());
            System.out.println("ok, ID " + newVMID + ".");

            // We can create a representation for the new VM, using the returned
            // VM-ID
            VirtualMachine vm = new VirtualMachine(newVMID, oneClient);
            //Wait till it running
            while(true){
                rc = vm.info();
                String info = vm.status();
                if(info == "runn"){
                    break;
                }
            }
            //VM instantiation time
            long endTime = System.currentTimeMillis();
            long timeEscape1 = endTime - startTime;
            System.out.println("Time for instantiation" + timeEscape1 + "ms");
        
            //System.out.println("现在的" + vm2.info().getMessage());

            //新加的
            // Host host = new Host(6, oneClient);
            // vm.migrate(6);
            
            // HostPool hostPool = new HostPool(oneClient);    
            // String hostInfo = host.info().getMessage();
            // //String type = getType(host.info().getMessage());
            // //System.out.println(hostInfo);
            // String result = hostInfo;
            // String hostPoolInfo = hostPool.info().getMessage();
            // //System.out.print(hostPoolInfo);
            // result = result.replace("</", "\n");
            // System.out.print(result);

            // Let's hold the VM, so the scheduler won't try to deploy it
            System.out.print("Trying to hold the new VM... ");
            rc = vm.hold();
            rc = vm.info();

            if(rc.isError())
            {
                System.out.println("failed!");
                throw new Exception( rc.getErrorMessage() );
            }
            else
                System.out.println("ok.");

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
            System.out.println("The path of the disk is");
            System.out.println("\t" + vm.xpath("TEMPLATE/DISK/SOURCE") );

            // System.out.println("_______________________________");
            // VirtualMachinePool vmPool = new VirtualMachinePool(oneClient);
            // printVMachinePool(vmPool);



            long startTimeDelete = System.currentTimeMillis();
            // Let's delete the VirtualMachine object.
            //System.out.println("_______________________________" + vm);
            //下面这个代码有个diao用？ 也不像删除。
            vm = null;
            //System.out.println("_______________________________" + vm);
            
            // The reference is lost, but we can ask OpenNebula about the VM
            // again. This time however, we are going to use the VM pool
            VirtualMachinePool vmPool = new VirtualMachinePool(oneClient);

            // Remember that we have to ask the pool to retrieve the information
            // from OpenNebula
            rc = vmPool.info();
            // System.out.println("_______________________________" + rc);
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

            // We have also some useful helpers for the actions you can perform
            // on a virtual machine, like cancel:

            rc = vm.cancel();
            System.out.println("\nTrying to cancel the VM " + vm.getId() +
                    " (should fail)...");

            // This is all the information you can get from the OneResponse:
            System.out.println("\tOpenNebula response");
            System.out.println("\t Error: " + rc.isError());
            System.out.println("\t Msg: " + rc.getMessage());
            System.out.println("\t ErrMsg: " + rc.getErrorMessage());

            rc = vm.finalizeVM();
            long endTimeDelete = System.currentTimeMillis();
            long timeEscape = endTimeDelete - startTimeDelete;
            System.out.println("Time for delete" + timeEscape + "ms");
            System.out.println("\nTrying to finalize (delete) the VM " +
                    vm.getId() + "...");

            System.out.println("\tOpenNebula response");
            System.out.println("\t Error: " + rc.isError());
            System.out.println("\t Msg: " + rc.getMessage());
            System.out.println("\t ErrMsg: " + rc.getErrorMessage());
            

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }

    public static void printVMachinePool (VirtualMachinePool vmPool)
    {
        System.out.println("--------------------------------------------");
        System.out.println("Number of VMs: " + vmPool.getLength());
        System.out.println("User ID\t\tName\t\tEnabled");

        // You can use the for-each loops with the OpenNebula pools
        for( VirtualMachine vm : vmPool )
        {
            String id = vm.getId();
            String name = vm.getName();
            String enab = vm.xpath("enabled");

            System.out.println(id+"\t\t"+name+"\t\t"+enab);
        }

        System.out.println("--------------------------------------------");
    }
}
