import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class Handler1 extends AbstractHandler {

    HashMap<String,Broker2> requests = new HashMap<String,Broker2>();

    ReentrantLock requestsLock = new ReentrantLock();

    public void unlockObj(String url) {
        System.out.println("locks: remove Obj requestsLock.lock() "+url);
        //synchronized(requests) {
        requests.remove(url);
        //}
        System.out.println("locks: remove Obj requestsLock.unlock() "+url);
    }

    public boolean isLocked(String url) {
        requestsLock.lock();
        System.out.println("locks: Broker2 lock queue size: "+requests.size()+" "+url+" "+requests.containsKey(url));
        boolean rflag = requests.containsKey(url);
        requestsLock.unlock();
        return rflag;
    }

    public Handler1() {
        System.out.println("Create Handler1");
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        UUID un1 = UUID.randomUUID();
        String pollId = un1.toString();
        //check lock for parent
        Integer d1 = target.lastIndexOf('/');
        String parentPath = "";
        parentPath = target.substring(0,d1);
        System.out.println("locks: Check parent lock for: "+parentPath);
        if(!isLocked(target)) {
            System.out.println("locks: requestsLock.lock() "+target);
            //synchronized (requests)
            {
                Broker2 broker2 = new Broker2(this);
                broker2.url=target;
                System.out.println("locks: Broker2 Requests id: "+requests.hashCode());
                requestsLock.lock();
                requests.put(target, broker2);
                requestsLock.unlock();
                broker2.start();
                //broker2.lock1.lock();
                broker2.addRequest(pollId, "empty", "cookiesData");
                //broker2.lock1.unlock();
            }
            System.out.println("locks: requestsLock.unlock() "+target);
        } else {
            requestsLock.lock();
            Broker2 broker2 = requests.get(target);
            requestsLock.unlock();
            //broker2.lock1.lock();
            broker2.addRequest(pollId, "empty", "cookiesData");
            //broker2.lock1.unlock();
        }
        //out.write(pollId);

    }
}
