package subedi.flatfile.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 *
 * Helper class to reference the current Spring IoC Container.
 * This should be added to the application context configuration file.
 *
 * @author vivek.subedi
 *
 */
public class SpringBeanFactory implements BeanFactoryAware {
	
	private static final Logger logger = LoggerFactory.getLogger(SpringBeanFactory.class);
	private static BeanFactory beanFactory = null;

	public static final String BEAN_NOT_INIT = "FactoryBean not initialized. Please declare this class as a bean in the context file in order to use it!";

	/**
	 * setter used by the Spring IoC Container.
	 * @param beanFactory
	 */
	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		initBeanFactory(beanFactory);

	}

	/**
	 * setter that saves a reference to the [BeanFactory] (i.e. Spring IoC Container).
	 *
	 *
	 * @param beanFactory
	 */
	protected synchronized void initBeanFactory(final BeanFactory beanFactoryValue) {
		beanFactory = beanFactoryValue;
		logger.info("SpringBeanFactory initialized.");
	}

	/**
	 * Returns an instance of the bean with the bean name provided. Highly risk-prone, so try to use {@link SpringBeanFactory#getBean(Class) getBean(Class<T>)} method instead.
	 * @deprecated
	 * @param String bean name
	 * @return Object bean
	 */
	@Deprecated
	public static Object getBean(final String name) {
		if (beanFactory == null) {
			throw new RuntimeException(BEAN_NOT_INIT);
		}
		return beanFactory.getBean(name);
	}

	/**
	 * Returns an instance of the Spring for the class provided
	 * @param clazz Class to find in Spring context
	 * @return An instance of that class if it can be found
	 */
	public static <T> T getBean(final Class<T> clazz) {
		if (beanFactory == null) {
			throw new RuntimeException(BEAN_NOT_INIT);
		}
		return beanFactory.getBean(clazz);
	}

	/**
	 * Returns an instance of the Spring for the name and class provided
	 * @param name Name of bean to find in Spring context
	 * @param clazz Class to find in Spring context
	 * @return An instance of that class if it can be found
	 */
	public static <T> T getBean(final String name, final Class<T> clazz) {
		if (beanFactory == null) {
			throw new RuntimeException(BEAN_NOT_INIT);
		}
		return beanFactory.getBean(name, clazz);
	}
}
