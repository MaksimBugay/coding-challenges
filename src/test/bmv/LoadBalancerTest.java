package bmv;

import bmv.revolut.model.Node;
import bmv.revolut.service.BalanceTransferService;
import bmv.revolut.service.LoadBalancer;
import bmv.revolut.service.MyWallet;
import bmv.revolut.service.WithRandomAccessIndexIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoadBalancerTest {

  private LoadBalancer loadBalancer;

  @BeforeEach
  void init() {
    loadBalancer = new LoadBalancer(new WithRandomAccessIndexIterator(10));
  }

  @Test
  void moneyTransferTest() {
    BalanceTransferService service = new BalanceTransferService();
    MyWallet w1 = new MyWallet("100");
    MyWallet w2 = new MyWallet("120");

    service.transfer(w1, w2, "20");
    Assertions.assertEquals("80", w1.getBalance());
    Assertions.assertEquals("140", w2.getBalance());
  }

  @Test
  void registerNewNodeTest() {
    Node newNode = new Node("127.0.0.1");

    loadBalancer.register(newNode);

    try {
      loadBalancer.register(newNode);
    } catch (Exception ex) {
      //do nothing
    }

    Assertions.assertEquals(1, loadBalancer.getCurrentCapacity());
  }

  @Test
  void registerMaxCapacityTest() {
    for (int i = 0; i < LoadBalancer.REGISTRY_CAPACITY; i++) {
      Node newNode = new Node("127.0.0." + i);
      loadBalancer.register(newNode);
      Assertions.assertEquals(i + 1, loadBalancer.getRegistry().size());
    }
    Node newNode = new Node("127.0.0." + LoadBalancer.REGISTRY_CAPACITY);
    Assertions
        .assertThrowsExactly(IllegalStateException.class, () -> loadBalancer.register(newNode));
  }

  @Test
  void addressUniqueTest() {
    Node newNode = new Node("127.0.0.1");

    loadBalancer.register(newNode);

    Assertions.assertEquals(1, loadBalancer.getRegistry().size());

    Assertions
        .assertThrowsExactly(IllegalArgumentException.class, () -> loadBalancer.register(newNode));
  }

  @Test
  void getNodeTest() {
    for (int i = 0; i < LoadBalancer.REGISTRY_CAPACITY; i++) {
      Node newNode = new Node("127.0.0." + i);
      loadBalancer.register(newNode);
      Assertions.assertEquals(i + 1, loadBalancer.getRegistry().size());
    }
    Assertions.assertNotNull(loadBalancer.getNode());
    List<Node> results = new ArrayList<>();

    for (int i = 0; i < LoadBalancer.REGISTRY_CAPACITY; i++) {
      results.add(loadBalancer.getNode());
    }
    Assertions.assertEquals(LoadBalancer.REGISTRY_CAPACITY, results.size());

    results.forEach(node -> System.out.println(node.address));
  }
}
