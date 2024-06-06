// C++ code
//
#define SCAN_DELAY 200

byte LED_PINS[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
const byte LED_PIN_COlUNT = sizeof(LED_PINS);
const byte total_iterations = 40;
  
// Flags the first time the roulette is used
const byte first_time = 1;
  
// The minimum iterations of the roulette
const byte minimum_iterations = 50;
  
// The maximum iterations of the roulette
const byte maximum_iterations = 200;

long rand_number;


// struct LED{
//   byte positive;
//   byte negative;
// };

void turn_led_on(int led);
void turn_led_off(int led);
void turn_led_on_and_off(int led, int delay_time);
int updateDelay(int delay_time, float percentage);
int translator(int number);

int led1 = A5;
int led2 = A4;
int led3 = 2;
int led4 = 3;
int led5 = 4;
int led6 = 5;
int led7 = 6;
int led8 = 7;
int led9 = 8;
int led10 = 9;
int led11 = 10;
int led12 = 11;
int led13 = 12;
int led14 = 13;


// Led 1 = 0 and Led7 = 0
// LED LEDS[] = {led1, led2, led3, led4, led5, led6, led7, led8, led9, led10, led11, led12, led13};^
int LEDS[] = {led1, led2, led3, led4, led5, led6, led7, led14, led8, led9, led10, led11, led12, led13};
const byte LEDS_COUNT = sizeof(LEDS)/2;


// Turns a given led ON
void turn_led_on(int led){
  pinMode(led, OUTPUT);
  digitalWrite(led, HIGH);
  // pinMode(led.positive, OUTPUT);
  // pinMode(led.negative, OUTPUT);
  // digitalWrite(led.positive, LOW);
  // digitalWrite(led.negative, HIGH);
}

// Turns a given led OFF
void turn_led_off(int led) {
  // pinMode(led.positive, INPUT);
  // pinMode(led.negative, INPUT);
  pinMode(led, INPUT);
}

// Turns a given led ON and OFF
void turn_led_on_and_off(int led, int delay_time){
  turn_led_on(led);
  delay(delay_time);
  turn_led_off(led);
}

int updateDelay(int delay_time, float percentage){
  if (percentage > 90){
    return 300;
  } else if (percentage > 70){
    return 175;
  } else if (percentage > 60){
    return 110;
  } else if(percentage > 50){
    return 75;
  }
  return 50;
}

int translator(int number){
  if (number >= 2 && number <= 7){
    return number - 1;
  } else if (number >= 9 && number <=15){
    return number - 1;
  }
  else //is the led corresponding to 0
    return 0;
}

// -- FUNCTIONS --

void setup() {
  Serial.begin(9600);
  randomSeed(analogRead(0));
  pinMode(A4, OUTPUT);
  pinMode(A5, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
  pinMode(13, OUTPUT);

}




void loop() {
  // Get random number between minimum and maximum iterations
  // This is the winning number
  long randNumber = random(minimum_iterations, maximum_iterations);
  int delay_time = 50; // time that a LED is turned on
  int led_number = 0; // LED that will turn ON
  //Serial.println(LEDS_COUNT);
  Serial.println(randNumber);
  Serial.println(randNumber%LEDS_COUNT);

  // Translate the winning number to the Pins (for example, winning number 3 lihts the pin which is suposed to be the bet of 2)
  int winning_number = translator(randNumber%LEDS_COUNT);
  Serial.println("Winning Number:");
  Serial.println(winning_number);
  //number to calculate the  deacelaration of the leds
  float percentage = 0;
  for (int i = 0; i < randNumber; i++) {
       percentage = i / (float)randNumber;
    percentage = percentage * 100;
    delay_time = updateDelay(delay_time, percentage);
    // if (percentage > 70 ) {
    // // if percentage of completeness is > 70: start deacelerating
    // // the roulette
    //   delay_time=delay_time+percentage;
    // }
    int winning_led = 0; 
    if (i == randNumber-1) winning_led = 1;
    // If a complete loop is finished - turn to first led
    if (led_number == LEDS_COUNT) led_number = 0; 
    if (winning_led == 1) {
      // if the winning led is showing, turn on and off 3 times
      for (byte i = 0; i < 5; i++) {
        turn_led_on_and_off(LEDS[led_number],1200);
        delay(300);
      }
      
    } else {
      turn_led_on_and_off(LEDS[led_number],delay_time);
    }
    
    //Serial.println(led_number);
    led_number++;
  }
}
