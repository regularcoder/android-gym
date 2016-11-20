package io.github.regularcoder.shakeunlock;

public class Constants {
    public interface ACTION {
        public static String LOCK_ACTION = "io.github.regularcoder.shakeunlock.action.lock";
        public static String TURN_OFF_ACTION = "io.github.regularcoder.shakeunlock.action.turn_off";
        public static String STOP_SERVICE_MAIN_ACTION = "io.github.regularcoder.shakeunlock.action.stop_service_main";
        public static String SENSITIVITY_CHANGED_ACTION = "io.github.regularcoder.shakeunlock.action.sensitivity_changed";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}