package com.wlw.thrift.client.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.wlw.thrift.util.Logger;

public class ClientSideProcessorFetcherHelper {
	private static final Logger logger = Logger.getLogger(ClientSideProcessorFetcherHelper.class);
	private static HashMap<String, Model> serviceContainer = new HashMap<String, Model>();
	private static final String CLIENT = "$Client";

	// 只是读，不加锁,不会有线程安全问题
	public static Model getModelByClassName(String clz) {
		return serviceContainer.get(clz);
	}

	public static synchronized Set<String> fetchProcessors(String scanPackages) throws Exception {
		Set<String> globalPathSets = new HashSet<String>();
		if (null == scanPackages) {
			throw new Exception("scanPackages is null");
		}
		scanPackages = scanPackages.trim();
		if (0 == scanPackages.length()) {
			throw new Exception("scanPackages is empty...");
		}
		// 可能有多个包,分开，逐个击破
		String[] packageArray = scanPackages.split(",");
		// 针对每个包路径
		for (String singlePackage : packageArray) {
			// 拿到这个包路径下面的所有物理路径
			Set<String> result = fetchProcessor(singlePackage);
			for (String str : result) {
				// 逐个增加
				if (false == globalPathSets.add(str)) {// 全局包也不应该有重复的，否则必须报错
					throw new Exception("duplicated path  : " + str);
				}
			}
		}
		// 至少要有1个吧
		if (0 == globalPathSets.size()) {
			throw new Exception("globalPathSets is zero,no processor to listen on,why???");
		}
		return globalPathSets;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Set<String> fetchProcessor(String scanPackage) throws Exception {
		// 切换成自动扫描方式
		Set<String> pathSet = new HashSet<String>();
		logger.info("---------------------------------------");
		logger.info("current scan package --- " + scanPackage);
		// 开始扫描
		Reflections reflections = new Reflections(scanPackage);
		Set<Class<?>> annotationedClasses = reflections.getTypesAnnotatedWith(Processor.class);
		for (Class annotationedClass : annotationedClasses) {
			// 0)获取本身类
			logger.info("annotationedClassName:" + annotationedClass.getName());// HelloWorldService
			// 1)开始获取注解信息
			Processor annotation = (Processor) annotationedClass.getAnnotation(Processor.class);
			String group = annotation.group();
			String service = annotation.service();
			String edition = annotation.edition();
			// 2)最后一步
			String path = StringUtils.groupUnionServiceUnionEdition(group, service, edition) + "/PROVIDER";
			if (false == pathSet.add(path)) {
				// 注册失败,因为有重复的//单个包不容许有重复的
				throw new Exception("duplicated path: " + path);
			}
			// 注册到service列表里
			Model model = new Model(group, service, edition);
			serviceContainer.put(annotationedClass.getName() + CLIENT, model);
			serviceContainer.put(annotationedClass.getName(), model);

			logger.info("succeed added: " + path);
		}
		logger.info("---------------------------------------");
		return pathSet;
	}
}
