 

#include <MeetAndroid.h>

MeetAndroid meetAndroid;
int onboardLed = 13;

int MOTOR2_PIN1 = 3;

int MOTOR2_PIN2 = 5;

int MOTOR1_PIN1 = 6;

int MOTOR1_PIN2 = 9;
int val;

byte SPEED=45;
byte TURN_SPEED=45;
long time = millis();

void setup() {
 Serial.begin(9600); 

  meetAndroid.registerFunction(phoneorient, 'A');
  pinMode(MOTOR1_PIN1, OUTPUT);

  pinMode(MOTOR1_PIN2, OUTPUT);

  pinMode(MOTOR2_PIN1, OUTPUT);

  pinMode(MOTOR2_PIN2, OUTPUT);  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(testEvent, 'A');  
  meetAndroid.registerFunction(testEvent, 'B');  

  pinMode(onboardLed, OUTPUT);
  digitalWrite(onboardLed, LOW);
  

} 

void loop() {
if (Serial.available()){
   go_left();
   delay(1000);
   Serial.read();
  }
  
  if ((millis() - time) > 2000) {
    time = millis();
  }  
  }void phoneorient(byte flag, byte numOfValues)
{     go_back();
}
 void testEvent(byte flag, byte numOfValues)
{
  if(flag=='A')
     {
       go_left();
     }else{
        if(flag=='B')
           go_back();
        else go_left();
 }
}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
  delay(time);
}
 //BLOCUL MOTOR ->INAINTE
 void go_front(){ 
    go(SPEED,-SPEED); //MERGE FATA
 }
 void go_back(){
    go(-SPEED,SPEED);//MERGE INAPOI 
 }
 void go_left(){ 
    go(TURN_SPEED,TURN_SPEED);//INTOARCERE STANGA    
 }
 void go_right(){    
    go(-TURN_SPEED,-TURN_SPEED);//INTOARCERE DREAPTA 
 }
 void go_stop(){
    go(0,0);
 }

void go(int speedLeft, int speedRight) {

  if (speedLeft > 0) {

    analogWrite(MOTOR1_PIN1, speedLeft);

    analogWrite(MOTOR1_PIN2, 0);

  } 

  else {

    analogWrite(MOTOR1_PIN1, 0);

    analogWrite(MOTOR1_PIN2, -speedLeft);

  }

 

  if (speedRight > 0) {

    analogWrite(MOTOR2_PIN1, speedRight);

    analogWrite(MOTOR2_PIN2, 0);

  } 

  else {

    analogWrite(MOTOR2_PIN1, 0);

    analogWrite(MOTOR2_PIN2, -speedRight);

  }

}
