arduino-clj
===========

Generic Arduino protocol and comms for Clojure

## Prerequisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.x installed.

Setting up the Arduino environment: symlink the library:

    cd ~/Documents/Arduino/libraries/
    ln -s [...]/arduino-clj/arduino/test/libraries/CljComms ./CljComms

Then you may have to add it (from the Arduino sketch subdirectory) via
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

That uses command "`L`" to turn LED 13 on and off, attempts command "`X`" (ignored at the Arduino end), and does ten calls of "`+`" with multiple arguments (all of which much be in the byte range; the plus operation returns a 16-bit result in two bytes).

On the Arduino side:

```
void setup() {
  pinMode(LED, OUTPUT);
  comms.begin(9600);
  comms.bind('L', do_LED);
  comms.bind('+', do_PLUS);
}
```






(c/xmit p \L [1])
(c/xmit p \L [0])

(c/xmit p \X [0])

(doseq [n (range 64 74)]
  (c/xmit p \+ [n n n 1]))


## License

Copyright © 2013 Nick Rothwell, nick@cassiel.com.
