package subedi.flatfile.persistence.enumerated;
/**
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
