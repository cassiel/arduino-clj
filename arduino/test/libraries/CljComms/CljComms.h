#ifndef CljComms_h
#define CljComms_h

#include "Arduino.h"

#define MAXDATA 20

class CljComms {
 public:
  CljComms();
  void begin(int speed);
  void xmit(byte cmd, int len, byte *data);
  void process(int b, void (*callback)(byte cmd, int n, byte *args));

 private:
    byte itsCommand;
    byte itsData[MAXDATA];
    byte *itsDataPtr;
    int itsFirstNybble;
};

#endif
