package es.uji.al259348.sliwandroid.core.services;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InterruptedIOException;
import java.util.UUID;

import es.uji.al259348.sliwandroid.core.R;
import rx.Observable;

public class MessagingServiceImpl extends AbstractService implements MessagingService {

    public static final int DEFAULT_REQUEST_TIMEOUT = 5000;

    private MqttAndroidClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;

    public MessagingServiceImpl(Context context) {
        super(context);
        createClientFromContext();
    }

    private void createClientFromContext() {
        String brokerHost = getContext().getResources().getString(R.string.mqtt_broker_host);
        String brokerUser = getContext().getResources().getString(R.string.mqtt_broker_user);
        String brokerPass = getContext().getResources().getString(R.string.mqtt_broker_pass);
        String clientId = getContext().getResources().getString(R.string.mqtt_client_id) + "-" + UUID.randomUUID().toString();

        mqttClient = new MqttAndroidClient(getContext(), brokerHost, clientId, new MemoryPersistence());

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(brokerUser);
        mqttConnectOptions.setPassword(brokerPass.toCharArray());
    }

    @Override
    public void onDestroy() {
        mqttClient.unregisterResources();
    }

    private Observable<Void> connectAction() {
        return Observable.create(subscriber -> {
            Log.d("MQTT", "Connecting... | " + Thread.currentThread().getName());
            if (mqttClient.isConnected()) {
                Log.d("MQTT", "Already connected! | " + Thread.currentThread().getName());
                subscriber.onCompleted();
            } else {
                try {
                    IMqttToken token = mqttClient.connect(mqttConnectOptions);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d("MQTT", "Connected successfully! | " + Thread.currentThread().getName());
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.d("MQTT", "Connection error! | " + Thread.currentThread().getName());
                            subscriber.onError(throwable);
                            mqttClient.unregisterResources();
                        }
                    });
                } catch (MqttException e) {
                    Log.d("MQTT", "Connection error! | " + Thread.currentThread().getName());
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<Void> disconnectAction() {
        return Observable.create(subscriber -> {
            Log.d("MQTT", "Disconnecting... | " + Thread.currentThread().getName());
            if (mqttClient.getClientId().length() == 0 && !mqttClient.isConnected()) {
                Log.d("MQTT", "Already disconnected! | " + Thread.currentThread().getName());
                subscriber.onCompleted();
            } else {
                try {
                    IMqttToken token = mqttClient.disconnect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken iMqttToken) {
                            Log.d("MQTT", "Disconnected successfully! | " + Thread.currentThread().getName());
                            subscriber.onCompleted();
                            mqttClient.unregisterResources();
                        }

                        @Override
                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                            Log.d("MQTT", "Disconnection error! | " + Thread.currentThread().getName());
                            subscriber.onError(throwable);
                        }
                    });
                } catch (MqttException e) {
                    Log.d("MQTT", "Disconnection error! | " + Thread.currentThread().getName());
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<Void> subscribeAction(String topic) {
        return Observable.create(subscriber -> {
            Log.d("MQTT", "Subscribing to topic: " + topic + " ... | " + Thread.currentThread().getName());
            try {
                IMqttToken token = mqttClient.subscribe(topic, 2);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.d("MQTT", "Subscribed successfully! | " + Thread.currentThread().getName());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.d("MQTT", "Subscription error! | " + Thread.currentThread().getName());
                        subscriber.onError(throwable);
                    }
                });
            } catch (MqttException e) {
                Log.d("MQTT", "Subscription error! | " + Thread.currentThread().getName());
                subscriber.onError(e);
            }
        });
    }

    private Observable<Void> unsubscribeAction(String topic) {
        return Observable.create(subscriber -> {
            Log.d("MQTT", "Unsubscribing from topic: " + topic + " ... | " + Thread.currentThread().getName());
            try {
                IMqttToken token = mqttClient.unsubscribe(topic);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.d("MQTT", "Unsubscribed successfully! | " + Thread.currentThread().getName());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.d("MQTT", "Unsubscription error! | " + Thread.currentThread().getName());
                        //subscriber.onError(throwable);
                    }
                });
            } catch (MqttException e) {
                Log.d("MQTT", "Unsubscription error! | " + Thread.currentThread().getName());
                subscriber.onError(e);
            }
        });
    }


    private Observable<Void> publishAction(String topic, String msg) {
        return Observable.create(subscriber -> {
            Log.d("MQTT", "Publishing to topic: " + topic + " ... | " + Thread.currentThread().getName());
            try {
                IMqttDeliveryToken token = mqttClient.publish(topic, msg.getBytes(), 2, false);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                        Log.d("MQTT", "Published successfully! | " + Thread.currentThread().getName());
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        Log.d("MQTT", "Publish error! | " + Thread.currentThread().getName());
                        subscriber.onError(throwable);
                    }
                });
            } catch (MqttException e) {
                Log.d("MQTT", "Publish error! | " + Thread.currentThread().getName());
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> publish(String topic, String msg) {
        return Observable.concat(
                connectAction(),
                publishAction(topic, msg),
                disconnectAction()
        );
    }

    // Possible errores:
    // 1. No está conectado a ninguna red (WiFi, Bluetooth, etc):
    //      Unreachable network (ENETUNREACH) -> MqttException (32103) (No es posible conectarse al servidor)
    // 2. Está conectado a una red diferente que el servidor:
    //      Timeout -> InterruptedIOException (No es posible conectarse al servidor: tiempo de espera agotado.)
    // 3. Está conectado a la misma red que el servidor, pero el servidor está caído (ip y/o puerto incorrectos?):
    //      Timeout -> InterruptedIOException (No es posible conectarse al servidor: tiempo de espera agotado.)
    // 4. Está conectado a la misma red que el servidor, pero este rechaza la conexión (ip y/o puerto incorrectos?)):
    //      Connection refused (ECONNREFUSED) -> MqttException (32103) (No es posible conectarse al servidor)
    // 5. Está conectado a la misma red que el servidor, se establece comunicación con broker pero no se recibe response del backend:
    //      Timeout -> InterruptedIOException (No es posible conectarse al servidor: tiempo de espera agotado.)
    @Override
    public Observable<String> request(String topic, String msg) {
        return Observable.create(subscriber -> {

            Log.d("MessageService", "Requesting to topic: " + topic + " ... | " + Thread.currentThread().getName());

            createClientFromContext();

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d("MessageService", "The connection has been lost while making a request! | " + Thread.currentThread().getName());
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Log.d("MessageService", "The response has been successfully received! | " + Thread.currentThread().getName());
                    subscriber.onNext(new String(mqttMessage.getPayload()));

                    Observable.concat(
                            unsubscribeAction(topic + "/response"),
                            disconnectAction()
                    ).doOnError(subscriber::onError).doOnCompleted(subscriber::onCompleted).subscribe();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.d("MessageService", "The request has been successfully delivered! | " + Thread.currentThread().getName());
                }
            });

            Observable.concat(
                    connectAction(),
                    subscribeAction(topic + "/response"),
                    publishAction(topic + "/request", msg)
            ).subscribe((s) -> {}, subscriber::onError);

            try {
                Thread.sleep(DEFAULT_REQUEST_TIMEOUT);
                mqttClient.unregisterResources();
                subscriber.onError(new InterruptedIOException("No es posible conectarse al servidor: tiempo de espera agotado."));
            } catch (InterruptedException e) {
                subscriber.onError(e.getCause());
            }

        });
    }

}
