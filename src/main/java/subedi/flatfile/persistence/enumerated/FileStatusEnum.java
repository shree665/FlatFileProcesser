package subedi.flatfile.persistence.enumerated;
/**
 * Enum class to hold the process status of a file
 * 
 * @author vivek.subedi
 *
 */
public enum FileStatusEnum {

	/** The processed File. */
	PROCESSED,
	
	/** The failed File. */
	FAILED,
	
	/** The Moved File to another directory. */
	MOVED,
	
	/** The uploaded file but unprocessed */
	UNPROCESSED,
	
	/** The newly uploaeded file status*/
	NEW
}
