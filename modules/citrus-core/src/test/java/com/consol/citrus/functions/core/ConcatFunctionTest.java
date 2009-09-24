/*
 * Copyright 2006-2009 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 *  Citrus is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Citrus is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Citrus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.functions.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.AbstractBaseTest;
import com.consol.citrus.exceptions.InvalidFunctionUsageException;
import com.consol.citrus.functions.core.ConcatFunction;

public class ConcatFunctionTest extends AbstractBaseTest {
    ConcatFunction function = new ConcatFunction();
    
    @Test
    public void testFunction() {
        List<String> params = new ArrayList<String>();
        params.add("Hallo ");
        params.add("TestFramework");
        params.add("!");
        
        Assert.assertEquals(function.execute(params), "Hallo TestFramework!");
    }
    
    @Test(expectedExceptions = {InvalidFunctionUsageException.class})
    public void testNoParameters() {
        function.execute(Collections.<String>emptyList());
    }
}
