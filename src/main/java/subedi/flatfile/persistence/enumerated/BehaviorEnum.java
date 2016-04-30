package subedi.flatfile.persistence.enumerated;

/**
 * Enum class to hold the behavior of a table
 * 
 * @author vivek.subedi
 *
 */

public enum BehaviorEnum {

	/** Load Resume */
	M("Merge"),
	/** Insert Only */
	I("Insert"),
	/** Load Replace */
	P("Load Replace"),
	/** Load - No initial delete*/
	L("Load"),
	/** Reference table */
	R("Reference Table");
	
	private String description;
	
	BehaviorEnum(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
