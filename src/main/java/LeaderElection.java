import java.io.IOException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class LeaderElection implements Watcher {
  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final int SESSION_TIMEOUT = 3000;
  private ZooKeeper zooKeeper;

  public static void main(String[] args) throws IOException, InterruptedException {
    LeaderElection leaderElection = new LeaderElection();
    leaderElection.connectToZookeeper();
    leaderElection.run();
  }

  public void connectToZookeeper() throws IOException {
    this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
  }

  public void run() throws InterruptedException {
    synchronized (zooKeeper) {
      zooKeeper.wait();
    }
  }

  public void close() throws InterruptedException {
    zooKeeper.close();
  }

  @Override
  public void process(WatchedEvent event) {
    if (event.getType() == EventType.None) {
      if (event.getState() == KeeperState.SyncConnected) {
        System.out.println("Successfully connected to Zookeeper");
      } else {
        synchronized (zooKeeper) {
          System.out.println("Disconnected from Zookeeper event");
          zooKeeper.notifyAll();
        }
      }
    }
  }
}
