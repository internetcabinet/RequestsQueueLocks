import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;

public class Server1 extends Server{

    public Server1() {
        super();
    }

    public Server1(int port) {
        super(port);
    }

    public static void main(String[] args) throws Exception{
        System.out.println("Start Test");
        Server1 server = new Server1();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8083);
        server.setConnectors(new Connector[] { connector });
        Handler h1=new Handler1();
        Broker2Lookup br2l = new Broker2Lookup();
        br2l.handler = (Handler1) h1;
        br2l.start();
        ContextHandler contextRoot = new ContextHandler();
        contextRoot.setContextPath("/");
        contextRoot.setHandler(h1);
        HandlerCollection hc = new HandlerCollection ();
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        contextHandlerCollection.addHandler(contextRoot);
        hc.setHandlers(new Handler[]{contextHandlerCollection});
        server.setHandler(hc);
        server.start();
        server.join();
    }
}
