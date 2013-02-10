 

int MOTOR2_PIN1 = 3;

int MOTOR2_PIN2 = 5;

int MOTOR1_PIN1 = 6;

int MOTOR1_PIN2 = 9;

byte SPEED=180;
byte TURN_SPEED=100;
long time = millis();

void setup() {

  pinMode(MOTOR1_PIN1, OUTPUT);

  pinMode(MOTOR1_PIN2, OUTPUT);

  pinMode(MOTOR2_PIN1, OUTPUT);

  pinMode(MOTOR2_PIN2, OUTPUT);

  Serial.begin(9600);

} 

void loop() {      if (Serial.available()){

  go_front();
    delay(1000);
    go_stop();
    Serial.println(Serial.read());

  }

  if ((millis() - time) > 2000) {

    Serial.println(time);

    time = millis();

  } delay(1000);
  //go_front(); 
  delay(1000);
    go_stop();
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

    analogWrite(MOTOR1_PIN1, speedLeft-10);

    analogWrite(MOTOR1_PIN2, 0);

  } 

  else {

    analogWrite(MOTOR1_PIN1, 0);

    analogWrite(MOTOR1_PIN2, -speedLeft+10);

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
