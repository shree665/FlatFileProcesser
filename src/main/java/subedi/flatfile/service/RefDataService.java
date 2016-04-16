/**
 * 
 */
package subedi.flatfile.service;

import java.util.List;

import subedi.flatfile.persistence.RefData;

/**
 * @author vivek.subedi
 *
 */
public interface RefDataService {
	
	List<RefData> getRefDataForDatabase(String mappingName);
	
	List<RefData> getRefDataForTable(String mappingName, String tableNameFromFile);

}
