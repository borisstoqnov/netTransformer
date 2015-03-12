/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 11/15/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */

package net.itransformers.idiscover.v2.core.openstackDiscoverer;


import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.telemetry.MeterService;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.identity.User;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.telemetry.Meter;
import org.openstack4j.model.telemetry.Sample;
import org.openstack4j.model.telemetry.Statistics;
import org.openstack4j.openstack.OSFactory;

import java.util.List;
import java.util.Map;


public class openstack4jDiscoverer implements NodeDiscoverer {
    private final OSClient os;
    private List<? extends User> users;
    private  List<? extends Tenant> tenants;
    private List<? extends Flavor> flavors;
    private List<? extends Server> servers;
    private List<? extends Network> networks;
    private List<? extends Subnet> subnets;
    private List<? extends Image> images;
    private List<? extends Port> ports;
    private List<? extends Hypervisor> hypervisors;
    private MeterService meters;
    private List<? extends Router> routers;
    private List<? extends Volume> volumes;


    public openstack4jDiscoverer(String endpoint, String userName, String password, String tenant){
                os = OSFactory.builder()
                .endpoint(endpoint)
                .credentials(userName,password)
                .tenantName(tenant)
                .authenticate();
    }

    public List<? extends User> getUsers() {
        return users;
    }

    public void setUsers() {

        this.users = os.identity().users().list();
    }

    public List<? extends Tenant> getTenants() {
        return tenants;
    }

    public void setTenants() {
        this.tenants = os.identity().tenants().list();
    }

    public List<? extends Flavor> getFlavors() {
        return flavors;
    }

    public void setFlavors() {
        this.flavors = os.compute().flavors().list();
    }

    public List<? extends Server> getServers() {
        return servers;
    }

    public void setServers() {
        this.servers = os.compute().servers().list();
    }

    public List<? extends Network> getNetworks() {
        return networks;
    }

    public void setNetworks() {
        this.networks = os.networking().network().list();
    }

    public List<? extends Subnet> getSubnets() {
        return subnets;
    }

    public void setSubnets() {
        this.subnets = os.networking().subnet().list();

    }

    public List<? extends Image> getImages() {
        return images;
    }

    public void setImages() {
        this.images = os.images().list();
    }

    public List<? extends Router> getRouters() {
        return routers;
    }

    public void setRouters() {
        this.routers = os.networking().router().list();
    }

    public List<? extends Port> getPorts() {
        return ports;
    }

    public void setPorts() {
        this.ports = os.networking().port().list();
    }

    public List<? extends Hypervisor> getHypervisors() {
        return hypervisors;
    }

    public void setHypervisors() {
        this.hypervisors = os.compute().hypervisors().list();
    }

    public MeterService getMeters() {
        return meters;
    }

    public void setMeters() {
        this.meters = os.telemetry().meters();
    }
    public List<? extends Sample> getSamples(String sampleType) {
        return os.telemetry().meters().samples(sampleType);
    }
    public List<? extends Statistics> getStats(String sampleType,int period) {
        return os.telemetry().meters().statistics(sampleType, period);
    }

