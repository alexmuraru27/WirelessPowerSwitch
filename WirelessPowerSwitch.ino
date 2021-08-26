#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h> 
#include <ESP8266WebServer.h>

ESP8266WiFiMulti wifiMulti;
ESP8266WebServer server(60000);

int PWRSW = 3;
int PWLED = 1;

String SHA256KeyHash="<YOUR SHA 256 KEY HERE>";

void turnOnOff(); 
void turnOffForced(); 
void getStatus(); 
void handleNotFound();

void setup(void){
  pinMode(PWRSW, FUNCTION_3); 
  pinMode(PWRSW, OUTPUT); 
  digitalWrite(PWRSW,HIGH);
  
  pinMode(PWLED, FUNCTION_3); 
  pinMode(PWLED, INPUT); 

  
  
  wifiMulti.addAP("<YOUR WIFI SSID>", "<YOUR WIFI PASSWORD>");
  
  
  server.on("/power", turnOnOff);
  server.on("/powerOffForced", turnOffForced);
  server.on("/getStatus", getStatus);  
  server.onNotFound(handleNotFound); 


  
  server.begin();                        
}

void loop(void){
   while (wifiMulti.run() != WL_CONNECTED) { 
    delay(250);
  }
  server.handleClient();                   
}

void turnOnOff(){
  String bodyKey="";
  bodyKey += server.arg("key");
  if (bodyKey.equals(SHA256KeyHash)){
      digitalWrite(PWRSW,LOW);
      delay(1000);
      digitalWrite(PWRSW,HIGH);
      server.send(200, "text/plain", "OK");
  }
  server.send(400, "text/plain", bodyKey);
}

void turnOffForced(){
  String bodyKey="";
  bodyKey += server.arg("key");
  if (bodyKey.equals(SHA256KeyHash)){
      digitalWrite(PWRSW,LOW);
      delay(6000);
      digitalWrite(PWRSW,HIGH);
      server.send(200, "text/plain", "OK");
  }
  server.send(400, "text/plain", bodyKey);
}

void getStatus(){
  int val=0;
  val=digitalRead(PWLED);
  if(val ==1){
    val =0;
  }
  else{
    val=1;
  }
  String response= String(val,BIN);
  server.send(200, "text/plain", response);
}

void handleNotFound(){
  server.send(404, "text/plain", "404: Not found");
}
