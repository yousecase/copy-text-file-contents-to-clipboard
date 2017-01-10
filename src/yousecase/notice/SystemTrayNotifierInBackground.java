package yousecase.notice;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class SystemTrayNotifierInBackground implements Notifier {
    private static final ExecutorService executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    // 上記はExecutors#newSingleThreadExecutorの内部実装のカスタマイズ
    // Executors#newCachedThreadPoolはタスクがなくなると60秒後にスレッドを終了するが実行順が保証されない
    // Executors#newFixedThreadPool(1)はタスクが順番に実行されるがスレッドが自動で終了しない
    // Executors#newSingleThreadExecutorはタスクが順番に実行されるがスレッドが自動で終了しない
    // Executors#newScheduledThreadPool(0)はタスクがなくなるとスレッドが自動で終了するが実行順が保証されない
    // Executors#newSingleThreadScheduledExecutorはタスクの実行順が保証されずスレッドが自動で終了しない
    // SwingUtilities#invokeLater, EventQueue#invokeLaterはAWTイベント用

    private final Notifier notifier;

    private SystemTrayNotifierInBackground(Notifier notifier) {
        super();
        this.notifier = notifier;
    }

    static class Builder implements SystemTrayNotifierBuilder {
        private SystemTrayNotifierBuilder builder;

        Builder() {
            builder = new SystemTrayNotifierInForeground.Builder();
        }

        @Override
        public SystemTrayNotifierBuilder displayTime(long displayTime, TimeUnit timeUnit) {
            Objects.requireNonNull(timeUnit);
            builder.displayTime(displayTime, timeUnit);
            return this;
        }

        @Override
        public Notifier build() {
            return new SystemTrayNotifierInBackground(builder.build());
        }
    }

    @Override
    public void notifyMessage(String message) {
        Objects.requireNonNull(message);
        executor.execute(() -> {
            notifier.notifyMessage(message);
        });
    }

    // test システムトレイの通知の終了を待たずにメインスレッドが終了する
    public static void main(String[] args) {
        Notifier notifier = new Builder().build();
        for (int i = 'a'; i <= 'c'; i++) {
            notifier.notifyMessage(String.valueOf((char) i));
        }
        System.out.println("end of main thread");
    }
}
