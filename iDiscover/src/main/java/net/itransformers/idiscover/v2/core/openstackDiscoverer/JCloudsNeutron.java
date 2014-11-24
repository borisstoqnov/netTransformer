package net.itransformers.idiscover.v2.core.openstackDiscoverer;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2_0.NeutronApi;
import org.jclouds.openstack.neutron.v2_0.domain.Network;
import org.jclouds.openstack.neutron.v2_0.domain.NetworkType;
import org.jclouds.openstack.neutron.v2_0.features.NetworkApi;
import org.jclouds.openstack.neutron.v2_0.options.CreateNetworkOptions;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class JCloudsNeutron implements Closeable {
   private static final String ZONE = "RegionOne";
   private final NeutronApi neutronApi;

   public static void main(String[] args) throws IOException {
      System.out.format("Create, List, and Delete Networks");

      JCloudsNeutron jcloudsNeutron = new JCloudsNeutron();

      try {
         Network network = jcloudsNeutron.createNetwork();
         jcloudsNeutron.listNetworks();
         jcloudsNeutron.deleteNetwork(network);
         jcloudsNeutron.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      finally {
         jcloudsNeutron.close();
      }
   }

   public JCloudsNeutron() {
      Iterable<Module> modules = ImmutableSet.<Module> of(new SLF4JLoggingModule());

      String provider = "openstack-neutron";
      String identity = "admin:admin"; // tenantName:userName
      String password = "devstack";

      neutronApi = ContextBuilder.newBuilder(provider)
            .credentials(identity, password)
            .endpoint("http://162.242.219.27:5000/v2.0/")
            .modules(modules)
            .buildApi(NeutronApi.class);
   }

   private Network createNetwork() {
      NetworkApi networkApi = neutronApi.getNetworkApiForZone(ZONE);

      CreateNetworkOptions createNetworkOptions = CreateNetworkOptions.builder()
            .name("devnet")
            .networkType(NetworkType.LOCAL)
            .build();

      Network network = networkApi.create(createNetworkOptions);

      System.out.format("  Network created: %s%n", network);

      return network;
   }

   private void listNetworks() {
      NetworkApi networkApi = neutronApi.getNetworkApiForZone(ZONE);
      List<? extends Network> networks = networkApi.listInDetail().concat().toList();

      System.out.format("  Networks: %n");
      for (Network network: networks) {
         System.out.format("    %s%n", network);
      }
   }

   private void deleteNetwork(Network network) {
      NetworkApi networkApi = neutronApi.getNetworkApiForZone(ZONE);
      networkApi.delete(network.getId());

      System.out.format("  Network deleted: %s%n", network);
   }

   public void close() throws IOException {
      Closeables.close(neutronApi, true);
   }
}
