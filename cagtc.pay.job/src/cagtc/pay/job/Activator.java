package cagtc.pay.job;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import cagtc.pay.core.common.service.ServiceEngine;
import cagtc.pay.core.log.PayLogger;
import cagtc.pay.job.startup.JobStartup;

public class Activator implements BundleActivator {

	public static final String bundleName = "cagtc.pay.job";
		
	private static final PayLogger log = PayLogger.getInstance(bundleName, Activator.class);

	private static BundleContext context;

	private JobStartup jobStartup;
	
	public static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		log.info("加载catgc.pay.job插件开始……");
		Activator.context = bundleContext;
		log.info("加载job管理对象......");
		jobStartup = new JobStartup();
		jobStartup.start(bundleContext);
		log.info("完成job管理对象......");
		log.info("加载catgc.pay.job开始注册service……");
		ServiceEngine.registerServiceForBundle(bundleContext,bundleName);
		log.info("加载catgc.pay.job结束注册service……");
		log.info("加载catgc.pay.job插件结束……");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		log.info("catgc.pay.job开始注销service……");
		jobStartup.stop(bundleContext);
		ServiceEngine.unregisterService(bundleContext);
		log.info("catgc.pay.job结束注销service……");
		Activator.context = null;
	}

}
