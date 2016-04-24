package subedi.flatfile.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
/**
 * Hibernate object to map the column properties of a table.
 * 
 * @author vivek.subedi
 *
 */
@Entity
@Table(name = "ALL_TAB_COLUMNS", schema="SYS")
public class OracleSystemColumn implements Serializable {
	
	private static final long serialVersionUID = 6774564622504178566L;
	
	@EmbeddedId
	private OracleSystemColumnId id;
	
	@Column(name = "COLUMN_ID")
	private Integer position;
	
	@Column(name = "DATA_TYPE")
	private String type;
	
	@Column(name = "CHAR_LENGTH")
	private Integer charLength;
	
	@Column(name = "DATA_PRECISION", nullable = true)
	private Integer numberPrecision;
	
	@Column(name = "NULLABLE")
	@Type(type = "yes_no")
	private boolean isNullable;
	
	public OracleSystemColumn() {
		super();
	}
	
	public OracleSystemColumn(OracleSystemColumnId id, Integer position, String type, Integer charLength, Integer numberPrecision, boolean isNullable) {
		super();
		this.id = id;
		this.position = position;
		this.type = type;
		this.charLength = charLength;
		this.numberPrecision = numberPrecision;
		this.isNullable = isNullable;
	}

	public OracleSystemColumnId getId() {
		return id;
	}

	public void setId(OracleSystemColumnId id) {
		this.id = id;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCharLength() {
		return charLength;
	}

	public void setCharLength(Integer charLength) {
		this.charLength = charLength;
	}

	public int getNumberPrecision() {
		return numberPrecision;
	}

	public void setNumberPrecision(Integer numberPrecision) {
		this.numberPrecision = numberPrecision;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charLength;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isNullable ? 1231 : 1237);
		result = prime * result + numberPrecision;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OracleSystemColumn other = (OracleSystemColumn) obj;
		if (charLength != other.charLength)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isNullable != other.isNullable)
			return false;
		if (numberPrecision != other.numberPrecision)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OracleSystemColumn [id=" + id + ", position=" + position
				+ ", type=" + type + ", charLength=" + charLength
				+ ", numberPrecision=" + numberPrecision + ", isNullable=" + isNullable + "]";
	}
}
