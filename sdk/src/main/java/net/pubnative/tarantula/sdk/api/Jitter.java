package net.pubnative.tarantula.sdk.api;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public interface Jitter {
    Jitter DEFAULT = new Jitter() {
        @NonNull
        private final Random mRandom = new Random();

        /**
         * @return a random value inside [0.85, 1.15] every time it's called
         */
        @Override
        public double get() {
            return 0.85 + mRandom.nextDouble() % 0.3f;
        }
    };

    Jitter NO_OP = new Jitter() {
        @Override
        public double get() {
            return 1;
        }
    };

    double get();
}
