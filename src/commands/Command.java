package commands;

import java.lang.annotation.*;

/**
 * Command annotates a command method in the CommandBank
 * @author Jon
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String command();

	String aliases();

	String description();

	boolean debug();
}