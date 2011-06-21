/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.javascript.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.builder.ISourceLineTracker;

public class Reporter extends LineTracker implements IProblemReporter,
		JSProblemReporter {

	private final IProblemReporter problemReporter;

	private IProblemIdentifier id;
	private String message;
	private int line;
	private int start;
	private int end;
	private ProblemSeverity severity;

	private int problemCount;

	private final ISeverityReporter severityReporter;

	public Reporter(ISourceLineTracker lineTracker,
			IProblemReporter problemReporter) {
		this(lineTracker, problemReporter,
				new JavaScriptParserSeverityReporter());
	}

	public Reporter(ISourceLineTracker lineTracker,
			IProblemReporter problemReporter, ISeverityReporter severityReporter) {
		super(lineTracker);
		this.problemReporter = problemReporter;
		this.severityReporter = severityReporter;
		reset();
	}

	private void reset() {
		severity = ProblemSeverity.WARNING;
		id = null;
		message = null;
		line = -1;
		start = -1;
		end = -1;
	}

	public void report() {
		if (problemReporter != null && message != null) {
			reportProblem(createProblem());
		}
		reset();
	}

	private IProblem createProblem() {
		if (line == -1 && start != -1) {
			line = getLineNumberOfOffset(start);
		}
		if (line > getNumberOfLines() && start >= 0 && start <= getLength()) {
			line = getLineNumberOfOffset(start);
		}

		if (severityReporter != null) {
			severity = severityReporter.getSeverity(id, severity);
			if (severity == null)
				return null;
		}

		return new DefaultProblem(message, id, null, severity, start, end, line);
	}

	public void setFormattedMessage(JSProblemIdentifier id, Object... args) {
		this.id = id;
		this.message = id.formatMessage(args);
	}

	public void setMessage(JSProblemIdentifier id) {
		this.id = id;
		this.message = id.getMessage();
	}

	public void setMessage(IProblemIdentifier id, String message) {
		this.id = id;
		this.message = message;
	}

	public IProblemIdentifier getId() {
		return id;
	}

	public void setId(IProblemIdentifier id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public ProblemSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(ProblemSeverity severity) {
		this.severity = severity;
	}

	public void setRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public void reportProblem(IProblem problem) {
		if (problemReporter != null && problem != null) {
			problemCount++;
			problemReporter.reportProblem(problem);
		}
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public void reportProblem(IProblemIdentifier identifier, String message,
			int start, int end) {
		if (isSuppressed(identifier)) {
			return;
		}
		reportProblem(ProblemSeverity.WARNING, identifier, message, start, end);
	}

	public void reportProblem(ProblemSeverity severity, IProblemIdentifier id,
			String message, int start, int end) {
		if (isSuppressed(id)) {
			return;
		}
		ProblemSeverity sev = severity;
		if (severityReporter != null) {
			sev = severityReporter.getSeverity(id, sev);
			if (sev == null || sev == ProblemSeverity.IGNORE)
				return;
		}

		reportProblem(new DefaultProblem(message, id, null, sev, start, end,
				getLineNumberOfOffset(start)));
	}

	public int getProblemCount() {
		return problemCount;
	}

	private boolean isSuppressed(IProblemIdentifier identifier) {
		return suppressed != null && suppressed.contains(identifier);
	}

	private Set<IProblemIdentifier> suppressed;

	public void suppressProblems(IProblemIdentifier... identifiers) {
		if (suppressed == null) {
			suppressed = new HashSet<IProblemIdentifier>();
		}
		Collections.addAll(suppressed, identifiers);
	}

	private Stack<Set<IProblemIdentifier>> suppressedStack = null;

	public void pushSuppressWarnings(Collection<IProblemIdentifier> suppressed) {
		if (suppressedStack == null) {
			suppressedStack = new Stack<Set<IProblemIdentifier>>();
		}
		suppressedStack.push(this.suppressed);
		if (this.suppressed == null) {
			this.suppressed = new HashSet<IProblemIdentifier>();
		} else {
			this.suppressed = new HashSet<IProblemIdentifier>(this.suppressed);
		}
		this.suppressed.addAll(suppressed);
	}

	public void popSuppressWarnings() {
		if (suppressedStack != null && !suppressedStack.isEmpty()) {
			suppressed = suppressedStack.pop();
		}
	}

	public Set<IProblemIdentifier> getSuppressWarnings() {
		return suppressed;
	}

	public void setSuppressWarnings(Set<IProblemIdentifier> sup) {
		suppressed = sup;
	}

}
