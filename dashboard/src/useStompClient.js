import { useEffect, useRef, useState } from "react";
import { orderWebSocketUrl } from "./api.js";
import SockJS from 'sockjs-client';
import { Client } from "@stomp/stompjs";

export function useStompClient() {

    const clientRef = useRef(null);
    const isConnectedRef = useRef(false);

    const pendingRef = useRef([]);
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS(orderWebSocketUrl()),
            reconnectDelay: 3000,
            onConnect: () => {
                isConnectedRef.current = true;
                setConnected(true);
                pendingRef.current.forEach(({ destination, onMessage, subRef }) => {
                    subRef.current = client.subscribe(destination, (message) => {
                        onMessage(JSON.parse(message.body));
                    });
                });
                pendingRef.current = [];
            },

            onDisconnect: () => {
                isConnectedRef.current = false;
                setConnected(false);
            },

            onWebSocketClose: () => {
                isConnectedRef.current = false;
                setConnected(false);
            },
        });

        client.activate();
        clientRef.current = client;

        return () => {
            client.deactivate();
        };
    },[]);


    function subscribe(destination, onMessage) {
        const client = clientRef.current;
        if(!client) return () => {};

        const subRef = { current:  null };

        if(isConnectedRef.current){
            subRef.current = client.subscribe(destination, (message) => {
                onMessage(JSON.parse(message.body));
            });
        }
        else{
            pendingRef.current.push({ destination, onMessage, subRef });
        }

        return () => {
            subRef.current?.unsubscribe();
        };
    }

    return { connected, subscribe };
}