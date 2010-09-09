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
package org.eclipse.dltk.internal.javascript.validation;

import org.eclipse.osgi.util.NLS;

public class ValidationMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.javascript.validation.ValidationMessages"; //$NON-NLS-1$
	public static String UnknownType;
	public static String DeprecatedType;

	public static String UndefinedMethodInScript;
	public static String UndefinedMethod;
	public static String MethodNotSelected;
	public static String MethodNotApplicable;
	public static String MethodNotApplicableInScript;
	public static String DeprecatedMethod;
	public static String DeprecatedFunction;

	public static String UndefinedProperty;
	public static String DeprecatedProperty;
	public static String DeprecatedPropertyOfInstance;
	public static String DeprecatedPropertyNoType;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ValidationMessages.class);
	}

	private ValidationMessages() {
	}
}
