package org.opencompare.explorable;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.zip.CRC32;

import org.opencompare.explore.ExplorationException;

public abstract class Explorable {

	private final int id;
	private final int parentId;

	private String tempFullId = null;
	private String sha = null;
	
	private String description = null;

	private final static byte[] HEX = 
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * This method will work as expected only at the exploration phase, it
	 * should never be called later.
	 */
	public void calculateSha(String parentFullId) {
		tempFullId = parentFullId == null ? 
				getRelativeId() : 
				parentFullId + " > " + getRelativeId();

		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(
					tempFullId.getBytes("UTF-8")
				);

			// SHA-256 length is 256 bit, i.e. 32 bytes.
			// Convert it into more human-friendly HEX representation.
			byte[] digestHex = new byte[64];
			for (int i = 0; i < 32; ++i) {
				digestHex[i * 2] = HEX[digest[i] & 0x0F];
				digestHex[i * 2 + 1] = HEX[(digest[i] & 0xF0) >> 4];
			}

			sha = new String(digestHex, "utf-8");
		} catch (Exception ex) {
			// Can safely ignore, because support for SHA-256 and UTF-8
			// is guarantted by the standard.
		}
	}

	/**
	 * This method will work as expected only at the exploration phase, it
	 * should never be called later.
	 */
	public String getTempFullId() {
		return tempFullId == null ? getRelativeId() : tempFullId;
	}

    public String getSha() {
		return sha;
	}

	/**
	 * Creates new generic Explorable. Note -- all Explorable constructors in
	 * subtypes should be lightweight, i.e. not perform any parsing or heavy
	 * computations. Those objects will be serialized to database, thus
	 * POJO-like semantics applies.
	 */
	public Explorable(int id, int parentId, String sha) {
		this.id = id;
		this.parentId = parentId;
		this.sha = sha;
	}

	/**
	 * Get the "value" of this object. This method will not be called often, so
	 * there is no need to cache its results in subclasses. Examples of the
	 * values: CRC for binary files, property value for property object.
	 * 
	 * @return String representing comparable value. Can be empty, but not null.
	 *         Be aware that database implementations may truncate this value
	 *         during (de)serialization.
	 * @throws ExplorationException
	 */
	public abstract String getValue();

	public abstract String getUserFriendlyValue();
	
	/**
	 * Get some sort of the hash code of this object's value. This is useful
	 * when the values are truncated and thus become impossible to compare to
	 * each other anymore.
	 * 
	 * The semantics for using this hash code is as following: for two
	 * "corresponding" (in the same branch of the hierarchy) objects A and B,
	 * that have the same children, first get their values and compare. If those
	 * are equal, then get hash codes and compare. If those are equal too, then
	 * the objects are considered equal, otherwise they are different.
	 * 
	 * Helper method crc32 might be useful in most of the cases.
	 * 
	 * @return long representing comparable value hash. Using CRC32 is a good
	 *         idea, while using hashCode isn't, due to possible differences in
	 *         JVM implementations. This value will be persisted, so it should
	 *         be platform-independent.
	 */
	public abstract long getValueHashCode();

	/**
	 * Relative ID is some name, uniquely identifying this Explorable on the
	 * given hierarchy level (hence "relative"). Examples are: short file name
	 * (not absolute path), property name.
	 * 
	 * @return A non-empty string, never null. This value is persisted, and thus
	 *         might be truncated. Refer to the database implementation for
	 *         further details.
	 */
	public abstract String getRelativeId();

	public int getId() {
		return id;
	}

	public int getParentId() {
		return parentId;
	}

	@Override
	public String toString() {
		return "Explorable [id=" + id + ", parentId=" + parentId + "]";
	}

	/**
	 * Calculates CRC32 checksum of the given stream content. Consumes stream
	 * content in chunks of 8192 bytes (standard size of the buffer in Oracle's
	 * BufferedInputStream).
	 * 
	 * @param stream
	 *            An open InputStream. This method won't close this stream.
	 * @return CRC32 of the stream content.
	 * @throws ExplorationException
	 *             In case of any IOExceptions, those will be wrapped into this.
	 */
	static public long crc32(InputStream stream) throws ExplorationException {
		try {
			CRC32 checksum = new CRC32();
			byte[] bytes = new byte[8192];
			int len = 0;
			while ((len = stream.read(bytes)) >= 0) {
				checksum.update(bytes, 0, len);
			}
			return checksum.getValue();
		} catch (Throwable t) {
			throw new ExplorationException(t);
		}
	}

	/**
	 * Calculates CRC32 checksum for the given string's UTF-8 representation.
	 * 
	 * @param str
	 *            An input string.
	 * @return CRC32 of the string content.
	 */
	static long crc32(String str) {
		CRC32 checksum = new CRC32();
		try {
			checksum.update(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// We can safely ignore this one -- UTF-8 is always supported
		}
		return checksum.getValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Explorable) {
			Explorable e = (Explorable) obj;
			return 
					e.getRelativeId().equals(getRelativeId()) && 
					e.getValue().equals(getValue()) && 
					e.getValueHashCode() == getValueHashCode();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getRelativeId().hashCode());
		result = prime * result + (getValue().hashCode());
		result = (int) (prime * result + getValueHashCode());
		return result;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
