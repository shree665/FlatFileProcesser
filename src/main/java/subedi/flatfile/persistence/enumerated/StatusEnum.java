package subedi.flatfile.persistence.enumerated;

/**
 * Enum to hold the final status of a table
 * 
 * @author vivek.subedi
 *
 */
public enum StatusEnum {

	/** Merge-Insert */
	I,
	/** Merge-Update */
	U,
	/** Insert Only - Other, no status **/
	O,
	/** Load-Replace/Reference Completed **/
	C,
	/** Load-Replace/Reference Started **/
	S;
}
