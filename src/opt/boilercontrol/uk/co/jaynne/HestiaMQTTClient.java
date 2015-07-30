package uk.co.jaynne;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class HestiaMQTTClient implements MqttCallback {

  static MqttClient myClient;
  MqttConnectOptions connOpt;

  static final String BROKER_URL = "tcp://localhost:1883";
  static final String clientID = "HestiaEngine";
  static final String M2MIO_USERNAME = "";
  static final String M2MIO_PASSWORD_MD5 = "";

  // the following two flags control whether this example is a publisher, a subscriber or both
  static final Boolean subscriber = true;
  static final Boolean publisher = false;

  /**
   * 
   * connectionLost
   * This callback is invoked upon losing the MQTT connection.
   * 
   */
  @Override
  public void connectionLost(Throwable t) {
    System.out.println("Connection lost!");
    // code to reconnect to the broker would go here if desired
  }

  /**
   * 
   * deliveryComplete
   * This callback is invoked when a message published by this client
   * is successfully received by the broker.
   * 
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
      // TODO Auto-generated method stub
  }
  /**
   * 
   * messageArrived
   * This callback is invoked when a message is received on a subscribed topic.
   * 
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    ControlBroker control = ControlBroker.getInstance();
    //System.out.println("MQTT: " + topic + ": " + new String(message.getPayload()) + ".");
  
    if (topic.equals("hestia/heating0/OUT") && new String(message.getPayload()).equals("ON"))
    {
      System.out.println("MQTT received: " + topic);
      control.turnHeatingOn();
      //control.toggleHeatingBoostStatus();
      //control.testh();
      //System.out.println("MQTT called control.toggleHeatingBoostStatus()");
    } else if (topic.equals("hestia/heating0/OUT") && new String(message.getPayload()).equals("OFF"))
    {
      System.out.println("MQTT received: " + topic);
      control.turnHeatingOff();
      //control.toggleHeatingBoostStatus();
      //control.testh();
      //System.out.println("MQTT called control.toggleHeatingBoostStatus()");
    } else if (topic.equals("hestia/water0/OUT") && new String(message.getPayload()).equals("ON"))
    {
      System.out.println("MQTT received: " + topic);
      control.turnWaterOn();
      //control.toggleWaterBoostStatus();
      //control.testw();
      //System.out.println("MQTT called control.toggleWaterBoostStatus()"); 
    } else if (topic.equals("hestia/water0/OUT") && new String(message.getPayload()).equals("OFF"))
    {
      System.out.println("MQTT received: " + topic);
      control.turnWaterOff();
      //control.toggleWaterBoostStatus();
      //control.testw();
      //System.out.println("MQTT called control.toggleWaterBoostStatus()"); 
    }
  }

  public void MqttSend(String topic, String msg) {
    MqttMessage message = new MqttMessage();
    message.setPayload(msg.getBytes());
  
    try {
      myClient.publish(topic, message);
    } catch (MqttException mqtte) {
      System.out.println("MqttException caught on publish");
    }
  }

  /**
  public void MqttSend2(String topic, String msg) {
    for (int i=1; i<=10; i++) {
        int pubQoS = 0;
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(pubQoS);
        message.setRetained(false);

        // Publish the message
        System.out.println("Publishing to topic \"" + topic + "\" qos " + pubQoS);
        MqttDeliveryToken token = null;
        try {
          // publish message to broker
          myClient.publish(topic, message);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }     
  }
  /**/
  
  public void MqttCleanup() {
    try {
      myClient.disconnect();
    } catch (MqttException mqtte) {
        System.out.println("MqttException caught on disconnect");
    }
  }

  /**
   * 
   * MAIN
   * 
   */
  public static void main(String[] args) {
    HestiaMQTTClient hmc = new HestiaMQTTClient();
    //hmc.MqttInit();
  }
  
  /**
   * 
   * MqttInit
   * The main functionality of this simple example.
   * Create a MQTT client, connect to broker, subscribe.
   * 
   */
  public void MqttInit() {
    // setup MQTT Client
    connOpt = new MqttConnectOptions();
    
    connOpt.setCleanSession(true);
    connOpt.setKeepAliveInterval(30);
    //connOpt.setUserName(M2MIO_USERNAME);
    //connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());
    
    // Connect to Broker
    try {
      myClient = new MqttClient(BROKER_URL, clientID);
      myClient.setCallback(this);
      myClient.connect(connOpt);
    } catch (MqttException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    
    System.out.println("MQTT - Connected to: " + BROKER_URL);

    try {
      int subQoS = 0;
      myClient.subscribe("hestia/heating0/OUT/ON", subQoS);
      myClient.subscribe("hestia/heating0/OUT/OFF", subQoS);
      myClient.subscribe("hestia/water0/OUT/ON", subQoS);
      myClient.subscribe("hestia/water0/OUT/OFF", subQoS);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private static class SingletonHolder { 
    public static final HestiaMQTTClient INSTANCE = new HestiaMQTTClient();
  }

  public static synchronized HestiaMQTTClient getInstance() {
    return SingletonHolder.INSTANCE;
  }
}

