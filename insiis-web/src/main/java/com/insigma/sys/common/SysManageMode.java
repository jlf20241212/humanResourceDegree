package com.insigma.sys.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;

/**
 * @author jinw
 * @version 2020/5/19
 * <p>epsoft - insiis7</p>
 */
@Component
public final class SysManageMode {

    @Value("${sys.mode.triple:false}")
    private boolean tripleMode;

    private static boolean TRIPLE_MODE = false;

    @PostConstruct
    public void init() {
        TRIPLE_MODE = tripleMode;
    }

    public static boolean isTripleMode() {
        return TRIPLE_MODE;
    }

    public static <T> T doActionInTripleMode(Supplier<T> tripleModeAction) {
        if (TRIPLE_MODE)
            return tripleModeAction.get();
        return null;
    }

    public static void doActionInTripleMode(Runnable tripleModeRun) {
        if (TRIPLE_MODE)
            tripleModeRun.run();
    }

    public static void doAction(Runnable tripleModeRun, Runnable notTripleModeRun) {
        if (TRIPLE_MODE) {
            tripleModeRun.run();
        } else {
            notTripleModeRun.run();
        }
    }
}
