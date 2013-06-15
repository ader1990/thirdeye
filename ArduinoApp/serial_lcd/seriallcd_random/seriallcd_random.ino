#include "Wire.h"
#include "LiquidCrystal.h"
void getWord(char *wordw){
  //char wordw[16];
  int i=0;

  for(i=0;i<16;i++){
    wordw[i]='a';
    randomSeed(millis());
    wordw[i]=random(50,70);
  }
  // return wordw;
}
// Connect via i2c, default address #0 (A0-A2 not jumpered)
LiquidCrystal lcd(0);

void setup() {
  // set up the LCD's number of rows and columns: 
  lcd.begin(16, 2);

  lcd.setBacklight(HIGH);
}




void loop() {
  // lcd.clear();
  lcd.setCursor(0, 1); 
  char wordw[16];
  getWord(wordw);
  lcd.print(wordw); 
  lcd.setCursor(0, 0); 
  //char wordw[16];
  getWord(wordw);
  lcd.print(wordw); 

  delay(500);

}



