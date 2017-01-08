package yousecase.notice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class SystemTrayNotifierInBackgroundTest {
    public static class ThreadPoolExecutorTest {
        public static final int WORKER_COUNT = 1_000_000;

        @Test
        public void タスクがFIFOで実行されているか() throws InterruptedException {
            ExecutorService executor = new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
            CountDownLatch countDownLatch = new CountDownLatch(WORKER_COUNT);
            List<Integer> list = new ArrayList<>();

            // idが0ものから順番に並び、順番にlistにidが格納されていく
            for (int id = 0; id < WORKER_COUNT; id++) {
                Runnable worker = new Worker(id, list, countDownLatch);
                executor.execute(worker);
            }

            countDownLatch.await();// 全てのWorkerが終了するまで待機

            // 順番通りに実行されていればソート前後で値が一致する
            List<Integer> sorted = new ArrayList<>(list);
            sorted.sort(Comparator.naturalOrder());
            for (int i = 0; i < list.size(); i++) {
                assertEquals(list.get(i), sorted.get(i));
            }
            assertEquals(list.size(), WORKER_COUNT);
        }

        // 自分のidをlistにセットするクラス
        private static class Worker implements Runnable {
            private int id;
            private List<Integer> list;
            private CountDownLatch countDownLatch;

            public Worker(int id, List<Integer> list, CountDownLatch countDownLatch) {
                super();
                this.id = id;
                this.list = list;
                this.countDownLatch = countDownLatch;
            }

            @Override
            public void run() {
                list.add(id);
                countDownLatch.countDown();
            }
        }
    }
}
