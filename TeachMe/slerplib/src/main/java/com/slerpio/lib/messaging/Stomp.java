package com.slerpio.lib.messaging;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.slerpio.lib.core.Domain;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Stomp {

    private static final String TAG = Stomp.class.getSimpleName();

    public static final int CONNECTED = 1;//Connection completely established
    public static final int NOT_AGAIN_CONNECTED = 2;//Connection process is ongoing
    public static final int DISCONNECTED_FROM_OTHER = 3;//Error, no more internet connection, etc.
    public static final int DISCONNECTED_FROM_APP = 4;//application explicitely ask for shut down the connection

    private static final String PREFIX_ID_SUBSCIPTION = "sub-";
    private static final String ACCEPT_VERSION_NAME = "accept-version";
    private static final String ACCEPT_VERSION = "1.1,1.0";
    private static final String COMMAND_CONNECT = "CONNECT";
    private static final String COMMAND_CONNECTED = "CONNECTED";
    private static final String COMMAND_MESSAGE = "MESSAGE";
    private static final String COMMAND_RECEIPT = "RECEIPT";
    private static final String COMMAND_ERROR = "ERROR";
    private static final String COMMAND_DISCONNECT = "DISCONNECT";
    private static final String COMMAND_SEND = "SEND";
    private static final String COMMAND_SUBSCRIBE = "SUBSCRIBE";
    private static final String COMMAND_UNSUBSCRIBE = "UNSUBSCRIBE";
    private static final String SUBSCRIPTION_ID = "id";
    private static final String SUBSCRIPTION_DESTINATION = "destination";
    private static final String SUBSCRIPTION_SUBSCRIPTION = "subscription";


    private static final Set<String> VERSIONS = new HashSet<String>();
    static {
        VERSIONS.add("V1.0");
        VERSIONS.add("V1.1");
        VERSIONS.add("V1.2");
    }

    private WebSocketClient socket;

    private int counter;

    private int connection;

    private Map<String, String> headers;

    private int maxWebSocketFrameSize;

    private Map<String, Subscription> subscriptions;

    private Subscription.ListenerWSNetwork networkListener;

    /**
     * Constructor of a stomp object. Only url used to set up a connection with a server can be instantiate
     *
     * @param url
     *      the url of the server to connect with
     */

    public Stomp(final Context context, String url, Subscription.ListenerWSNetwork stompStates){
        final Handler handler = new Handler(context.getMainLooper());
        try {
            this.counter = 0;

            this.headers = new HashMap<>();
            this.maxWebSocketFrameSize = 16 * 1024;
            this.connection = NOT_AGAIN_CONNECTED;
            this.networkListener = stompStates;
            this.networkListener.onState(NOT_AGAIN_CONNECTED);
            this.subscriptions = new HashMap<>();
            this.socket = new WebSocketClient(URI.create(url)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    if(Stomp.this.headers != null){
                        Stomp.this.headers.put(ACCEPT_VERSION_NAME, ACCEPT_VERSION);
                        transmit(COMMAND_CONNECT, Stomp.this.headers, null);

                    }
                }

                @Override
                public void onMessage(String message) {
                    final Frame frame = Frame.fromString(message);
                    boolean isMessageConnected = false;

                    if(frame.getCommand().equals(COMMAND_CONNECTED)){
                        Stomp.this.connection = CONNECTED;
                        Stomp.this.networkListener.onState(CONNECTED);

                        Log.d(TAG, "connected to server : " + frame.getHeaders().get("server"));
                        isMessageConnected = true;

                    } else if(frame.getCommand().equals(COMMAND_MESSAGE)){
                        String subscription = frame.getHeaders().get(SUBSCRIPTION_SUBSCRIPTION);
                        final Subscription.ListenerSubscription onReceive = Stomp.this.subscriptions.get(subscription).getCallback();
                        Log.d(TAG, "body : "+ frame.getBody());
                        if(onReceive != null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onReceive.onMessage(frame.getHeaders(), frame.getBody());
                                    try {
                                        onReceive.onMessage(frame.getHeaders(), new Domain(frame.getBody()));
                                    }catch (Exception ignore){
                                        Log.e(TAG, "run: ", ignore);
                                    }
                                }
                            });
                        } else{
                            Log.d(TAG,"Error : Subscription with id = " + subscription + " had not been subscribed");
                            //ACTION TO DETERMINE TO MANAGE SUBCRIPTION ERROR
                        }

                    } else if(frame.getCommand().equals(COMMAND_RECEIPT)){
                        //I DON'T KNOW WHAT A RECEIPT STOMP MESSAGE IS

                    } else if(frame.getCommand().equals(COMMAND_ERROR)){
                        Log.d(TAG, "Error : Headers = " + frame.getHeaders() + ", Body = " + frame.getBody());

                    }

                    if(isMessageConnected)
                        Stomp.this.subscribe();
                }

                @Override
                public void onClose(int i, String s, boolean b) {

                }

                @Override
                public void onError(Exception e) {

                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to server thanks to socket
     *
     * @param command
     *      one of a frame property, see {@link Frame} for more details
     * @param headers
     *      one of a frame property, see {@link Frame} for more details
     * @param body
     *      one of a frame property, see {@link Frame} for more details
     */
    private void transmit(String command, Map<String, String> headers, String body){
        String out = Frame.marshall(command, headers, body);
        Log.d(TAG, ">>> " + out);
        while (true) {
            if (out.length() > this.maxWebSocketFrameSize) {
                this.socket.send(out.substring(0, this.maxWebSocketFrameSize));
                out = out.substring(this.maxWebSocketFrameSize);
            } else {
                this.socket.send(out);
                break;
            }
        }
    }

    /**
     * Set up a web socket connection with a server
     */
    public void connect(){
        if(this.connection != CONNECTED){
            System.out.println(TAG + "Opening Web Socket...");
            try{
                this.socket.connect();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * disconnection come from the server, without any intervention of client side. Operations order is very important
     */
    private void disconnectFromServer(){
        if(this.connection == CONNECTED){
            this.connection = DISCONNECTED_FROM_OTHER;
            this.socket.close();
            this.networkListener.onState(this.connection);
        }
    }

    /**
     * disconnection come from the app, because the public method disconnect was called
     */
    private void disconnectFromApp(){
        if(this.connection == DISCONNECTED_FROM_APP){
            this.socket.close();
            this.networkListener.onState(this.connection);
        }
    }

    /**
     * Close the web socket connection with the server. Operations order is very important
     */
    public void disconnect(){
        if(this.connection == CONNECTED){
            this.connection = DISCONNECTED_FROM_APP;
            transmit(COMMAND_DISCONNECT, null, null);
        }
    }
    /**
     * Send a simple message to the server thanks to the body parameter
     *
     *
     * @param destination
     *      The destination through a Stomp message will be send to the server
     * @param body
     *      body of a message
     */
    public void send(String destination, Domain body){
        send(destination, null, body.toString());
    }
    /**
     * Send a simple message to the server thanks to the body parameter
     *
     *
     * @param destination
     *      The destination through a Stomp message will be send to the server
     * @param body
     *      body of a message
     */
    public void send(String destination, String body){
        send(destination, null, body);
    }
    /**
     * Send a simple message to the server thanks to the body parameter
     *
     *
     * @param destination
     *      The destination through a Stomp message will be send to the server
     * @param headers
     *      headers of the message
     * @param body
     *      body of a message
     */
    public void send(String destination, Map<String,String> headers, Domain body){
        send(destination, headers, body.toString());
    }
    /**
     * Send a simple message to the server thanks to the body parameter
     *
     *
     * @param destination
     *      The destination through a Stomp message will be send to the server
     * @param headers
     *      headers of the message
     * @param body
     *      body of a message
     */
    public void send(String destination, Map<String,String> headers, String body){
        if(this.connection == CONNECTED){
            if(headers == null)
                headers = new HashMap<>();

            if(body == null)
                body = "";

            headers.put(SUBSCRIPTION_DESTINATION, destination);

            transmit(COMMAND_SEND, headers, body);
        }
    }

    /**
     * Allow a client to send a subscription message to the server independently of the initialization of the web socket.
     * If connection have not been already done, just save the subscription
     *
     * @param subscription
     *      a subscription object
     */
    public void subscribe(Subscription subscription){
        subscription.setId(PREFIX_ID_SUBSCIPTION + this.counter++);
        this.subscriptions.put(subscription.getId(), subscription);

        if(this.connection == CONNECTED){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(SUBSCRIPTION_ID, subscription.getId());
            headers.put(SUBSCRIPTION_DESTINATION, subscription.getDestination());

            subscribe(headers);
        }
    }

    /**
     * Subscribe to a Stomp channel, through messages will be send and received. A message send from a determine channel
     * can not be receive in an another.
     *
     */
    private void subscribe(){
        if(this.connection == CONNECTED){
            for(Subscription subscription : this.subscriptions.values()){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put(SUBSCRIPTION_ID, subscription.getId());
                headers.put(SUBSCRIPTION_DESTINATION, subscription.getDestination());

                subscribe(headers);
            }
        }
    }

    /*
     * Send the subscribe to the server with an header
     * @param headers
     *      header of a subscribe STOMP message
     */
    private void subscribe(Map<String, String> headers){
        transmit(COMMAND_SUBSCRIBE, headers, null);
    }

    /**
     * Destroy a subscription with its id
     *
     * @param id
     *      the id of the subscription. This id is automatically setting up in the subscribe method
     */
    public void unsubscribe(String id){
        if(this.connection == CONNECTED){
            Map<String, String> headers = new HashMap<String, String>();
            headers.put(SUBSCRIPTION_ID, id);

            this.subscriptions.remove(id);
            this.transmit(COMMAND_UNSUBSCRIBE, headers, null);
        }
    }
}