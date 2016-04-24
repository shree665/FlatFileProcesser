/**
 * 
 */
package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.RefData;

/**
 * Interface to retrieve all the reference data that requires for mapping between file and database
 * 
 * @author vivek.subedi
 *
 */
public interface RefDataDao {
	/**
	 * Method to retrieve all the mapping objects from reference tables so that we can process data
	 * smoothly
	 * 
	 * @param mappingName - name of mapping i.e. mapping depends on the type of job
	 * @return - list of RefData objects
	 */
	List<RefData> getRefDataforDatabase(String mappingName);

}
