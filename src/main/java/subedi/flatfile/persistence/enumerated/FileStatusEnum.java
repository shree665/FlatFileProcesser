package subedi.flatfile.persistence.enumerated;

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
