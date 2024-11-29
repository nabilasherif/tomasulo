package Core.Storage;

//for cache and memory bas i'm thinking of separating
//aslun nekhaly el cache keda simply wala ne add tag wala ehh
// laeen el mafroud law keda yeba address final w el tag howa el ye3rafna if this is what we are looking for
//we need to dicuss and ask about the memory and cache

public class StorageEntry {
    private Integer address;
    private Object value;

    public StorageEntry(Integer address, Object value) {
        this.address = address;
        this.value = value;
    }
    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
