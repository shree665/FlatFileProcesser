/**
 * 
 */
package subedi.flatfile.dao;

import java.util.List;

import subedi.flatfile.persistence.RefData;

/**
 * @author vivek.subedi
 *
 */
public interface RefDataDao {
	
	List<RefData> getRefDataforDatabase(String mappingName);

}
