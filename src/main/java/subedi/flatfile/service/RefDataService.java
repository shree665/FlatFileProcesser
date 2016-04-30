/**
 * 
 */
package subedi.flatfile.service;

import java.util.List;

import subedi.flatfile.persistence.RefData;

/**
 * 
 * @author vivek.subedi
 *
 */
public interface RefDataService {
	/**
	 * Method to get all the reference values from a reference table and cache it
	 * 
	 * @param mappingName - mapping name to look for
	 * @return list of RefData object
	 */
	List<RefData> getRefDataForDatabase(String mappingName);
	
	/**
	 * Method to get a specific table name reference value from reference table
	 * 
	 * @param mappingName - mapping name 
	 * @param tableNameFromFile - table name
	 * @return list of RefData objects
	 */
	List<RefData> getRefDataForTable(String mappingName, String tableNameFromFile);

}
