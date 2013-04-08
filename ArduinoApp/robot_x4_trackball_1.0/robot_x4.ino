int MOTOR2_PIN1 = 3;
int MOTOR2_PIN2 = 5;
int MOTOR1_PIN1 = 6;
int MOTOR1_PIN2 = 9;

int command=NULL;
int currentCommand=0;
int lastTime= millis();
int currentTime= millis();
int speed=255;
int delayTime=400;

int currentServoCommand=0;
int currentMotionCommand=0;

int multiplierSpeed=95;
int nextServoCommand=0;
int nextMotionCommand=0;
int protocolStarted=false;
int lastCommandChar='Z';
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
    int commandChar=Serial.read(); 
    if(commandChar=='A'){
      protocolStarted=true;
      nextServoCommand=0;
      nextMotionCommand=0;
    }
    if(commandChar=='Z'){
      protocolStarted=false;
      currentServoCommand=nextServoCommand;
      currentMotionCommand=nextMotionCommand;
      nextServoCommand=0;
      nextMotionCommand=0;
    }
    if(protocolStarted){

      if(commandChar<'6' && commandChar>'0'){
        int gear=commandChar - 48;
        switch (gear) {

        default:   
          if(lastCommandChar=='F' ||lastCommandChar=='B'){
            nextMotionCommand=getMotionSpeed(gear,lastCommandChar);
          }
          if(lastCommandChar=='L' ||lastCommandChar=='R'){
            nextServoCommand=getMotionSpeed(gear,lastCommandChar);
          }
          break;
        }

      }
      if(commandChar=='0'){
        if(lastCommandChar=='F' ||lastCommandChar=='B')
          nextMotionCommand=nextMotionCommand*0;
        if(lastCommandChar=='L' ||lastCommandChar=='R')
          nextServoCommand=nextServoCommand*0;
      }
    }
    lastCommandChar=commandChar;

    //insertCommand(command);
  }
  executeCurrentExtraCommand();
}

int getMotionSpeed(int gear, int orientation){
  int  a=(orientation=='F'||orientation=='R')?1:-1;
  int plusSpeed=40*gear;
  int maxSpeed=255;
  int beginSpeed=100;
  if(plusSpeed>(maxSpeed-beginSpeed))
  {
    plusSpeed=maxSpeed-beginSpeed;
  }
  int fspeed=a*(beginSpeed + plusSpeed);
  
  if(orientation=='L')
    fspeed=fspeed-fspeed/7;
  if(orientation=='R')    
    fspeed=fspeed-fspeed/7;
    
  return fspeed;
}
int getServoSpeed(int gear, int orientation){
   int  a=(orientation=='L')?1:-1;
  int plusSpeed=25*gear;
  int maxSpeed=255;
  int beginSpeed=155;
  if(plusSpeed>(maxSpeed-beginSpeed))
  {
    plusSpeed=maxSpeed-beginSpeed;
  }
  return a*(beginSpeed + plusSpeed);
}

void executeCurrentExtraCommand(){
  go(currentMotionCommand,currentServoCommand);
}

void insertCommand(byte comm){
  if(comm>'A'&&comm<'Z' || comm=='+')
  { 
    currentCommand=comm;
    lastTime= millis();  
  }
}

void executeCurrentCommand(){
  currentTime= millis()-delayTime;
  if(currentTime>lastTime){
    go(0,0);
    currentCommand='N';
  }
  else{
    executeCommandN(currentCommand);
  }
}


void executeCommandN(byte commandGiven){
  switch (commandGiven) {
  case 'F':
    goForward();
    break;
  case 'B':
    goBack();
    break;
  case 'L':
    goLeft();
    break;
  case 'R':
    goRight();
    break;
  case '+':
    go(0,0);
    break;
  case '-':
    decreaseSpeed();
    break;
  default: 
    break;
  }
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



























