package subedi.flatfile.testJob;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hibernate.jpa.criteria.expression.SearchedCaseExpression.WhenClause;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ResourceLoader;

public class JobParameterValidatorTest {
	
	String filePath = "classpath:inst1_awdb.dbo.Agent.20160413.041022.3510025";
	private ResourceLoader resourceLoader;
	private ImportValidator validator;

	@Before
	public void setUp() throws Exception {
		resourceLoader = mock(ResourceLoader.class, Mockito.RETURNS_DEEP_STUBS);
		WhenClause(resourceLoader.getResource(filePath));
		
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
