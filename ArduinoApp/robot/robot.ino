int MOTOR2_PIN1 = 3;
int MOTOR2_PIN2 = 5;
int MOTOR1_PIN1 = 6;
int MOTOR1_PIN2 = 9;

int command=NULL;
int speed=255;
void setup()  
{
  pinMode(MOTOR1_PIN1, OUTPUT);
  pinMode(MOTOR1_PIN2, OUTPUT);
  pinMode(MOTOR2_PIN1, OUTPUT);
  pinMode(MOTOR2_PIN2, OUTPUT);
  Serial.begin(9600); 
}

void loop()
{  
  if (Serial.available()){
    command=Serial.read(); 
    executeCommand(command);
  }
}

void executeCommand(byte commandGiven){
  switch (commandGiven) {
  case 'F':
    goForward();
    delay(500);
    break;
  case 'B':
    goBack();
    delay(500);
    break;
  case 'L':
    goLeft();
    delay(500);
    break;
  case 'R':
    goRight();
    delay(500);
    break;
  case '+':
    increaseSpeed();
    break;
  case '-':
    decreaseSpeed();
    break;
  default: 
    break;
  }
  go(0,0);
}

void goRight(){
  go(-speed/2,-speed);
}

void goLeft(){
  go(speed,speed/2);
}

void goBack(){
  go(-speed,speed);
}

void goForward(){
  go(speed,-speed);
}

void increaseSpeed(){
  speed=(speed+30)%256;
}

void decreaseSpeed(){
  speed=speed-30;
  if(speed<0)
    speed=0;
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










