package subedi.flatfile.test.support;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
* A hook in the Application Context creation that fires before any other beans are created. Can be
* used to create/initalize any beans that need to be created first before any other beans. Was originally
* created for {@link gov.ed.fsa.common.utils.DynamicResourceDatabasePopulator} bean, but can also be used
* with other beans.
* <p>
* Special thanks to: <a
* href="http://rachitskillisaurus.blogspot.com/2013/10/spring-force-bean-to-be-first-to.html">http://rachitskillisaurus
* .blogspot.com/2013/10/spring-force-bean-to-be-first-to.html</a>
*/

public class TestEnvironmentPriorityInitializer implements BeanFactoryPostProcessor, PriorityOrdered {

	private final static Logger logger = LoggerFactory.getLogger(TestEnvironmentPriorityInitializer.class);

	public List<String> beans;

	@Override
	public int getOrder() {

		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		logger.info("TestEnvironmentPriorityInitializer class initialized");
		// getBean() forces bean creation, cannot be autowired
		for (final String bean : beans) {
			logger.info("Creating prioritized bean: {}", bean);
			beanFactory.getBean(bean);
		}

	}

	public void setBeans(final String... beans) {
		this.beans = Arrays.asList(beans);
	}

}
