package yousecase.notice;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import yousecase.util.Builder;

/**
 * システムトレイにメッセージを通知する{@link Notifier}を生成するビルダーです。
 * {@link #displayTime}メソッドでパラメータを設定し、{@link #build}メソッドでパラメータを利用したインスタンスを生成します。
 * デフォルトの表示時間は3秒です。状況により設定した時間が経過する前に表示が終了する場合があります。
 * 
 * <h3>使用例</h3>
 * 
 * <pre>
 * {@code
 * 
 * パラメータ設定なし(表示時間3秒)
 * Notifier notifier = Notifier.getSystemTrayNotifierBuilder().build();
 * 
 * 表示時間を2秒に設定
 * Notifier notifier = Notifier.getSystemTrayNotifierBuilder().displayTime(2, TimeUnit.SECONDS).build();
 * }
 * </pre>
 */
public interface SystemTrayNotifierBuilder extends Builder<Notifier> {

    /**
     * システムトレイにメッセージを表示する時間を設定します。状況により設定した時間が経過する前に表示が終了する場合があります。
     * 
     * @param displayTime
     *            システムトレイにメッセージを表示する時間
     * @param timeUnit
     *            displayTimeパラメータの時間単位
     * @return システムトレイにメッセージを通知する{@link Notifier}を生成するビルダーオブジェクト
     */
    SystemTrayNotifierBuilder displayTime(long displayTime, TimeUnit timeUnit);

    /**
     * システムトレイにメッセージを通知する{@link Notifier}を生成します。
     * 
     * @return システムトレイにメッセージを通知する{@link Notifier}
     */
    abstract Notifier build();

    // base class
    static abstract class AbstractSystemTrayNotifierBuilder implements SystemTrayNotifierBuilder {
        private long displayTime = 3;// 初期値
        private TimeUnit timeUnit = TimeUnit.SECONDS;// 初期値

        @Override
        public SystemTrayNotifierBuilder displayTime(long displayTime, TimeUnit timeUnit) {
            this.displayTime = displayTime;
            this.timeUnit = Objects.requireNonNull(timeUnit);
            return this;
        }

        long getDisplayTime() {
            return displayTime;
        }

        TimeUnit getTimeUnit() {
            return timeUnit;
        }

        AbstractSystemTrayNotifierBuilder() {
        }
    }
}