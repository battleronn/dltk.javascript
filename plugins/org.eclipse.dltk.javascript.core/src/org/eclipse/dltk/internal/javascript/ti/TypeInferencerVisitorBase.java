/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.javascript.ti;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.javascript.ast.ASTVisitor;
import org.eclipse.dltk.javascript.parser.JSProblemReporter;
import org.eclipse.dltk.javascript.typeinference.IValueCollection;
import org.eclipse.dltk.javascript.typeinference.IValueReference;
import org.eclipse.dltk.javascript.typeinfo.ITypeInferenceHandler;
import org.eclipse.dltk.javascript.typeinfo.ITypeInferenceHandlerFactory;
import org.eclipse.dltk.javascript.typeinfo.ITypeInferencerVisitor;
import org.eclipse.dltk.javascript.typeinfo.TypeInfoManager;

public abstract class TypeInferencerVisitorBase extends
		ASTVisitor<IValueReference> implements ITypeInferencerVisitor {

	protected final ITypeInferenceContext context;

	private Stack<IValueCollection> contexts = new Stack<IValueCollection>();

	public ITypeInferenceContext getContext() {
		return context;
	}

	public IValueCollection peekContext() {
		return !contexts.isEmpty() ? contexts.peek() : null;
	}

	public void enterContext(IValueCollection collection) {
		contexts.push(collection);
	}

	public IValueCollection leaveContext() {
		return contexts.pop();
	}

	protected boolean inFunction() {
		for (int i = contexts.size(); --i >= 0;) {
			if (contexts.get(i) instanceof FunctionValueCollection) {
				return true;
			}
		}
		return false;
	}

	public TypeInferencerVisitorBase(ITypeInferenceContext context) {
		this.context = context;
	}

	private ITypeInferenceHandler[] handlers = null;

	public void initialize() {
		handlers = null;
		contexts.clear();
		initialize0();
	}

	protected void initialize0() {
		contexts.push(new TopValueCollection(context));
		final List<ITypeInferenceHandler> handlers = new ArrayList<ITypeInferenceHandler>();
		for (ITypeInferenceHandlerFactory factory : TypeInfoManager
				.getNodeHandlerFactories()) {
			final ITypeInferenceHandler handler = factory.create(context, this);
			if (handler != null) {
				handlers.add(handler);
			}
		}
		if (!handlers.isEmpty()) {
			this.handlers = handlers.toArray(new ITypeInferenceHandler[handlers
					.size()]);
		} else {
			this.handlers = null;
		}
	}

	@Override
	public IValueReference visit(ASTNode node) {
		if (handlers != null) {
			for (ITypeInferenceHandler handler : handlers) {
				final IValueReference result = handler.handle(node);
				if (result != ITypeInferenceHandler.CONTINUE) {
					return result;
				}
			}
		}
		return super.visit(node);
	}

	public IValueCollection getCollection() {
		return contexts.get(0);
	}

	/**
	 * @param value1
	 * @param value2
	 * @return
	 */
	protected IValueReference merge(IValueReference value1,
			IValueReference value2) {
		final AnonymousValue reference = new AnonymousValue();
		reference.setValue(value1);
		reference.addValue(value2, false);
		return reference;
	}

	protected JSProblemReporter reporter;

	public JSProblemReporter getProblemReporter() {
		return reporter;
	}

	public void setProblemReporter(JSProblemReporter reporter) {
		this.reporter = reporter;
	}

}
