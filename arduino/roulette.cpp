// C++ code
//
#define SCAN_DELAY 200

byte LED_PINS[] = { 11, 12, 13 };
const byte LED_PIN_COUNT = sizeof(LED_PINS);
const byte total_iterations = 40;
  
// Flags the first time the roulette is used
const byte first_time = 1;
  
// The minimum iterations of the roulette
const byte minimum_iterations = 12;
  
// The maximum iterations of the roulette
const byte maximum_iterations = 40;

long rand_number;


struct LED{
  byte positive;
  byte negative;
};

void turn_led_on(LED led);
void turn_led_off(LED led);
void turn_led_on_and_off(LED led, int delay_time);


LED led1 = {LED_PINS[1],LED_PINS[0]};
LED led2 = {LED_PINS[1],LED_PINS[2]};
LED led3 = {LED_PINS[0],LED_PINS[1]};
LED led4 = {LED_PINS[0],LED_PINS[2]};
LED led5 = {LED_PINS[2],LED_PINS[1]};
LED led6 = {LED_PINS[2],LED_PINS[0]};

LED LEDS[] = {led1, led2, led3, led4, led5, led6};
const byte LEDS_COUNT = sizeof(LEDS)/2;


// Turns a given led ON
void turn_led_on(LED led){
  pinMode(led.positive, OUTPUT);
  pinMode(led.negative, OUTPUT);
  digitalWrite(led.positive, LOW);
  digitalWrite(led.negative, HIGH);
}

// Turns a given led OFF
void turn_led_off(LED led) {
  pinMode(led.positive, INPUT);
  pinMode(led.negative, INPUT);
}

// Turns a given led ON and OFF
void turn_led_on_and_off(LED led, int delay_time){
  turn_led_on(led);
  delay(delay_time);
  turn_led_off(led);
}

// -- FUNCTIONS --

void setup() {
  Serial.begin(9600);
  randomSeed(analogRead(0));
  
}




void loop() {
  // Get random number between minimum and maximum iterations
  // This is the winning number
  long randNumber = random(minimum_iterations, maximum_iterations);
  int delay_time = 200; // time that a LED is turned on
  int led_number = 0; // LED that will turn ON
  //Serial.println(LEDS_COUNT);
  Serial.println(randNumber);
  Serial.println(randNumber%LEDS_COUNT);
  //number to calculate the  deacelaration of the leds
  float percentage = 0;
  for (int i = 0; i < randNumber; i++) {
   	percentage = i / (float)randNumber;
    percentage = percentage * 100;
    if (percentage > 70 ) {
    // if percentage of completeness is > 70: start deacelerating
    // the roulette
      delay_time=delay_time+percentage;
    }
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