    public static void main(String[] args) {
         openstack4jDiscoverer  oD = new openstack4jDiscoverer("http://192.168.31.149:5000/v2.0","admin","openstack","admin");

        System.out.println("----------------Hypervisors-----------------\n");
        oD.setHypervisors();
        List<? extends Hypervisor> hypervisors1 =  oD.getHypervisors();

        for (Hypervisor hypervisor : hypervisors1) {
            System.out.println("Hypervisor hostname: "+hypervisor.getHypervisorHostname());
            System.out.println("Hypervisor id: "+hypervisor.getId());
            System.out.println("Hypervisor hostIP: "+hypervisor.getHostIP());
            System.out.println("Hypervisor type: "+hypervisor.getType());
            System.out.println("Hypervisor CurrentWorkLoad: "+hypervisor.getCurrentWorkload());
            System.out.println("Hypervisor FreeRam: "+hypervisor.getFreeRam() +"MB out of "+ hypervisor.getLocalMemory()+"MB");
            System.out.println("Hypervisor FreeDisk: "+hypervisor.getFreeDisk() +"G out of "+ hypervisor.getLocalDisk()+"G");
            System.out.println("Hypervisor Running VM count: "+hypervisor.getRunningVM());
            System.out.println("Hypervisor Used Virtual CPUs: "+hypervisor.getVirtualUsedCPU());
            System.out.println("Hypervisor Total Virtual CPUs: "+hypervisor.getVirtualCPU());
            System.out.println("\n");

        }



        System.out.println("----------------Networks-----------------\n");

        oD.setNetworks();
        List<? extends Network> networks1 =  oD.getNetworks();
        for (Network network : networks1) {
            List<? extends  Subnet> subnets1 = network.getNeutronSubnets();

            System.out.println("Name: " + network.getName()+ " has "+subnets1.size()+ " subnets");
            System.out.println("Id: " + network.getId());
            System.out.println("Type: " + network.getNetworkType());

            System.out.println("Status: " + network.getStatus());
            System.out.println("ProviderPhysical: "+network.getProviderPhyNet());
            System.out.println("ProviderSegmentId:" + network.getProviderSegID());

            System.out.println("AdminUP: "+ network.isAdminStateUp());
            System.out.println("External Router status: "+ network.isRouterExternal());
            System.out.println("\n");
            String tabs = "\t";
            System.out.println("----------------Network Subnets-----------------");

            for (Subnet subnet : subnets1) {
                System.out.println(tabs+"Subnet name:"+subnet.getName());
                System.out.println(tabs+"Subnet id: " + subnet.getId());
                System.out.println(tabs+"Subnet gateway: " + subnet.getGateway());
                System.out.println(tabs+"Subnet IPversion: " + subnet.getIpVersion());
                System.out.println(tabs+"Subnet range: " + subnet.getCidr());
                System.out.println(tabs+"Subnet allocation pools: " + subnet.getAllocationPools());
            }
            System.out.println("\n");




        }
        System.out.println("----------------Virtual Machines-----------------\n");

        oD.setServers();

        List<? extends Server> servers1 =  oD.getServers();
        for (Server server : servers1) {
            System.out.println("Instance Name: " + server.getName());
            System.out.println("Instance Id: " + server.getId());
            System.out.println("Instance Name: "+server.getInstanceName());

            System.out.println("Instance Availability Zone: "+server.getAvailabilityZone());
            System.out.println("Instance Hypervisor Name: "+server.getHypervisorHostname());

            System.out.println("Instance IPv4 Access: "+server.getAccessIPv4());
            System.out.println("Instance IPv6 Access: "+server.getAccessIPv6());
            System.out.println("Instance Power State: "+server.getPowerState());
            System.out.println("Instance Disk Config: "+server.getDiskConfig());
            System.out.println("Instance Launched at: "+server.getLaunchedAt());
            System.out.println("Instance Flavor: "+server.getFlavor());

            //  System.out.println("Instance Links: "+server.getLinks());

            Map<String, List<? extends Address>> addresses1 = server.getAddresses().getAddresses();
            String tabs = "\t";

            for (String s : addresses1.keySet()) {

                System.out.println(tabs+"Instance network: "+s);
                List<? extends Address> address = addresses1.get(s);
                tabs="\t\t";
                for (Address addres : address) {
                    System.out.println(tabs+"Address: " + addres.getAddr());
                    System.out.println(tabs+"MacAddress: "+ addres.getMacAddr());
                    System.out.println(tabs+"Address Type: "+addres.getType());
                    System.out.println(tabs+"Address Version: "+addres.getVersion());
                }
            }
            System.out.println("\n");

        }
        System.out.println("----------------Routers-----------------\n");

        oD.setRouters();
        List<? extends  Router> routers1 = oD.getRouters();

        for (Router router : routers1) {
            System.out.println("Router Name: "+router.getName());
            System.out.println("Router Id: "+ router.getId());
            System.out.println("Router externalGateway: "+router.getExternalGatewayInfo());
            System.out.println("Router status: " + router.getStatus());
            System.out.println("Router routes: " + router.getRoutes());
            System.out.println("\n");

        }

        System.out.println("----------------Ports-----------------\n");
        oD.setPorts();
        List<? extends  Port> ports1 = oD.getPorts();
        for (Port port : ports1) {
            System.out.println("Port on: " + port.getDeviceId());
            System.out.println("Port in: " + port.getNetworkId());
            System.out.println("Port IPs: " + port.getFixedIps());
            System.out.println("Port state: " + port.getState());
            System.out.println("\n");

        }

        System.out.println("----------------Telemetry Meters----------------\n");
        oD.setMeters();
        List<? extends Meter> meters1 = oD.getMeters().list();
        for (Meter meter : meters1) {
            System.out.println("Meter Name: "+meter.getName());
            System.out.println("Meter Id: "+meter.getId());
            System.out.println("Meter ProjectId: "+meter.getProjectId());
            System.out.println("Meter Type: "+meter.getType());
            System.out.println("Meter Unit: "+meter.getUnit());
            System.out.println("Meter ResourceId: "+meter.getResourceId());
            List<? extends Sample> samples1 = oD.getMeters().samples(meter.getName());
            String tabs="\t";
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println("\t----------------Telemetry Samples----------------\n");

            for (Sample sample : samples1) {
                System.out.println("Counter name: " + sample.getCounterName() +"Counter type: "+sample.getCounterType());
                System.out.println("Counter unit: " + sample.getCounterUnit());
                System.out.println("Counter Volume: " + sample.getCounterVolume());
                System.out.println("Counter Timestamp: "+ sample.getTimestamp());

                Map<String, Object> metadata = sample.getMetadata();
                tabs = "\t\t";
                System.out.println(tabs+"----------------CPU Telemetry Metadata----------------\n");



                for (String s : metadata.keySet()) {
                    System.out.println(s+": "+metadata.get(s));
                }
            }
            System.out.println(tabs+"----------------Telemetry Stats----------------\n");

            int period = 320;
            List<? extends Statistics> stats1 = oD.getMeters().statistics(meter.getName(),period);
            for (Statistics statistics : stats1) {
                System.out.println("Stats: "+statistics+"\n");
            }


        }

    }


    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
