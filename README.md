arduino-clj
===========

Generic Arduino protocol and comms for Clojure

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

Setting up the Arduino environment: symlink the library:

    cd ~/Documents/Arduino/libraries/
    ln -s [...]/arduino-clj/arduino/test/libraries/CljComms ./CljComms

Then add it (in the Arduino sketch subdirectory) via `Sketch->Import Library`.

## Running

Unit tests (not needing Arduino) with:

        lein midje

Actual communication tests in `scratch.clj`.

## License

Copyright © 2013 Nick Rothwell, nick@cassiel.com.
