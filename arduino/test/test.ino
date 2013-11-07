/* -*- mode: c++; -*- */

#include <CljComms.h>

#define LED 13

CljComms comms;

void setup() {
  pinMode(LED, OUTPUT);
  comms.begin(9600);
}

void doit(byte cmd, int n, byte *args) {
  if (cmd == 'L' && n >= 1) {
    byte how = args[0];

    if (how > 0) {
      digitalWrite(LED, HIGH);
    } else {
      digitalWrite(LED, LOW);
    }
  }
}

void loop() {
  while (Serial.available() > 0) {
    comms.process(Serial.read(), doit);
  }
}
