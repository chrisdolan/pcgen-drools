package net.chrisdolan.pcgen.drools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.chrisdolan.pcgen.drools.input.PC;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class PCReader {
    private boolean ignoreAllUnknowns = false;

    public PC read(File file) throws IOException {
        return read(file.getAbsoluteFile().toURI().toURL());
    }
    public PC read(URL url) throws IOException {
        try {
            XStream xstream = new XStream() {
                protected MapperWrapper wrapMapper(final MapperWrapper wrapped) {
                    return new MapperWrapper(wrapped) {
                        public String aliasForSystemAttribute(String attribute) {
                            // XStream treats attributes named "class" specially. So, I change the alias for the special
                            // attribute so I can use attributes named "class" in, e.g., the <level> element.
                            return "class".equals(attribute) ? "clazz" : wrapped.aliasForSystemAttribute(attribute);
                        }
                        public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn, String fieldName) {
                            // from http://stackoverflow.com/questions/4409717/java-xstream-ignore-tag-that-doesnt-exist-in-xml
                            // this drops unknown XML elements
                            if (ignoreAllUnknowns && definedIn == Object.class) {
                                return false;
                            }
                            return super.shouldSerializeMember(definedIn, fieldName);
                        }
                    };
                }
            };
            xstream.autodetectAnnotations(true);
            xstream.alias("pc", PC.class);
            Object o = xstream.fromXML(url);
            if (!(o instanceof PC))
                throw new IOException("Unmarshalled XML is not a PC, but is: " + o.getClass());
            PC pc = (PC) o;
            return pc;
        } catch (XStreamException e) {
            throw new IOException(e);
        }
    }

    public boolean ignoreAllUnknowns() {
        return ignoreAllUnknowns;
    }

    public void setIgnoreAllUnknowns(boolean ignoreAllUnknowns) {
        this.ignoreAllUnknowns = ignoreAllUnknowns;
    }

}
