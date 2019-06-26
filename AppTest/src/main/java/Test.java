import com.raiden.homewiork.rpcapi.TestService;
import com.raiden.homework.rpcclient.RpcProxyClient;
import com.raiden.homework.rpcserver.RpcProxyServer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class Test {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            new RpcProxyServer().init();
        });
        t.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RpcProxyClient client = new RpcProxyClient();
        TestService testService = client.clientProxy(TestService.class);
        String s = testService.hello("world");
        System.out.println(s);
    }
}
