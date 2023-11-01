import java.util.Map;
public class Broker2Lookup extends Thread {

    Handler1 handler = null;

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            //System.out.println("Check Broker2:");
            String body = "Response (" + handler.requests.size() + ") : ";
            for (Map.Entry<String, Broker2> v : handler.requests.entrySet()) {
                v.getValue().lock1.lock();
                String body2 = body + " | " + v.getKey() + " | " + v.getValue().url + " | requestsq:" + v.getValue().requestsq.size() + " | timeOut:" + v.getValue().timeOut + "\n";
                v.getValue().lock1.unlock();
                System.out.println(body2);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println()
        }
    }

}