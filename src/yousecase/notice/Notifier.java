package yousecase.notice;

/**
 * ユーザーにメッセージを通知するクラスです。
 */
public interface Notifier {
    /**
     * ユーザーにメッセージを通知します。
     * 
     * @param message
     *            通知するメッセージ
     */
    void notifyMessage(String message);

    // factory
    /**
     * システムトレイにメッセージを通知する{@link Notifier}を生成する{@link SystemTrayNotifierBuilder}を返します。
     * 
     * @return システムトレイにメッセージを通知する{@link Notifier}を生成する{@link SystemTrayNotifierBuilder}
     */
    public static SystemTrayNotifierBuilder getSystemTrayNotifierBuilder() {
        return new SystemTrayNotifierInBackground.Builder();
    }
}