package subedi.flatfile.test.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

/**
 * Plugs into the Spring JUnit loader lifecycle, to load system properties <i>before</i> the application context is built.
 * Reads properties from the classpath resource: {@value #PROPERTIES_FILE}
 * <p>
 * Properties loaded by this initializer are successfully found in &ltimport resource="..."> statements, which otherwise
 * will not resolve. These properties cannot simply be loaded in a {@link PropertyPlaceholderConfigurer}, because the
 * {@link BeanFactoryPostProcessor} and {@link BeanPostProcessor} hooks (which would otherwise read the properties files)
 * are only fired <i>after</i> all beans scanned inside <i>all</i> context files ...
 * </p>
 *
 */
public class TestIntegrationEnvironmentInitializer extends TestEnvironmentInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestIntegrationEnvironmentInitializer.class);

	@Override
	public void initialize(final ConfigurableApplicationContext applicationContext) {

		super.initialize(applicationContext);

		final String environment = System.getProperty(TEST_ENVIRONMENT);

		final String propertiesFile = "database/" + environment + "/" + environment + ".properties";

		// classpath resource
		final InputStream stream = ClassLoader.getSystemResourceAsStream(propertiesFile);
		Assert.notNull(stream, String.format("Could not find properties file [%s] on classpath!", propertiesFile));

		final Properties testProps = new Properties();
		try {
			testProps.load(stream);
		} catch (final IOException e) {
			throw new IllegalStateException(String.format("Could not load properties file [%]", propertiesFile), e);
		}

		for (final String key : testProps.stringPropertyNames()) {
			final String value = testProps.getProperty(key);
			LOGGER.info("Setting system property [{}] to [{}]", key, value);
			System.setProperty(key, value);
		}

	}

}
