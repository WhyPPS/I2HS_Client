package club.hsspace.i2hs;

import club.hsspace.whypps.framework.app.annotation.AppInterface;
import club.hsspace.whypps.model.ContainerClosable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ApplicationThread
 * @CreateTime: 2022/8/1
 * @Comment: 平台线程事件处理
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */
@AppInterface
public class ApplicationThread implements Runnable, ContainerClosable {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationThread.class);

    private Thread thread;

    public ApplicationThread() {
        logger.info("平台线程初始化成功！");
        thread = new Thread(this::run);
        thread.start();
    }

    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        Runnable run;
        try {
            while (!thread.isInterrupted()) {
                run = tasks.poll(5, TimeUnit.SECONDS);
                if (run != null)
                    run.run();
            }
        } catch (InterruptedException e) {
            logger.info("平台线程阻塞中断");
        }
        logger.info("平台线程已结束");
    }

    public void run(Runnable runnable) {
        try {
            tasks.put(runnable);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
    }
}
