package org.eclipse.dltk.javascript.console;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.console.ScriptConsoleServer;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.javascript.core.JavaScriptNature;
import org.eclipse.dltk.javascript.launching.JavaScriptLaunchingPlugin;
import org.eclipse.dltk.launching.ScriptLaunchUtil;

public class JavaScriptConsoleUtil {

	public static void runDefaultTclInterpreter(
			JavaScriptInterpreter interpreter) throws CoreException,
			IOException {
		ScriptConsoleServer server = ScriptConsoleServer.getInstance();

		String id = server.register(interpreter);
		String port = Integer.toString(server.getPort());

		String[] args = new String[] { "127.0.0.1", port, id };

		// TODO: Add environments support
		IExecutionEnvironment exeEnv = (IExecutionEnvironment) EnvironmentManager
				.getLocalEnvironment().getAdapter(IExecutionEnvironment.class);
		IFileHandle scriptFile = JavaScriptLaunchingPlugin.getDefault()
				.getConsoleProxy(exeEnv);
		if (scriptFile != null) {
			ScriptLaunchUtil.runScript(JavaScriptNature.NATURE_ID, scriptFile,
					null, null, args, null);
		}
	}
}
