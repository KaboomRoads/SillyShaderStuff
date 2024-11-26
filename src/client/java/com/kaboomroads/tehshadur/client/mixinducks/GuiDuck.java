package com.kaboomroads.tehshadur.client.mixinducks;

public interface GuiDuck {
    int tehshadur$getFlashColor();

    void tehshadur$setFlashColor(int flashColor);

    int tehshadur$getFlashTime();

    void tehshadur$setFlashTime(int flashTime);

    int tehshadur$getFlashFadeInTime();

    void tehshadur$setFlashFadeInTime(int flashFadeInTime);

    int tehshadur$getFlashStayTime();

    void tehshadur$setFlashStayTime(int flashStayTime);

    int tehshadur$getFlashFadeOutTime();

    void tehshadur$setFlashFadeOutTime(int flashFadeOutTime);

    void tehshadur$setTimes(int flashFadeInTime, int flashStayTime, int flashFadeOutTime);
}
