package net.chrisdolan.pcgen.drools.input;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("inventory")
public class InventoryInput {

    @XStreamImplicit(itemFieldName="item")
    private List<ItemInput> items;

    public List<ItemInput> getItems() {
        return items;
    }
    public void setItems(List<ItemInput> items) {
        this.items = items;
    }

    public static class ItemInput {

    }
}
