package com.applicationslab.ayurvedictreatment;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;
import org.testng.annotations.Test;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public <InstrumentationTestCase> void useAppContext() {
        // Context of the app under test.

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.applicationslab.ayurvedictreatment", appContext.getPackageName());
    }
}
