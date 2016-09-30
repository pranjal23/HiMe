package com.prs.kw.httpserver;

import android.content.Context;
import android.util.Log;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import java.lang.ref.WeakReference;

/**
 * Created by pranjal on 27/5/15.
 */
public class RestletServer {

    Component mComponent;
    private static RestletServer theServer;
    private String NUMBER_OF_THREADS = "10";

    public static RestletServer getInstance() {
        if (theServer == null) {
            theServer = new RestletServer();
        }
        return theServer;
    }

    public boolean startRestletServer(int port, Context context) {

            mComponent = new Component();
            Server server = mComponent.getServers().add(Protocol.HTTP, port);

            server.getContext().getParameters().add("maxThreads", NUMBER_OF_THREADS);

            server.getContext().getParameters().add("minThreads", NUMBER_OF_THREADS);

            server.getContext().getParameters().add("lowThreads", NUMBER_OF_THREADS);

            server.getContext().getParameters().add("socketNoDelay", "true");

            server.getContext().getParameters().add("maxQueued", "-1");


            /*
            final Router router = new Router(mComponent.getContext().createChildContext());
            router.attach("/hi_me", himedataHandler);
            mComponent.getDefaultHost().attach(router);
            */

            Application router = new RestletRouter(new WeakReference<>(mComponent),context);
            router.getEncoderService().setEnabled(true);
            mComponent.getDefaultHost().attach("", router);
            mComponent.getClients().add(Protocol.FILE);

        try {
            mComponent.start();
        } catch (Exception e) {
            Log.d("RestletServer", "Exception in start server: "
                    + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean stopRestletServer() {
        try {
            if (mComponent == null)
                return true;

            mComponent.stop();
        } catch (Exception e) {
            Log.d("RestletServer", "Exception in stop server: " + e.getMessage());
            return false;
        }
        return true;
    }

    /*
    String responseStr =
            "    {\n" +
            "            \"age\":31,\n" +
            "            \"name\":\"Pranjal\",\n" +
            "            \"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"]\n" +
            "    }";


    Restlet himedataHandler = new Restlet() {
        @Override
        public void handle(Request request, Response response) {
            super.handle(request, response);
            response.setEntity(responseStr, MediaType.APPLICATION_JSON);
            releaseRestletResource(request, response);
            response.setStatus(Status.SUCCESS_OK);
        }
    };

    public static void releaseRestletResource(Request request, Response response) {
        request.release();
    }
    */
}
