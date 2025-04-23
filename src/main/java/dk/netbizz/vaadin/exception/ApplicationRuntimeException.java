package dk.netbizz.vaadin.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 2537055295840721500L;
	protected static final Logger tendernisseLogger = LoggerFactory.getLogger(ApplicationRuntimeException.class.getName());

	public ApplicationRuntimeException(String msg) {
		super(msg);
		tendernisseLogger.error(msg);
	}

	public ApplicationRuntimeException(Exception exception) {
		super(exception);
		tendernisseLogger.error(exception.getMessage());
	}

	public ApplicationRuntimeException(String msg, Exception exception) {
		super(exception);
		tendernisseLogger.error(msg, exception);
	}

}