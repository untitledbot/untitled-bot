package dev.alexisok.untitledbot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Builds implementations of {@link ImmutableList}
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class ImmutableListBuilder<T> {
    
    private List<T> temp = new ArrayList<>();
    
    public ImmutableListBuilder() {
        
    }
    
    public ImmutableListBuilder(Collection<T> initial) {
        this.temp.addAll(initial);
    }
    
    public void add(T t) {
        this.temp.add(t);
    }
    
    public void removeIndex(int i) {
        this.temp.remove(i);
    }
    
    public void remove(T t) {
        this.temp.remove(t);
    }
    
    public ImmutableList<T> build() {
        return () -> new ArrayList<>(temp);
    }
    
}
