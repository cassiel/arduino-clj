`-*- word-wrap: t; -*-`

# `arduino-clj` [![Build Status](https://secure.travis-ci.org/cassiel/arduino-clj.png)](http://travis-ci.org/cassiel/arduino-clj)

Generic Arduino protocol and comms for Clojure.

Calls from Clojure to Arduino, and back again, are distinguished by a character (byte) denoting the command, and zero or more bytes of data. Encoding/decoding is done on both sides of the wire, using a MIDI sysex-like protocol. This approach is similar to [Firmata](http://playground.arduino.cc/Interfacing/Firmata), but we expect applications to actually extend the Arduino sketch with additional functions, rather than rely on Firmata's fixed sketch and protocol features.

(We consider bytes to be unsigned on both platforms; on the Clojure side they're left in `int`/`long` form, but encoding will fail if values are not in the `0..255` range.)

## Prerequisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.x installed.

Setting up the Arduino environment: symlink the library:

    cd ~/Documents/Arduino/libraries/
    ln -s [...]/arduino-clj/arduino/test/libraries/CljComms ./CljComms

Then you may have to add it (or rather, the symlink in the Arduino sketch subdirectory) via
`Sketch->Import Library`.

## Running

Unit tests (not needing Arduino) with:

        lein midje

Actual communication tests are in `scratch.clj`, with some example endpoints in the Arduino. Here's a taster:

```clojure
(def p (c/open "/dev/tty.usbmodemfa141" 9600
                {\? println
                 \+ (fn [[h l]] (println "TOTAL" (+ (bit-shift-left h 8) l)))}))
```

That opens a connection to an Arduino at 9600 baud, with two handlers for commands "`?`" and "`+`". Every command is denoted by a character, and takes a sequence of byte values as arguments.

To transmit:

```clojure
(c/xmit p \L [1])
(c/xmit p \L [0])

(c/xmit p \X [0])

(doseq [n (range 64 74)]
  (c/xmit p \+ [n n n 1]))
```

That uses command "`L`" to turn LED 13 on and off (see the Arduino sketch `test.ino` for details), attempts command "`X`" (which doesn't exist, and is ignored at the Arduino end), and does ten calls of "`+`" with multiple arguments (all of which much be in the byte range; the plus operation returns a 16-bit result in two bytes).

On the Arduino side:

```c++
void setup() {
  pinMode(LED, OUTPUT);
  comms.begin(9600);
  comms.bind('L', do_LED);
  comms.bind('+', do_PLUS);
}
```

Call `comms.bind` for every function that is to be called in the Arduino.

```c++
comms.xmit('+', 2, b);
```

Transmit a command, and an array of bytes, back to the host.

## License

Copyright © 2013 Nick Rothwell, nick@cassiel.com.
