package com.actionbarsherlock.internal;

import junit.framework.Assert;
import org.junit.Test;

import static com.actionbarsherlock.internal.ActionBarSherlockCompat.cleanActivityName;

public class ManifestParsingTest{
    @Test
    public void testFullyQualifiedClassName() {
        String expected = "com.other.package.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.other.package.SomeClass");
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void testFullyQualifiedClassNameSamePackage() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "com.jakewharton.test.SomeClass");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testUnqualifiedClassName() {
        String expected = "com.jakewharton.test.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", "SomeClass");
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void testRelativeClassName() {
        String expected = "com.jakewharton.test.ui.SomeClass";
        String actual = cleanActivityName("com.jakewharton.test", ".ui.SomeClass");
        Assert.assertEquals(expected, actual);
    }
}