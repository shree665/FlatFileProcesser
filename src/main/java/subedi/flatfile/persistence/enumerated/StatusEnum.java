package subedi.flatfile.persistence.enumerated;

/**
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
