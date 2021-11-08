package app.pilo.android.services.MusicPlayer;

import java.io.Serializable;

public class Constant implements Serializable {
    public static String CUSTOM_PLAYER_INTENT = "app.pilo.android.Services.custom_broadcast_intent";

    private static final long serialVersionUID = 1L;

    public static final int REPEAT_MODE_NONE = 1;
    public static final int REPEAT_MODE_ONE = 3;

    public static boolean isMusicLoading = false;


    public static final String INTENT_START = "start";
    public static final String INTENT_TOGGLE = "toggle";
    public static final String INTENT_PLAY = "play";
    public static final String INTENT_PAUSE = "pause";
    public static final String INTENT_NEXT = "next";
    public static final String INTENT_PREVIOUS = "previous";
    public static final String INTENT_CLOSE = "close";
    public static final String INTENT_NOTIFY = "notify";
    public static final String INTENT_LOADING = "loading";
    public static final String INTENT_ENDED = "ended";
}