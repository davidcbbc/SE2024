// C++ code
//
#include <WiFi.h>
#include <WiFiUdp.h>
#include <SPI.h>
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
void printMacAddress();
void listNetworks();
void run_roulette(int winning_number, int randNumber);


char ssid[] = "sjhwhwhd";  //  your network SSID (name)
char pass[] = "caralho1234";
int status = WL_IDLE_STATUS;
WiFiServer server(23);
boolean alreadyConnected = false;
IPAddress ip;
unsigned long lastPingTime = 0;            // Track the last time a ping was sent
const unsigned long pingInterval = 10000;  // Interval for sending pings (10 seconds)
//WiFiUDP udp;
//unsigned int localUdpPort = 1234;  // Local port to listen on
//char incomingPacket[255];          // Buffer for incoming packets


//6 - 7
//8 - 9
//A3 - 8
int led1 = A5;
int led2 = A4;
int led3 = 2;
int led4 = 3;
int led5 = 4;
int led6 = 5;
int led7 = 9;
//int led8 = A3; //
int led8 = A1;
int led9 = A0;
int led10 = 6;
//int led12 = A1;
//int led13 = A0;
//int led14 = 6;

int LEDS[] = { led1, led2, led3, led4, led5, led6, led7, led8, led9, led10};

const byte LEDS_COUNT = sizeof(LEDS) / 2;

void printMacAddress() {

  // the MAC address of your Wifi shield

  byte mac[6];

  // print your MAC address:

  WiFi.macAddress(mac);

  Serial.print("MAC: ");

  Serial.print(mac[5], HEX);

  Serial.print(":");

  Serial.print(mac[4], HEX);

  Serial.print(":");

  Serial.print(mac[3], HEX);

  Serial.print(":");

  Serial.print(mac[2], HEX);

  Serial.print(":");

  Serial.print(mac[1], HEX);

  Serial.print(":");

  Serial.println(mac[0], HEX);
}

void listNetworks() {

  // scan for nearby networks:

  Serial.println("** Scan Networks **");

  byte numSsid = WiFi.scanNetworks();

  // print the list of networks seen:

  Serial.print("number of available networks:");

  Serial.println(numSsid);

  // print the network number and name for each network found:

  for (int thisNet = 0; thisNet < numSsid; thisNet++) {

    Serial.print(thisNet);

    Serial.print(") ");

    Serial.print(WiFi.SSID(thisNet));

    Serial.print("\tSignal: ");

    Serial.print(WiFi.RSSI(thisNet));

    Serial.print(" dBm");

    Serial.print("\tEncryption: ");

    Serial.println(WiFi.encryptionType(thisNet));
  }
}



// Turns a given led ON
void turn_led_on(int led) {
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
void turn_led_on_and_off(int led, int delay_time) {
  turn_led_on(led);
  delay(delay_time);
  turn_led_off(led);
}

int updateDelay(int delay_time, float percentage) {
  if (percentage > 90) {
    return 300;
  } else if (percentage > 70) {
    return 175;
  } else if (percentage > 60) {
    return 110;
  } else if (percentage > 50) {
    return 75;
  }
  return 50;
}

int translator(int number) {
  return number == -1 ? 9 : (number % 10) - 1;
  //if (number >= 1 && number <= 10) {
  //  return number - 1;
  //} else  //is the led corresponding to 0
  //  return 0;
}

// runs the roulette
void run_roulette(int winning_number, int randNumber) {
  int delay_time = 50;  // time that a LED is turned on
  int led_number = 0;   // LED that will turn ON
  //number to calculate the  deacelaration of the leds
  float percentage = 0;
  for (int i = 0; i < randNumber; i++) {
    percentage = i / (float)randNumber;
    percentage = percentage * 100;
    delay_time = updateDelay(delay_time, percentage);
    int winning_led = 0;
    if (i == randNumber - 1) winning_led = 1;
    // If a complete loop is finished - turn to first led
    if (led_number == LEDS_COUNT) led_number = 0;
    if (winning_led == 1) {
      // if the winning led is showing, turn on and off 3 times
      for (byte i = 0; i < 5; i++) {
        turn_led_on_and_off(LEDS[led_number], 1200);
        delay(300);
      }

    } else {
      turn_led_on_and_off(LEDS[led_number], delay_time);
    }
    led_number++;
  }
}

// -- FUNCTIONS --

void setup() {
  Serial.begin(9600);
  delay(3000);
  randomSeed(analogRead(0));
  pinMode(A4, OUTPUT);
  pinMode(A5, OUTPUT);
  //pinMode(1, OUTPUT);
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);
  pinMode(4, OUTPUT);
  pinMode(5, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(A3, OUTPUT);
  //pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(A2, OUTPUT);
  //pinMode(11, OUTPUT);
  pinMode(A1, OUTPUT);
  pinMode(A0, OUTPUT);
  Serial.println("Initializing Wifi...");
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present, trying again in 3 sec");
    // don't continue:
    delay(3000);
  }


  printMacAddress();
  Serial.println("Scanning available networks...");

  listNetworks();

  Serial.println("Attempting to connect to WPA network...");

  status = WiFi.begin(ssid, pass);

  // if you're not connected, stop here:

  if (status != WL_CONNECTED) {

    Serial.println("Couldn't get a wifi connection");

    while (true)
      ;

  } else {

    Serial.println("Connected to network");
    ip = WiFi.localIP();
    Serial.println(ip);
    server.begin();

    //udp.begin(localUdpPort);  // Start UDP
  }
}




void loop() {
  WiFiClient client = server.available();  // Listen for incoming clients
  //Serial.println("THIS CAN ONLY HAPPEN ONCE");
  //Serial.print("Waiting for connections: ");
  //Serial.println(client);
  //Serial.println(WiFi.status());

  int winning_number = 50;
  if (client) {  // If a new client connects
    Serial.println("New Client connected.");
    String currentLine = "";  // Make a String to hold incoming data from the client

    while (client.connected()) {  // Loop while the client's connected
      if (client.available()) {   // If there's bytes to read from the client
        char c = client.read();   // Read a byte
        Serial.write(c);          // Print it out the serial monitor
        if (c == '\n') {          // If the byte is a newline character

          // If the current line is "spin_message", execute the code
          if (currentLine == "spin_message") {
            Serial.println("Received spin_message");
            // Get random number between minimum and maximum iterations
            // This is the winning number
            long randNumber = random(minimum_iterations, maximum_iterations);
            // Translate the winning number to the Pins (for example, winning number 3 lihts the pin which is suposed to be the bet of 2)
            // int LEDS_AUX = LEDS_COUNT +1;
            int winning_number = translator(randNumber);
            Serial.println(winning_number);
            
            //int winning_number = 9;
            String winning_string = String(winning_number);
            if (winning_string.length() == 1) {
              String zero_string = "0";
              winning_string = zero_string + winning_string;
            }
            Serial.println("Winning Number:");
            Serial.println(winning_string);

            

            Serial.println("Spin code executed. Sending finalized...");
            client.println(winning_number);
            run_roulette(winning_number, randNumber);
            //setup();
            //Serial.println("Client Disconnected.");
            client.flush();
          } else {
            client.println("-1");
          }
          currentLine = "";      // Clear currentLine
        } else if (c != '\r') {  // If you got anything else but a carriage return character,
          currentLine += c;      // Add it to the end of the currentLine
        }
      }
      //client.flush();
    }
    // Close the connection
    
    
    
    client.flush();
    client.stop();
    Serial.println("Client Disconnected.");
  }
}