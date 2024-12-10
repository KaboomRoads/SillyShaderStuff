package com.kaboomroads.tehshadur.mixinducks;

import com.kaboomroads.tehshadur.border.BorderProvider;

public interface EntityDuck {
    void tehshadur$setBorderProvider(BorderProvider borderProvider);

    BorderProvider tehshadur$getBorderProvider();

    void tehshadur$collideWithActiveBorder();

    void tehshadur$receiveAnimation(int animationId);
}
