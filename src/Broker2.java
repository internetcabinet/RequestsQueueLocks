import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Broker2 extends Thread{

    Handler1 handler = null;
    String url="";
    int timeOut=200;
    ReentrantLock lock1 = new ReentrantLock();
    boolean terminateThis=false;

    class WorkerData {
        String addr;
        String status;
    }

    class DataStruct {
        String stdin;
        String cookie;
        ArrayList<String> cookiesOut;
        String out;
    }

    HashMap<String,DataStruct> requests = new HashMap<String,DataStruct>();
    Queue<String> requestsq;

    public Broker2() {
        requestsq = new LinkedList<String>();
    }

    public Broker2(Handler1 hdlr) {
        this();
        handler = hdlr;
    }

    public void addRequest(String pollId,String stdin,String cookie) {
        System.out.println("locks: Broker2 add Request... "+pollId);
        DataStruct data=new DataStruct();
        data.stdin=stdin;
        data.cookie=cookie;
        data.out="<processing/>";
        lock1.lock();
        requests.put(pollId, data);
        requestsq.add(pollId);
        lock1.unlock();
    }

    public void run() {

        while(!terminateThis) {
            //System.out.println("Broker2 queue size: "+requestsq.size());
            //try{Thread.sleep(2000);} catch(Exception e) {};
            //System.out.println("locks: Broker2 get data ... ");

			/*TODO:
				Try to get worker from here
			*/

            lock1.lock();
            String pollId = null;
            pollId = requestsq.peek();
            DataStruct data = null;
            if(pollId != null) {
                data = requests.get(pollId);
            }
            lock1.unlock();
            //System.out.println("locks: Broker2 get data ... " + data);
            if(data!=null) {
                String outStr="";
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    PrintWriter out = new PrintWriter(byteStream, true);
                    System.out.println("locks: Broker2 call getData ... ");
                    //handler.getData(data.stdin,out,null,data.cookie,data.cookiesOut);
                    try{Thread.sleep(1000);} catch(Exception e) {};
                    Random random = new Random();
                    boolean value = random.nextBoolean();
                    if(value) out.println("Some data");
                    System.out.println("locks: Broker2 call getData ... done out size: "+byteStream.size());
                    data.out = "<done/>";//byteStream.toString();
                    //Lock?
                    System.out.println("locks: Broker2 update queue data ... ");
                    lock1.lock();
                    if(byteStream.size()>0) {
                        requestsq.poll();
                        requests.put(pollId,data);
                    }
                    lock1.unlock();
                    System.out.println("locks: Broker2 call getData ... done");
                    //Reset timeout
                    timeOut = 200;
                    //try{Thread.sleep(1000);} catch(Exception e) {};
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try{Thread.sleep(100);} catch(Exception e) {};
                timeOut--;
                if(timeOut<=0) {
                    handler.requestsLock.lock();
                    handler.unlockObj(url);
                    handler.requestsLock.unlock();
                    terminateThis=true;
                }
            }
        }

    }

}
