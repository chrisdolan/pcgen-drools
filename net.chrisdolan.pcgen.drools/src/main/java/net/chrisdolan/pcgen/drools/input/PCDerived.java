package net.chrisdolan.pcgen.drools.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("derived")
public class PCDerived {
    @XStreamImplicit(itemFieldName="property")
    private List<PropertyInput> properties = new ArrayList<PropertyInput>();

    public List<PropertyInput> getProperties() {
        return properties == null ? Collections.<PropertyInput>emptyList() : new ArrayList<PropertyInput>(properties);
    }
    public void setProperties(List<PropertyInput> properties) {
        this.properties = new ArrayList<PropertyInput>(properties);
    }

    @XStreamImplicit()
    public Map<String,Object> elements = new HashMap<String,Object>();
}
