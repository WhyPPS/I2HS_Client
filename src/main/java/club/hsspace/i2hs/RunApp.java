package club.hsspace.i2hs;

import club.hsspace.whypps.framework.app.annotation.AppInterface;
import club.hsspace.whypps.framework.app.annotation.AppStart;
import club.hsspace.whypps.framework.run.WhyPPSFramework;
import club.hsspace.whypps.manage.ContainerManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: RunApp
 * @CreateTime: 2022/7/17
 * @Comment: 应用启动
 * @Author: Qing_ning
 * @Mail: 1750359613@qq.com
 */

@AppInterface
public class RunApp {

    private static final Logger logger = LoggerFactory.getLogger(RunApp.class);

    @AppStart(thread = true)
    public void main(WhyPPSFramework whyPPSFramework, ContainerManage containerManage) {
        Thread.currentThread().setContextClassLoader(RunApplication.class.getClassLoader());
        RunApplication.main(whyPPSFramework, containerManage);
    }
}
