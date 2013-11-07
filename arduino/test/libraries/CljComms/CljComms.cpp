#include "CljComms.h"

CljComms::CljComms() {
  itsCommand = 0;
  itsDataPtr = itsData;
  itsFirstNybble = true;
}

void CljComms::begin(int speed) {
  Serial.begin(speed);

  int i = 0;

  while (Serial.available() <= 0) {
    byte b[3];
    b[0] = i;
    b[1] = 99;
    b[2] = i + 100;
    xmit('B', 3, b);
    delay(300);
    if (++i >= 100) { i = 0; }
  }
}

void CljComms::xmit(byte cmd, int len, byte *data) {
  Serial.write(cmd | 0x80);

  for (int i = 0; i < len; i++) {
    byte b = data[i];
    Serial.write(b >> 4);
    Serial.write(b & 0xF);
  }

  Serial.write(0x80);
}

void CljComms::process(int b, void (*callback)(byte cmd, int n, byte *args)) {
  /* Protocol is: letter&128, then nybblised data bytes, then 128. */

  if (b == 0x80) {		/* End of message */
    (*callback)(itsCommand, itsDataPtr - itsData, itsData);
  } else if (b & 0x80) {	/* Command */
    itsCommand = b & 0x7F;
    itsDataPtr = itsData;
    itsFirstNybble = true;
  } else if (itsDataPtr < itsData + MAXDATA) {
    if (itsFirstNybble) {
      *itsDataPtr = b;
    } else {
      *itsDataPtr = (*itsDataPtr << 4) | b;
      itsDataPtr++;
    }

    itsFirstNybble = !itsFirstNybble;
  }
}
