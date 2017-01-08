package yousecase.notice;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import yousecase.notice.SystemTrayNotifierBuilder.AbstractSystemTrayNotifierBuilder;

class SystemTrayNotifierInForeground implements Notifier {
    private static final SystemTray systemTray = SystemTray.getSystemTray();
    // BufferedImage#TYPE_INT_ARGBの指定で初期値が透明
    private static final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private static final TrayIcon trayIcon = new TrayIcon(image);
    private static final Object mutex = new Object();

    private long displayTime;// バルーンは自然に消えるので表示時間は保証しない
    private TimeUnit timeUnit;

    private SystemTrayNotifierInForeground(long displayTime, TimeUnit timeUnit) {
        super();
        this.displayTime = displayTime;
        this.timeUnit = timeUnit;
    }

    static class Builder extends AbstractSystemTrayNotifierBuilder {
        @Override
        public Notifier build() {
            return new SystemTrayNotifierInForeground(getDisplayTime(), getTimeUnit());
        }
    }

    @Override
    public void notifyMessage(String message) {
        if (!SystemTray.isSupported()) {
            return;
        }
        Objects.requireNonNull(message);

        // systemTrayをロックするとこのクラス以外からのsystemTrayの取得を制御できるがsleepが長い
        synchronized (mutex) {
            try {
                systemTray.add(trayIcon);// null、同じインスタンスが既にあればエラー
            } catch (AWTException e) {
                e.printStackTrace();
                systemTray.remove(trayIcon);
                return;
            }
            trayIcon.displayMessage(null, message, MessageType.NONE);
            try {
                timeUnit.sleep(displayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            systemTray.remove(trayIcon);
        }
    }

    // test システムトレイの通知が終了してからメインスレッドが終了する
    public static void main(String[] args) {
        Notifier notifier = new Builder().build();
        for (int i = 'a'; i <= 'c'; i++) {
            notifier.notifyMessage(String.valueOf((char) i));
        }
        System.out.println("end of main thread");
    }
}
