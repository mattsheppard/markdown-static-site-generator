package com.kstruct.markdown.navigation;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface NavigationNode {

    public String getTitle();
    
    public URI getUri();
    
    public List<NavigationNode> getChildren();
    
    public Optional<NavigationNode> getParent();
    
}
