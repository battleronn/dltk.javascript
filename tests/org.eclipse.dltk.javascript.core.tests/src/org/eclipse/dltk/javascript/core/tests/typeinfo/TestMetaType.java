/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.javascript.core.tests.typeinfo;

import org.eclipse.dltk.javascript.typeinfo.DefaultMetaType;

public class TestMetaType extends DefaultMetaType {

	public static final TestMetaType INSTANCE = new TestMetaType();

	private TestMetaType() {
	}

	@Override
	public String getId() {
		return TestMetaType.class.getName() + ".INSTANCE";
	}

}
