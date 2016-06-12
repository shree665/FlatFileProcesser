package subedi.flatfile.test.support;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Normally JUnit {@link TemporaryFolder}s are created using the @Rule annotation inside the test class. This approach will delay the
 * creation of the temporary folder until after the Spring application context has been created, which in certain integration tests is too
 * late to dynamically assign locations required in the context file itself (e.g. file pollers).
 * <p>
 * Thus, this class is designed to be used in a context file to provide the {@link #temporaryFolder} (via a getter) to any other bean
 * during the application context startup.
 * <p>
 * Note: to keep the temporary folder on the file system even after the JUnit completes (default false), an additional constructor is
 * provided: {@link #TemporaryFolderBean(boolean)}. The location of the temporary folder will always be logged at the end of the JVM
 * for convenience.
 */
public class TemporaryFolderBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemporaryFolderBean.class);

	private final TemporaryFolder temporaryFolder;

	private File stagingDirectory;

	private File workingDirectory;

	private File archiveDirectory;

	private File configDirectory;

	private File errorDirectory;

	/**
	 * Default constructor. Creates a JUnit {@link TemporaryFolder} which is deleted automatically on JVM close (whether pass or fail).
	 * <p>
	 * Equivalent to calling {@link #TemporaryFolderBean(boolean)} with <code>true</code>.
	 */
	public TemporaryFolderBean() {
		this(false);
	}

	/**
	 * Custom constructor that allows temp folder to remain after JUnit completes, i.e. to be cleaned up by the OS itself. Could be useful
	 * for debugging JUnit tests.
	 *
	 * @param isDeleteTempFolderAfterwards boolean to indicate whether the JUnit {@link TemporaryFolder} should be deleted automatically on
	 *        JVM close (whether pass or fail).
	 */
	public TemporaryFolderBean(final boolean isDeleteTempFolderAfterwards) {
		temporaryFolder = new TemporaryFolder();
		initialize(isDeleteTempFolderAfterwards);
		createSubfolders(temporaryFolder);
	}

	private void createSubfolders(final TemporaryFolder tempFolder) {
		try {
			stagingDirectory = tempFolder.newFolder("staging");
			workingDirectory = tempFolder.newFolder("working");
			archiveDirectory = tempFolder.newFolder("archive");
			errorDirectory = tempFolder.newFolder("error");
			configDirectory = tempFolder.newFolder("config");
		} catch (final IOException e) {
			throw new IllegalStateException("Error creating subfolders inside JUnit Temporary folder! Cannot continue test", e);
		}
	}

	private void initialize(final boolean isDeleteTempFolderAfterwards) {
		try {
			temporaryFolder.create();
			Assert.isTrue(temporaryFolder.getRoot().exists(), "Temporary folder not created yet! Race condition with the file system?"
					+ temporaryFolder.getRoot().getAbsolutePath());
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					LOGGER.info("The temporary folder used for this test was: {}", getCanonicalPath());
				} catch (final IOException e) {
					// e.printStackTrace();
				}
			}
		});

		if (isDeleteTempFolderAfterwards) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					temporaryFolder.delete();
				}
			});
		}

	}

	/**
	 * @return the temporary JUnit folder intended for the current test
	 */
	public TemporaryFolder getTemporaryFolder() {
		return temporaryFolder;
	}

	/**
	 * @return the path to the temporary JUnit folder, i.e. which can be navigated to in an OS file browser
	 * @throws IOException if {@link File#getCanonicalPath()} throws the exception
	 */
	public String getCanonicalPath() throws IOException {
		return temporaryFolder.getRoot().getCanonicalPath();
	}

	/** Staging directory, automatically created inside the temp folder */
	public File getStagingDirectory() {
		return stagingDirectory;
	}
	
	/** Working directory, automatically created inside the temp folder */
	public File getWorkingDirectory() {
		return workingDirectory;
	}
	
	/** Archive directory, automatically created inside the temp folder */
	public File getArchiveDirectory() {
		return archiveDirectory;
	}
	
	/** Config directory, automatically created inside the temp folder */
	public File getConfigDirectory() {
		return configDirectory;
	}
	
	/** Error directory, automatically created inside the temp folder */
	public File getErrorDirectory() {
		return errorDirectory;
	}

	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("temporaryFolder", temporaryFolder != null ? temporaryFolder.getRoot() : "null");
		return builder.toString();
	}

}
