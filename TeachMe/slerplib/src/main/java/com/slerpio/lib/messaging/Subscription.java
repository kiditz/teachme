package com.slerpio.lib.messaging;

import com.slerpio.lib.core.Domain;

import java.util.Map;

public class Subscription {

    private String id;

    private String destination;

    private ListenerSubscription callback;

    public Subscription(String destination, ListenerSubscription callback) {
        this.destination = destination;
        this.callback = callback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public ListenerSubscription getCallback() {
        return callback;
    }

    public interface ListenerWSNetwork {
        public void onState(int state);
    }

    public interface ListenerSubscription {
        void onMessage(Map<String, String> headers, String body);
        void onMessage(Map<String, String> headers, Domain body);
    }

    public abstract static class ListenerSubscriptionAdapter implements ListenerSubscription{
        @Override
        public void onMessage(Map<String, String> headers, String body) {
        }
    }
}