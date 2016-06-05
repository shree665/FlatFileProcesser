package subedi.flatfile.test.support;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContextInitializer;
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
public class TestEnvironmentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestEnvironmentInitializer.class);

	// non-final constant, to allow for unit testing
	private static String PROPERTIES_FILE = "test-environment.properties";

	protected static final String INITIALIZE_DATABASE = "test.database.initialization.enabled";
	protected static final String TEST_ENVIRONMENT = "test.environment.profile";
	private static final String ORACLE = "oracle";

	@Override
	public void initialize(final ConfigurableApplicationContext applicationContext) {

		// classpath resource
		final InputStream stream = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE);
		Assert.notNull(stream, String.format("Could not find properties file [%s] on classpath!", PROPERTIES_FILE));

		final Properties testProps = new Properties();
		try {
			testProps.load(stream);
		} catch (final IOException e) {
			throw new IllegalStateException(String.format("Could not load properties file [%]", PROPERTIES_FILE), e);
		}

		for (final String key : testProps.stringPropertyNames()) {
			final String value = testProps.getProperty(key);
			LOGGER.info("Setting system property [{}] to [{}]", key, value);
			System.setProperty(key, value);
		}

		final String testEnvironment = testProps.getProperty(TEST_ENVIRONMENT);
		if (ORACLE.equals(testEnvironment)) {
			final String overrideValue = "false";
			System.setProperty(INITIALIZE_DATABASE, overrideValue);
			System.setProperty("test.database.mode", "");
			// setProperty overwrites, but sanity check
			Assert.isTrue(overrideValue.equals(System.getProperty(INITIALIZE_DATABASE)), format("Catastrophic failure! Could not disable database initializer for [%s]", testEnvironment));
			LOGGER.info("Successfully overrode system property [{}] to [{}]",  INITIALIZE_DATABASE, overrideValue);
		}

	}

}
