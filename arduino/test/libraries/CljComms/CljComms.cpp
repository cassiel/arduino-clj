#include "CljComms.h"

CljComms::CljComms() {
  itsCommand = 0;
  itsDataPtr = itsData;
  itsFirstNybble = true;
  itsNumBindings = 0;
}

void CljComms::begin(int speed) {
  Serial.begin(speed);

  // This is rather superfluous: do a sporadic message until we get some input. (On the
  // Clojure side in `scratch.clj`, this just causes printout.)
  int i = 0;

  while (Serial.available() <= 0) {
    byte b[3];
    b[0] = i;
    b[1] = 99;
    b[2] = i + 100;
    xmit('?', 3, b);
    delay(300);
    if (++i >= 100) { i = 0; }
  }
}

void CljComms::bind(char cmd, void (*callback)(int n, byte *args)) {
  if (itsNumBindings < MAXBINDINGS - 1) {
    Binding &b = itsBindings[itsNumBindings++];
    b.cmd = cmd;
    b.callback = callback;
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

void CljComms::process(int b) {
  /* Protocol is: letter&128, then nybblised data bytes, then 128. */

  if (b == 0x80) {		/* End of message */
    char ch = (char) itsCommand;
    for (int i = 0; i < itsNumBindings; i++) {
      Binding &b = itsBindings[i];
      if (b.cmd == ch) {
        b.callback(itsDataPtr - itsData, itsData);
        return;
      }
    }
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
