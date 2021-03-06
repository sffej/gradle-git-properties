package com.gorylenko

import org.junit.Before
import static org.junit.Assert.*

import org.junit.Test

class PropertiesFileWriterTest {

    PropertiesFileWriter writer

    @Before
    public void setUp() throws Exception {
        writer = new PropertiesFileWriter()
    }

    @Test
    public void testWriteNormalProperties() {

        File file = File.createTempFile("temp",".tmp")
        writer.write([greeting: 'Hello'], file, true)

        Properties props = loadProperties(file)
        assertEquals(props, [greeting: 'Hello'])
    }

    @Test
    public void testWritingSameContent() {

        // Set up a Properties file with greeting=Hello
        File file = File.createTempFile("temp",".tmp")
        Properties props = new Properties()
        props.setProperty('greeting', 'Hello')
        file.withOutputStream {
            props.store(it, null)
        }
        long lastModified = file.lastModified()

        // Write to same file with same content (force=false)
        writer.write([greeting: 'Hello'], file, false)

        // Make sure the file lastModified not changed
        assertEquals(lastModified, file.lastModified())

        // Write to same file with same content (force=true)
        writer.write([greeting: 'Hello'], file, true)

        // Make sure the file lastModified changed
        assertNotEquals(lastModified, file.lastModified())
    }


    @Test
    public void testWritingDifferentContent() {

        // write a Properties file with greeting=Hello
        File file = File.createTempFile("temp",".tmp")
        Properties props = new Properties()
        props.setProperty('greeting', 'Hello')
        file.withOutputStream {
            props.store(it, null)
        }
        long lastModified = file.lastModified()

        // Try to write to same file with different content
        writer.write([greeting: 'Hello2'], file, false)

        // Make sure the file lastModified changed
        assertNotEquals(lastModified, file.lastModified())
        // Make sure content is updated
        assertEquals(loadProperties(file), [greeting: 'Hello2'])
    }

    private Properties loadProperties(File file) {
        def props = new Properties()
        file.withInputStream {
            props.load it
        }
        return props
    }


    @Test
    public void testWriteNormalPropertiesResultMustBeSorted() {

        File file = File.createTempFile("temp",".tmp")
        writer.write([greeting_3: 'Hello', greeting_1: 'Hello', greeting_2: 'Hello', greeting_4: 'Hello'], file, true)

        String result = file.text
        int index1 = result.indexOf("greeting_1")
        int index2 = result.indexOf("greeting_2")
        int index3 = result.indexOf("greeting_3")
        int index4 = result.indexOf("greeting_4")

        assertTrue(index1 < index2)
        assertTrue(index2 < index3)
        assertTrue(index3 < index4)
    }
}
