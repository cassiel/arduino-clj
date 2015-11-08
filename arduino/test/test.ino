/* -*- mode: c++; -*- */

#include <CljComms.h>

#define LED 13

CljComms comms;

void do_LED(int n, byte *args) {
  if (n >= 1) {
    byte how = args[0];

    if (how > 0) {
      digitalWrite(LED, HIGH);
    } else {
      digitalWrite(LED, LOW);
    }
  }
}

void do_PLUS(int n, byte *args) {
  short total = 0;
  for (int i = 0; i < n; i++) {
    total += args[i];
  }

  byte b[2];
  b[0] = total >> 8;
  b[1] = total & 0xFF;

  comms.xmit('+', 2, b);
}

void setup() {
  pinMode(LED, OUTPUT);
  comms.begin(9600);
  comms.bind('L', do_LED);
  comms.bind('+', do_PLUS);
}

void loop() {
  while (Serial.available() > 0) {
    comms.process(Serial.read());
  }
}
