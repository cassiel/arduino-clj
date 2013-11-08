#ifndef CljComms_h
#define CljComms_h

#include "Arduino.h"

#define MAXDATA 20
#define MAXBINDINGS 20

struct Binding {
  char cmd;
  void (*callback)(int n, byte *args);
};

class CljComms {
 public:
  CljComms();
  /* Setup, with specified baud rate. */
  void begin(int speed);
  /* Register a callback function for incoming command with this character. */
  void bind(char cmd, void (*callback)(int n, byte *args));
  void xmit(byte cmd, int len, byte *data);
  void process(int b);

 private:
  Binding itsBindings[MAXBINDINGS];
  int itsNumBindings;
  byte itsCommand;
  byte itsData[MAXDATA];
  byte *itsDataPtr;
  int itsFirstNybble;
};

#endif
