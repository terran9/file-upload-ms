package com.terrancode.fileupload_ms.exceptions;

public class StorageException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1983396122541383954L;

	public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable e) {
        super(message,e);
    }
}
