package cn.focus.eco.data.curator.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * register spring bean dynamically
 *
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/9
 */
@Component
public class RegisterBean implements ApplicationContextAware {
	public ApplicationContext aContext;
	private Logger logger = LoggerFactory.getLogger(RegisterBean.class);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.aContext = applicationContext;
	}

	/**
	 * 动态注册bean
	 * 
	 */
	public <T> void register(String beanName, Class<T> classType,
			Map<String, Object> values) {
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) aContext;
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
				.getBeanFactory();
		if (defaultListableBeanFactory.isBeanNameInUse(beanName)) {
			defaultListableBeanFactory.removeBeanDefinition(beanName);
			logger.info("beanName-->" + beanName + " removed successfully");
		}
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(classType);
		for (String key : values.keySet()){
			beanDefinitionBuilder.addPropertyValue(key, values.get(key));
		}
		defaultListableBeanFactory.registerBeanDefinition(beanName,
				beanDefinitionBuilder.getRawBeanDefinition());
		logger.info("beanName-->" + beanName + " registered successfully");
	}
}
